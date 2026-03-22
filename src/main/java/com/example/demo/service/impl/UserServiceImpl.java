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
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final OTPService otpService;
    @Override
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
            String tokenRegister = UUID.randomUUID().toString();
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            String link = "<a href='http://localhost:3000/accept_account?token=" + tokenRegister + "'>Tại đây</a>";
            mailService.sendEmailWithToken(register.getEmail(),link);
            UsersEntity saved = userRepository.save(users);

            TokenRegisterEntity tokenRegisterEntity = new TokenRegisterEntity();
            tokenRegisterEntity.setToken(tokenRegister);
            tokenRegisterEntity.setExpirationDate(new java.sql.Date(System.currentTimeMillis() + oneDayInMillis));
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
        boolean is2fa = !usersEntity.isTwoFactorEnabled();
        loginResponse.setToken(jwtUtilsToken.generateToken(usersEntity,is2fa));
        loginResponse.setRole(usersEntity.getRolename());
        if(!is2fa){
            String otpcode = String.format("06%d",new java.util.Random().nextInt(999999));
            otpService.saveOtp(usersEntity.getUsername(), otpcode);
            try {
                mailService.sendEmailWithToken(usersEntity.getEmail(),"Mã xác thực 2 bước của bạn là: " + otpcode + ". Mã này có hiệu lực trong 5 phút.");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
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
            tokenEntity.setExpirationDate(new java.sql.Date(System.currentTimeMillis() + oneDayInMillis));
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
        return List.of();
    }

    @Override
    public String verifyOtp(String tempToken, String otpCode) {
        String username = jwtUtilsToken.extractUsernameFromToken(tempToken);
        UsersEntity userEntity = userRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String validOtp = otpService.getOtp(username);

        if (validOtp == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn hoặc không tồn tại");
        }

        if (!otpCode.equals(validOtp)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mã OTP không chính xác");
        }

        otpService.deleteOtp(username);

        return jwtUtilsToken.generateToken(userEntity, true);
    }
}
