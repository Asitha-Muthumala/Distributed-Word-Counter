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
public class WordCountResult {
    private String processId;
    private char start;
    private char end;
    private int count;
    private List<String> words;
}