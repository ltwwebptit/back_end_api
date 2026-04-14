package com.example.demo.controller;

import com.example.demo.repository.FAQRepository;
import com.example.demo.repository.LegalDocumentRepository;
import com.example.demo.repository.SupportRequestRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final UserRepository userRepository;
    private final LegalDocumentRepository legalDocumentRepository;
    private final FAQRepository faqRepository;
    private final SupportRequestRepository supportRequestRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("Received request for dashboard stats");
        try {
            Map<String, Object> stats = new HashMap<>();

        
            java.util.List<com.example.demo.entity.UsersEntity> allUsers = userRepository.findAll();
            boolean needsUpdate = false;
            for (com.example.demo.entity.UsersEntity u : allUsers) {
                if (u.getCreatedAt() == null) {
                    u.setCreatedAt(new java.util.Date());
                    userRepository.save(u);
                    needsUpdate = true;
                }
            }
            if (needsUpdate) {
                log.info("Đã tự động bổ sung ngày gia nhập cho người dùng bị thiếu dữ liệu");
            }

            long totalUsers = allUsers.size();
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = LocalDate.now().withDayOfMonth(
                    LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
            long newUsersThisMonth = userRepository
                    .findByCreatedAtBetweenOrderByCreatedAtDesc(monthStart, monthEnd)
                    .size();
            LocalDateTime weekStart = LocalDate.now().minusDays(6).atStartOfDay();
            long newUsersThisWeek = userRepository
                    .findByCreatedAtBetweenOrderByCreatedAtDesc(weekStart, LocalDateTime.now())
                    .size();
            long totalAdmins = userRepository.findByRolename("ADMIN").size();

            long totalDocuments = legalDocumentRepository.count();
            long activeDocuments = legalDocumentRepository.findAll().stream()
                    .filter(d -> Boolean.TRUE.equals(d.getStatus())).count();

            long totalFaqs = faqRepository.count();

            long totalSupportRequests = supportRequestRepository.count();
            long pendingSupportRequests = supportRequestRepository.findAll().stream()
                    .filter(s -> s.getStatus() == null || s.getStatus() == 0).count();
            long resolvedSupportRequests = supportRequestRepository.findAll().stream()
                    .filter(s -> s.getStatus() != null && s.getStatus() == 2).count();

            stats.put("totalUsers", totalUsers);
            stats.put("newUsersThisMonth", newUsersThisMonth);
            stats.put("newUsersThisWeek", newUsersThisWeek);
            stats.put("totalAdmins", totalAdmins);
            stats.put("totalDocuments", totalDocuments);
            stats.put("activeDocuments", activeDocuments);
            stats.put("totalFaqs", totalFaqs);
            stats.put("totalSupportRequests", totalSupportRequests);
            stats.put("pendingSupportRequests", pendingSupportRequests);
            stats.put("resolvedSupportRequests", resolvedSupportRequests);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: ", e);
            throw e;
        }
    }
}
