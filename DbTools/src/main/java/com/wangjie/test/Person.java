/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.test;

import com.wangjie.dbtools.anno.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
@Table
public class Person implements Serializable
{
    @Column
    @PrimaryKey
    private int pid;
    @Column("uname")
    private String name;
    @Column("age")
    private int age;
    @Column("addr")
    private String address;
    @Column("phone_num")
    private String phoneNum;
    @Column("birth")
    private Timestamp birthday;

    public int getPid()
    {
        return pid;
    }

    public void setPid(int pid)
    {
        this.pid = pid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhoneNum()
    {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum)
    {
        this.phoneNum = phoneNum;
    }

    public Timestamp getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Timestamp birthday)
    {
        this.birthday = birthday;
    }

    @Override
    public String toString()
    {
        return "Person{" + "pid=" + pid + ", name=" + name + ", age=" + age + ", address=" + address + ", phoneNum=" + phoneNum + ", birthday=" + birthday + '}';
    }

    
    
}
