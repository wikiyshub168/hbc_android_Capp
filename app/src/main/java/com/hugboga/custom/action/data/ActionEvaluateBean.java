package com.hugboga.custom.action.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhangqiang on 17/6/23.
 */

public class ActionEvaluateBean implements Serializable {
    @SerializedName("o")
    public String orderId = null;//订单id
}
