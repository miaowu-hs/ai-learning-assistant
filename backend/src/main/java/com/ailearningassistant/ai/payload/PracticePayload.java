package com.ailearningassistant.ai.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticePayload {

    private String title;
    private List<PracticeQuestionPayload> questions;
}
