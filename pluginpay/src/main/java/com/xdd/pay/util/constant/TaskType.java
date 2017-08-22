package com.xdd.pay.util.constant;

public class TaskType {
    public static final int DO_SMS_TASK = 1; //处理短信内容任务
    
    public static final int DO_HTTP_TASK = 2; //处理http任务
    
    public static final int SUCCESS = 1; //成功
    
    public static final int FAIL = 2; //失败
    
    public static final int PAY_SUCCESS = 3; //失败
    public static final int PAY_FAIL = 4; //支付失败
    /*http type  1:get;2:post*/
    public static final int HTTP_GET_TYPE = 1;
    public static final int HTTP_POST_TYPE =2;
    public static final String SYNCTASK = "1";//同步任务
    public static final String ASYNCTASK = "2";//异步任务
}