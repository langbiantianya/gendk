package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

@Data
public class ExtraAttribute {
    private String attr_code;
    private String name;
    private String desc;
    private boolean enabled;
    private String type;
}
