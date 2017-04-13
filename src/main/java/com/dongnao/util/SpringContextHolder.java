package com.dongnao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    protected static Log logger = LogFactory.getLog(SpringContextHolder.class);
    
    public static ApplicationContext getWebApplicationContext() {
        return applicationContext;
    }
    
    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        applicationContext = arg0;
    }
}
