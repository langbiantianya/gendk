package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

import java.util.List;

@Data
public class AccountData {
    private List<AccountItem> accounts;
    private long totalCount;
    private List<ExtraAttribute> extraAttributes;
}

