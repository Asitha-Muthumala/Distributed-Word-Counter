package com.example.Procedure.util;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParagraphMessage {
    private String processId;
    private String paragraph;
    private List<NodeRange> ranges;
}
