package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.Data;

import java.util.List;

@Data
public class AccountItem {
    private Account account;
    private List<RelatedRole> relatedRoles;
    private Summary summary;
    private RelatedExtraAttributes relatedExtraAttributes;
}
