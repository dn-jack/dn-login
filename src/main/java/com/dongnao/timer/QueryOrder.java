package com.dongnao.timer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dongnao.util.HttpUtil;
import com.dongnao.util.SpringContextHolder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TaskItemDefine;

public class QueryOrder implements IScheduleTaskDealMulti<String> {
    
    private static Logger log = LoggerFactory.getLogger(QueryOrder.class);
    
    public List<String> selectTasks(String paramString1, String paramString2,
            int paramInt1, List<TaskItemDefine> paramList, int paramInt2)
            throws Exception {
        log.info("-------------------paramString1---------------"
                + paramString1);
        String cookie = getCookie();
        
        if ("".equals(cookie)) {
            return null;
        }
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", cookie);
        
        String queryStr = HttpUtil.doGet("https://wmcrm.baidu.com/crm?hhs=secure&qt=neworderlist&display=json",
                cookie,
                "");
        
        log.info("-------------------queryStr---------------" + queryStr);
        
        return null;
    }
    
    private String getCookie() {
        MongoTemplate mt = (MongoTemplate)SpringContextHolder.getWebApplicationContext()
                .getBean("mongoTemplate");
        DBCollection dbc = mt.getCollection("dn_loginInfo");
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("username", "cs15243688033");
        DBCursor dboo = dbc.find(dbo);
        
        String cookie = "";
        while (dboo.hasNext()) {
            DBObject ooo = dboo.next();
            cookie = ooo.get("cookie").toString();
        }
        
        return cookie;
    }
    
    public Comparator<String> getComparator() {
        return null;
    }
    
    public boolean execute(String[] paramArrayOfT, String paramString)
            throws Exception {
        return false;
    }
    
}
