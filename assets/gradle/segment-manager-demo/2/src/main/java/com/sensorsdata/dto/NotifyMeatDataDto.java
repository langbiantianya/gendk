package com.sensorsdata.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotifyMeatDataDto {
    private String title;
    private String content;
    private List<Long> template;
}
