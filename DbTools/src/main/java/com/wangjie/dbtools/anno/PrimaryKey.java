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
@Target(ElementType.FIELD)
public @interface PrimaryKey{
    boolean insertable() default false;
}
