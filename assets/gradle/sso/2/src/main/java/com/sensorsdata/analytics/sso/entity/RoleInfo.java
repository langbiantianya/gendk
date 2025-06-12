package com.sensorsdata.analytics.sso.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleInfo {
    private Integer id;
    private String name;
    private Integer projectId;

}