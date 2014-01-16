/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.dbtools.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
public class Log
{
    Logger logger;
    boolean isLog = true;
    
    public static Log getLogger(Class<?> clazz){
        return new Log().
                setLogger(java.util.logging.Logger.getLogger(clazz.getName()));
    }
    
    public void log(Level level, String msg){
        if(!isLog) return;
        logger.log(level, msg);
    }
    
    public void log(Level level, String msg, Throwable thrown){
        if(!isLog) return;
        logger.log(level, msg, thrown);
    }
    
    public void log(Level level, String msg, Object param){
        if(!isLog) return;
        logger.log(level, msg, param);
    }
    
    public void log(Level level, String msg, Object[] params){
        if(!isLog) return;
        logger.log(level, msg, params);
    }
    
    public void log(LogRecord logRecord){
        if(!isLog) return;
        logger.log(logRecord);
        
    }
    
    public void log(Level level, String msg, List objs){
        if(!isLog) return;
        StringBuilder sb = new StringBuilder(msg + "\n");
        for(Object obj : objs){
            sb.append("\t").append("==> ").append(obj.toString()).append("\n");
        }
        log(level, sb.toString());
    }
    

    public Logger getLogger()
    {
        return logger;
    }

    public Log setLogger(Logger logger)
    {
        this.logger = logger;
        return this;
    }
    
    
    
    
}
