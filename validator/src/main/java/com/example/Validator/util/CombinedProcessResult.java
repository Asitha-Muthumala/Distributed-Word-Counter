package com.example.Validator.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedProcessResult  {
    private String processId;
    private List<WordCountResult> nodeResults;
}
