package com.dongnao.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dongnao.util.HttpRequest;
import com.dongnao.util.HttpsRequestUtil;

@Controller
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    @Qualifier("mongoTemplate")
    MongoTemplate mt;
    
    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    
    @RequestMapping("/getToken")
    public @ResponseBody String getToken(HttpServletRequest request,
            String param) {
        
        String getTokenUrl = "https://wmpass.baidu.com/wmpass/openservice/captchapair?protocal=https&callback=jQuery111002785091826082762_1492088485138&_=1492088485140";
        Map<String, String> resultMap = HttpRequest.bdsendGet(getTokenUrl);
        log.info(resultMap.get("result"));
        log.info(resultMap.get("cookie"));
        
        String result = resultMap.get("result");
        String data = result.substring(result.indexOf("(") + 1).replace(");",
                "");
        
        JSONObject dataJo = JSON.parseObject(data);
        if (0 != dataJo.getInteger("errno")) {
            return "";
        }
        JSONObject dataJo1 = dataJo.getJSONObject("data");
        String token = dataJo1.getString("token");
        
        JSONObject ret = new JSONObject();
        ret.put("respCode", "0000");
        ret.put("token", token);
        
        HttpSession session = request.getSession();
        session.setAttribute("tokenCookie", resultMap.get("cookie"));
        session.setAttribute("token", token);
        return ret.toString();
    }
    
    @RequestMapping("/bdlogin")
    public @ResponseBody String bdlogin(HttpServletRequest request,
            @RequestBody String param) {
        JSONObject paramJo = JSON.parseObject(param);
        String username = paramJo.getString("username");
        String password = paramJo.getString("password");
        String code = paramJo.getString("code");
        
        log.info(password);
        HttpSession session = request.getSession();
        String queryStr = "redirect_url=https%3A%2F%2Fwmcrm.baidu.com%2F&return_url=https%3A%2F%2Fwmcrm.baidu.com%2Fcrm%2Fsetwmstoken&type=1&channel=pc&account="
                + username
                + "&upass="
                + password
                + "&captcha="
                + code
                + "&token=" + session.getAttribute("token").toString();
        
        String loginUrl = "https://wmpass.baidu.com/api/login";
        
        try {
            Map<String, String> retMap = HttpsRequestUtil.bddoPost(loginUrl,
                    queryStr,
                    "UTF-8",
                    300000,
                    300000,
                    session.getAttribute("tokenCookie").toString(),
                    "",
                    "");
            log.info(retMap.get("result"));
            log.info(retMap.get("cookie"));
            
            String cookie = session.getAttribute("tokenCookie").toString()
                    + retMap.get("cookie") + "WMSTOKEN="
                    + getWMSTOKEN(retMap.get("result"));
            
            JSONObject mongoJo = new JSONObject();
            mongoJo.put("username", username);
            mongoJo.put("cookie", cookie);
            mt.insert(mongoJo.toString(), "dn_loginInfo");
            
            return retMap.get("result");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private String getWMSTOKEN(String result) {
        JSONObject resultJo = JSON.parseObject(result);
        if (0 != resultJo.getInteger("errno")) {
            return "";
        }
        
        JSONObject dataJo = resultJo.getJSONObject("data");
        return dataJo.getString("WMSTOKEN");
    }
    
    public static void main(String[] agrs) {
        String queryOrderUrl = "https://wmcrm.baidu.com/crm?qt=neworderlist";
        
        Map<String, String> resultMap = HttpRequest.bdsendGet(queryOrderUrl);
        log.info(resultMap.get("result"));
    }
}
