package com.sensorsdata.analytics.sso.openapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RelatedRole {
    private String name;
    private String cname;
    private long id;
    private String description;
    private String create_user_cname;
    private String create_time;
    private String update_time;
    private String update_user;
    private boolean distributable;
    private String type_name;
    private boolean enable_copy;
    private boolean enable_edit;
    private boolean enable_delete;
    private boolean enable_list_member;
    private boolean enable_detail;
    private boolean enable_edit_member;
    private String tag;
    private Attribute attribute;
    private String attr_code;
    private String attr_name;
    private String attr_type;
    private String create_user;
    private boolean is_project_customize_default_role;
    private String role_type;

    @Data
    public static class Attribute {
        @JsonProperty("non_")
        private List<NonItem> nonList;
        @JsonProperty("mollit1_")
        private List<Mollit1Item> mollit1List;
        @JsonProperty("adipisicing_47c")
        private List<Adipisicing47cItem> adipisicing47cList;
        @JsonProperty("non_4dc")
        private List<Non4dcItem> non4dcList;
        @JsonProperty("occaecatd")
        private List<OccaecatdItem> occaecatdList;
        @JsonProperty("do_3c")
        private List<Do3cItem> do3cList;
        @JsonProperty("tempor8ae")
        private List<Tempor8aeItem> tempor8aeList;
        @JsonProperty("laborum_0")
        private List<Laborum0Item> laborum0List;
    }

    @Data
    public static class NonItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Mollit1Item {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Adipisicing47cItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Non4dcItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class OccaecatdItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Do3cItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Tempor8aeItem {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }

    @Data
    public static class Laborum0Item {
        private long user_id;
        private String attr_code;
        private String node_code;
        private String node_name;
        private boolean has_children_node;
        private String full_name;
        private String desc;
    }
}
