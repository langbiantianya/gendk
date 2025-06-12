package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

@Data
public class Summary {
    private long last_days;
    private long active_days;
    private String last_visit_time;
}
