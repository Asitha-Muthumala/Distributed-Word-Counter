package com.example.Consumer1.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphMessage {
    private String processId;
    private String paragraph;
    private List<NodeRange> ranges;
}
