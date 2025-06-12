package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

import java.util.List;

@Data
public class Account {
    private String username;
    private String user_cname;
    private long id;
    private String email;
    private String phone;
    private String position;
    private String password;
    private boolean is_global;
    private boolean expire_time;
    private boolean disabled;
    private boolean is_deleted;
    private String desc;
    private List<ExtraAccountBinding> extra_account_bindings;
}
