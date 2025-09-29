package com.sensorsdata.constant;

/**
 * @author ：wutengfei
 * @description：通用参数
 * @date ：2024-10-08 17:53
 */
public class CommonConstants {

  public final static String SUCCESS = "SUCCESS";

  public static class SFConstants{
    private final static String SF_OPEN_API_PRE_URL = "/api/v3/focus/v1";
    private final static String SF_WEB_API_PRE_URL = "/api/v2";
    private final static String GET_PLAN_LIST = "/web/plan/list";
    private final static String GET_CANVAS_LIST = "/web/canvas/list";

    private final static String GET_CANVAS_TEMPLATE_LIST = "/sf/plan_template/canvas/list";
    private final static String GET_PLAN_TEMPLATE_LIST = "/sf/plan_template/simple/list";

    public static String getPlanListUrl(){
      return SF_OPEN_API_PRE_URL + GET_PLAN_LIST;
    }
    public static String getCanvasList(){
      return SF_OPEN_API_PRE_URL + GET_CANVAS_LIST;
    }
    public static String getCanvasTemplateList(){
      return SF_WEB_API_PRE_URL + GET_CANVAS_TEMPLATE_LIST;
    }
    public static String getPlanTemplateList(){
      return SF_WEB_API_PRE_URL + GET_PLAN_TEMPLATE_LIST;
    }
  }

  public static class SbpConstants{
    private final static String SBP_OPEN_API_PRE_URL = "/api/v3/portal/v2";
    private final static String GET_ACCOUNT_BY_ID = "/identity/account/get";
    private final static String GET_ACCOUNT_BY_NAME = "/identity/account/get-by-name";
    private final static String GET_ROLE = "/identity/role/get";
    private final static String ADD_ROLE = "/identity/role/add";
    private final static String UPDATE_ROLE = "/identity/role/update";
    private final static String REGISTER_RESOURCE = "/identity/resource/register";
    private final static String DELETE_RESOURCE = "/identity/resource/delete";
    private final static String REGISTER_CATEGORY = "/identity/resource/category/register";
    private final static String DELETE_CATEGORY = "/identity/resource/category/delete";
    private final static String BIND_RESOURCE_CATEGORY = "/identity/resource/category/bind";

    public static String getAccountById(){
      return SBP_OPEN_API_PRE_URL + GET_ACCOUNT_BY_ID;
    }

    public static String getAccountByName(){
      return SBP_OPEN_API_PRE_URL + GET_ACCOUNT_BY_NAME;
    }
    public static String getRoleById(){
      return SBP_OPEN_API_PRE_URL + GET_ROLE;
    }
    public static String getAddRole(){
      return SBP_OPEN_API_PRE_URL + ADD_ROLE;
    }
    public static String getUpdateRole(){
      return SBP_OPEN_API_PRE_URL + UPDATE_ROLE;
    }
    public static String getRegisterResource(){
      return SBP_OPEN_API_PRE_URL + REGISTER_RESOURCE;
    }
    public static String getDeleteResource(){
      return SBP_OPEN_API_PRE_URL + DELETE_RESOURCE;
    }
    public static String getRegisterResourceCategory(){
      return SBP_OPEN_API_PRE_URL + REGISTER_CATEGORY;
    }
    public static String getDeleteResourceCategory(){
      return SBP_OPEN_API_PRE_URL + DELETE_CATEGORY;
    }
    public static String getBindResourceCategory(){
      return SBP_OPEN_API_PRE_URL + BIND_RESOURCE_CATEGORY;

    }
  }

  public static class SaConstants{
    private final static String SA_OPEN_API_PRE_URL = "/api/v3/analytics/v1";
    private final static String SA_WEB_API_PRE_URL = "/api/v2";
    private final static String GET_USER_LIST = "/model/user/list";
    private final static String ASYNC_SUBMIT = "/async_download/submit";
    private final static String ASYNC_SUBMIT_GET_REQUEST_ID = "/async_download/request_id";
    private final static String GET_USER_LOOK_UP = "/sa/user_lookup/report";
    public static String getGetUserList(){
      return SA_OPEN_API_PRE_URL + GET_USER_LIST;
    }
    public static String getAsyncSubmit(){
      return SA_WEB_API_PRE_URL + ASYNC_SUBMIT;
    }

    public static String getAsyncSubmitGetRequestId(){
      return SA_WEB_API_PRE_URL + ASYNC_SUBMIT_GET_REQUEST_ID;
    }

    public static String getGetUserLookUp(){
      return SA_WEB_API_PRE_URL + GET_USER_LOOK_UP;
    }

  }

}
