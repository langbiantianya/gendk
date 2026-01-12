package com.sensorsdata.analytics.sso.openapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RelatedExtraAttributes {
    @JsonProperty("occaecat_6")
    private List<Occaecat6Item> occaecat6List;
    @JsonProperty("id3")
    private List<Id3Item> id3List;
}
