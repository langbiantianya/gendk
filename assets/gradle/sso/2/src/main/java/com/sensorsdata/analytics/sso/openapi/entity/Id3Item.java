package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

@Data
public class Id3Item {
    private long user_id;
    private String attr_code;
    private String node_code;
    private String node_name;
    private boolean has_children_node;
    private String full_name;
    private String desc;
}
