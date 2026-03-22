package com.example.demo.service.impl;

import com.example.demo.convert.SupportRequestConvert;
import com.example.demo.entity.SupportRequestsEntity;
import com.example.demo.model.dto.SupportRequestDTO;
import com.example.demo.repository.SupportRequestRepository;
import com.example.demo.service.SupportRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final SupportRequestConvert supportRequestConvert;
    private final com.example.demo.service.MailService mailService;

    @Override
    public SupportRequestDTO create(SupportRequestDTO dto) {
        log.info("Creating new Support Request: {}", dto.getSubject());
        SupportRequestsEntity entity = supportRequestConvert.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        if (entity.getStatus() == null) {
            entity.setStatus(0); 
        }
        entity = supportRequestRepository.save(entity);
        log.info("Successfully created Support Request with id: {}", entity.getId());
        return supportRequestConvert.toDTO(entity);
    }

    @Override
    public SupportRequestDTO update(SupportRequestDTO dto) {
        log.info("Updating Support Request with id: {}", dto.getId());
        SupportRequestsEntity existingEntity = supportRequestRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Support Request update failed: not found with id: {}", dto.getId());
                    return new RuntimeException("Support Request not found");
                });

        existingEntity.setSubject(dto.getSubject());
        existingEntity.setMessage(dto.getMessage());
        existingEntity.setStatus(dto.getStatus());
        
        if (dto.getUserId() != null) {
            com.example.demo.entity.UsersEntity user = new com.example.demo.entity.UsersEntity();
            user.setId(dto.getUserId());
            existingEntity.setUser(user);
        }

        existingEntity = supportRequestRepository.save(existingEntity);
        log.info("Successfully updated Support Request with id: {}", existingEntity.getId());
        return supportRequestConvert.toDTO(existingEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting Support Request with id: {}", id);
        try {
            supportRequestRepository.deleteById(id);
            log.info("Successfully deleted Support Request with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting Support Request with id: {}", id, e);
            throw new RuntimeException("Error deleting Support Request", e);
        }
    }

    @Override
    public SupportRequestDTO findById(Integer id) {
        log.info("Fetching Support Request with id: {}", id);
        return supportRequestRepository.findById(id)
                .map(supportRequestConvert::toDTO)
                .orElseThrow(() -> {
                    log.error("Support Request fetch failed: not found with id: {}", id);
                    return new RuntimeException("Support Request not found");
                });
    }

    @Override
    public List<SupportRequestDTO> findAll() {
        log.info("Fetching all Support Requests");
        List<SupportRequestDTO> list = supportRequestRepository.findAll().stream()
                .map(supportRequestConvert::toDTO)
                .collect(Collectors.toList());
        log.info("Found {} Support Requests", list.size());
        return list;
    }

    @Override
    public void replyToSupportRequest(Integer requestId, com.example.demo.model.dto.SupportRequestReplyDTO replyDTO) {
        SupportRequestsEntity request = supportRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Support Request not found"));

        if (request.getUser() == null || request.getUser().getEmail() == null) {
            throw new RuntimeException("Email of the user not found");
        }

        String userEmail = request.getUser().getEmail();
        String subject = "Reply from Admin to: " + request.getSubject();
        
        String emailContent = "<h3>Hello " + (request.getUser().getFullname() != null ? request.getUser().getFullname() : "User") + ",</h3>" +
                              "<p>We received your request: <i>" + request.getMessage() + "</i></p>" +
                              "<p><b>Admin Reply:</b></p>" +
                              "<p>" + replyDTO.getReplyMessage() + "</p>" +
                              "<br><p>Thank you!</p>";

        try {
            mailService.sendReplyEmail(userEmail, subject, emailContent);
        } catch (Exception e) {
            log.error("Failed to send reply email to {}", userEmail, e);
            throw new RuntimeException("Error sending mail", e);
        }

        request.setStatus(1); // 1 = closed/replied
        supportRequestRepository.save(request);
        log.info("Replied to Support Request {} and sent email to {}", requestId, userEmail);
    }
}
