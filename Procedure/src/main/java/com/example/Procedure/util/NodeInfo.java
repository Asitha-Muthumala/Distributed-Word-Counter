package com.example.Procedure.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeInfo {
    private String nodeId;
    private String weight;
    private long lastHeartbeat;
}
