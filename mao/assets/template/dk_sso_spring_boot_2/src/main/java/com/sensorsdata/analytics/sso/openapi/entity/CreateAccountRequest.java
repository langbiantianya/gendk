package com.sensorsdata.analytics.sso.openapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateAccountRequest {
    private CreateAccount account;
    List<Integer> related_role_ids;

    @Data
    @AllArgsConstructor
    public static class CreateAccount {
        private String username;
        private Boolean is_global;
    }
}
