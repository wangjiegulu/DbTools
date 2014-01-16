/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.test;

import com.wangjie.dbtools.anno.Column;
import com.wangjie.dbtools.anno.PrimaryKey;
import com.wangjie.dbtools.anno.Table;
import java.io.*;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
@Table("users")
public class User implements Serializable
{
    @Column("uid")
    @PrimaryKey(insertable = true)
    private int uid;
    @Column
    private String username;
    @Column("password")
    private String password;

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "User{" + "uid=" + uid + ", username=" + username + ", password=" + password + '}';
    }
    
    
    
}
