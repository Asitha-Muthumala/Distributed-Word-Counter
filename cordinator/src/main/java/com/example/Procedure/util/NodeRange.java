package com.example.Procedure.util;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeRange {
    private String nodeId;
    private char start;
    private char end;
}
