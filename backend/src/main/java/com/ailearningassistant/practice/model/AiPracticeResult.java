package com.ailearningassistant.practice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPracticeResult {

    private String title;
    private List<GeneratedPracticeQuestion> questions;
}
