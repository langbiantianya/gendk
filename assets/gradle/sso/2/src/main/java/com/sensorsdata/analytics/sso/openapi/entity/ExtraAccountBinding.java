package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

@Data
public class ExtraAccountBinding {
    private String attr_code;
    private String attr_value;
    private long user_id;
    private String username;
    private String attr_name;
    private long project_id;
    private boolean has_children_node;
}
