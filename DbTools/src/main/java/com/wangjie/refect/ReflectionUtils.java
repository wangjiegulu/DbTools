/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.refect;

import java.lang.reflect.*;
import java.util.logging.*;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
public abstract class ReflectionUtils
{
    
    public static interface FieldCallback{
        public void doWith(Field field) throws Exception;
    }
    public static void doWithFields(Class<?> clazz, FieldCallback fieldCallback){
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            try
            {
                fieldCallback.doWith(f);
            }
            catch (Exception ex)
            {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
}
