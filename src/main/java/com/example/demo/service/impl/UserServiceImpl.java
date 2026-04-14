package com.example.demo.service.impl;

import com.example.demo.convert.UserConvert;
import com.example.demo.entity.TokenRegisterEntity;
import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.LoginDTO;
import com.example.demo.model.dto.RegisterDTO;
import com.example.demo.model.dto.UpdatePassword;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.response.LoginResponse;
import com.example.demo.repository.TokenRegisterRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MailService;
import com.example.demo.service.OTPService;
import com.example.demo.service.UserService;
import com.example.demo.utils.ExtarctUserUtils;
import com.example.demo.utils.JwtUtilsToken;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConvert  userConvert;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilsToken jwtUtilsToken;
    private final MailService  mailService;
    private final TokenRegisterRepository tokenRegisterRepository;
    private final ExtarctUserUtils  extarctUserUtils;

    @Override
    public void forgotPassword(String email) {
        Optional<UsersEntity> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email không tồn tại trong hệ thống. Vui lòng kiểm tra lại.");
        }
        UsersEntity userEntity = user.get();
        String newPassword = String.format("%06d", new java.util.Random().nextInt(999999));
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        try {
            mailService.sendEmailWithToken(email, "Mật khẩu khôi phục tạm thời của bạn là: " + newPassword + ". Vui lòng đăng nhập và tiến hành đổi mật khẩu ngay lập tức ở mục Hồ Sơ.");
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể gửi email.");
        }
    }

    @Override
    @Transactional
    public void register(RegisterDTO register) {
        if(userRepository.existsByUsername(register.getUsername())) {
            throw  new ResponseStatusException(HttpStatus.CONFLICT ,"Username is already in use");
        }
        if (userRepository.existsByEmail(register.getEmail())) {
            throw  new ResponseStatusException(HttpStatus.CONFLICT ,"Email is already in use");
        }
        try{
            UsersEntity users = userConvert.toUserEntity(register);
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setCreatedAt(new java.util.Date());
            String tokenRegister = UUID.randomUUID().toString();
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            String link = "<a href='http://localhost:3000/accept_account?token=" + tokenRegister + "'>Tại đây</a>";
            mailService.sendEmailWithToken(register.getEmail(),link);
            UsersEntity saved = userRepository.save(users);

            TokenRegisterEntity tokenRegisterEntity = new TokenRegisterEntity();
            tokenRegisterEntity.setToken(tokenRegister);
            tokenRegisterEntity.setExpirationDate(new Date(System.currentTimeMillis() + oneDayInMillis));
            tokenRegisterEntity.setUser(saved);
            tokenRegisterRepository.save(tokenRegisterEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public LoginResponse login(LoginDTO login) {
        Optional<UsersEntity> user = userRepository.findByUsernameAndStatus(login.getUsername(),1);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or password is incorrect");
        }
        UsersEntity usersEntity = user.get();
        if(!passwordEncoder.matches(login.getPassword(),usersEntity.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or password is incorrect");
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword(), usersEntity.getAuthorities());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtUtilsToken.generateToken(usersEntity));
        loginResponse.setRole(usersEntity.getRolename());
        return loginResponse;
    }

    @Override
    public void updatePassword(UpdatePassword updatePassword) {
        Optional<UsersEntity> user = userRepository.findByUsernameAndStatus(updatePassword.getUsername(), 1);
        if(user.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + updatePassword.getUsername() + " not found");
        }
        UsersEntity userEntity = user.get();
        if(!passwordEncoder.matches(updatePassword.getPassword(), userEntity.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
        userEntity.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void acceptAccount(String token) {
        TokenRegisterEntity tokenRegisterEntity = tokenRegisterRepository.findByToken(token);
        if(tokenRegisterEntity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token not found");
        }
        if (tokenRegisterEntity.getExpirationDate().before(new Date())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token is expired");
        }
        UsersEntity users = tokenRegisterEntity.getUser();
        users.setStatus(1);
        userRepository.save(users);
    }

    @Override
    @Transactional
    public void resendToken(String email) {
        Optional<UsersEntity> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        UsersEntity userEntity = user.get();
        boolean valid = tokenRegisterRepository.existsValidTokenByUserId(
                userEntity.getId(),
                new java.sql.Date(System.currentTimeMillis()));
        if(valid){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token has been sent, please check again.");
        }
        try{
            String token = UUID.randomUUID().toString();
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            String link = "<a href='https://vietmind.ai4life.com.vn/accept_account?token=" + token + "'>Tại đây</a>";
            mailService.sendEmailWithToken(email, link);
            TokenRegisterEntity tokenEntity = new TokenRegisterEntity();
            tokenEntity.setToken(token);
            tokenEntity.setUser(userEntity);
            tokenEntity.setExpirationDate(new Date(System.currentTimeMillis() + oneDayInMillis));
            tokenRegisterRepository.save(tokenEntity);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public UsersEntity getProfile(HttpServletRequest request) {
        UsersEntity user = extarctUserUtils.extract(request);
        return user;
    }

    @Override
    public List<UserDTO> getUsers(HttpServletRequest request) {
        return userRepository.findAll().stream()
                .map(userConvert::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserRole(Integer id, String role) {
        UsersEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        user.setRolename(role);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public java.util.List<UserDTO> getRecentRegisteredUsers() {
        return userRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
                .limit(4)
                .map(userConvert::toUserDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<UserDTO> getMonthlyRegisteredUsers(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);
        return userRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .stream().map(userConvert::toUserDTO).collect(Collectors.toList());
    }
}
