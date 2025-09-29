package com.sensorsdata.client.model;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

/**
 * @author ：wutengfei
 * @description：用户明细请求参数
 * @date ：2024-12-06 14:48
 */
@Data
public class UserLookUpRequest {

  @Alias("search_way")
  String searchWay;

  @Alias("subject_id")
  String subjectId;

  Filter filter;

  @Alias("user_group_filter")
  UserGroupFilter userGroupFilter;

  @Alias("use_cache")
  boolean useCache;

  Integer limit;

  Integer page;

  @Alias("num_per_page")
  Integer numPerPage;

  List<String> profiles;

  @Alias("sub_task_type")
  String subTaskType;

  String jumpURL;

  @Alias("all_page")
  boolean allPage;

  @Alias("sort_by_field")
  String sortByField;

}
