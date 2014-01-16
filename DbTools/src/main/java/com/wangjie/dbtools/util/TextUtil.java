/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.dbtools.util;

import java.util.*;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
public class TextUtil
{
    /**
     * 生成占位符
     * @param count
     * @return 
     */
    public static String generatePlaceholders(int count){
        if(count <= 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append(",?");
        }
        return sb.toString().substring(1);
    }
    
    public static String joinStrings(Collection<String> set, String separator){
        StringBuilder sb = new StringBuilder();
        for(Iterator<String> iter = set.iterator(); iter.hasNext();){
            sb.append(separator).append(iter.next());
        }
        return sb.toString().trim().substring(separator.length());
    }
    
    
}
