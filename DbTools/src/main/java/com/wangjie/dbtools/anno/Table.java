/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.dbtools.anno;

import java.lang.annotation.*;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table
{
    String value() default "";
    
    
}
