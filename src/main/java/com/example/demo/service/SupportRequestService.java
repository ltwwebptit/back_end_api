package com.example.demo.service;

import com.example.demo.model.dto.SupportRequestDTO;
import java.util.List;

public interface SupportRequestService {
    SupportRequestDTO create(SupportRequestDTO dto);
    SupportRequestDTO update(SupportRequestDTO dto);
    void delete(Integer id);
    SupportRequestDTO findById(Integer id);
    List<SupportRequestDTO> findAll();
    void replyToSupportRequest(Integer requestId, com.example.demo.model.dto.SupportRequestReplyDTO replyDTO);
}
