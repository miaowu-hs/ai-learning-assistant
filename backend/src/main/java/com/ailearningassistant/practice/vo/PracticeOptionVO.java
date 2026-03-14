package com.ailearningassistant.practice.vo;

import com.ailearningassistant.practice.model.PracticeOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeOptionVO {

    private String key;
    private String content;

    public static PracticeOptionVO fromModel(PracticeOption option) {
        return PracticeOptionVO.builder()
                .key(option.getKey())
                .content(option.getContent())
                .build();
    }
}
