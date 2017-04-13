package com.dongnao.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ElemeUtil {
    
    public static Map<String, String> orderFilterMap = new HashMap<String, String>();
    
    static {
        orderFilterMap.put("all", "ORDER_QUERY_ALL");
        orderFilterMap.put("being", "ORDER_QUERY_ALL_PROCESSING_V2");
        orderFilterMap.put("complete", "ORDER_QUERY_PROCESSED_V2");
        orderFilterMap.put("cancel", "ORDER_QUERY_PROCESSED_INVALID");
    }
    
    public static String queryOrderurl = "https://app-api.shop.ele.me/nevermore/invoke/?method=OrderService.queryOrder";
    
    public static String confirmOrderUrl = "https://app-api.shop.ele.me/order/invoke/?method=order.confirmOrder";
    
    public static JSONObject confirmOrderJo() {
        JSONObject paramJo = new JSONObject();
        paramJo.put("id", "3c9691a3-39cd-4324-843b-7d6d54c270a9");
        paramJo.put("method", "confirmOrder");
        paramJo.put("service", "order");
        JSONObject params = new JSONObject();
        params.put("orderId", "1203255090059113652");
        paramJo.put("params", params);
        JSONObject metas = new JSONObject();
        metas.put("appName", "melody");
        metas.put("appVersion", "4.4.0");
        metas.put("ksid", "NTE4MDkwYjItNDlmNy00ZmIyLWI4ODOWMyOT");
        paramJo.put("metas", metas);
        paramJo.put("ncp", "2.0.0");
        return paramJo;
    }
    
    public static JSONObject queryOrderJo() {
        JSONObject paramJo = new JSONObject();
        paramJo.put("id", "6a69984a-e07e-4095-9379-5a7af0905a45");
        paramJo.put("method", "queryOrder");
        paramJo.put("service", "OrderService");
        JSONObject params = new JSONObject();
        params.put("shopId", "1028679");
        params.put("orderFilter", "ORDER_QUERY_ALL");
        JSONObject conditionJo = new JSONObject();
        conditionJo.put("limit", 20);
        conditionJo.put("offset", 0);
        conditionJo.put("page", 1);
        conditionJo.put("bookingOrderType", "null");
        params.put("condition", conditionJo);
        paramJo.put("params", params);
        JSONObject metas = new JSONObject();
        metas.put("appName", "melody");
        metas.put("appVersion", "4.4.2");
        metas.put("ksid", "OTM4NjMxOGItODkxZC00ZTllLTg1ZWMjE5OW");
        paramJo.put("metas", metas);
        paramJo.put("ncp", "2.0.0");
        return paramJo;
    }
    
}
