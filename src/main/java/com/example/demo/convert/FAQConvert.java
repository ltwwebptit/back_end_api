package com.example.demo.convert;

import com.example.demo.entity.FAQEntity;
import com.example.demo.model.dto.FAQDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FAQConvert {
    private final ModelMapper modelMapper;
    public FAQEntity toFAQEntity(FAQDTO faqDTO) {
        FAQEntity faqEntity = modelMapper.map(faqDTO, FAQEntity.class);
        return faqEntity;
    }

    public FAQDTO toFAQDTO(FAQEntity faqEntity) {
        FAQDTO faqDTO = modelMapper.map(faqEntity, FAQDTO.class);
        return faqDTO;
    }
}
