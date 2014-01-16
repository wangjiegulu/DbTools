package com.wangjie.test;

import com.wangjie.dbtools.*;
import java.sql.*;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException, Exception
    {
        
        DbExecutor<User> dbExector = new DbExecutor<User>();
        List<User> users = dbExector.executeQuery("select * from users", User.class);
        for(User u : users){
            System.out.println("user: " + u.toString());
        }
        
        System.out.println("1-----------------------------");
        
        DbExecutor<Person> dbExector2 = new DbExecutor<Person>();
        List<Person> persons = dbExector2.executeQuery("select * from person", Person.class);
        for(Person p : persons){
            System.out.println("person: " + p.toString());
        }
        
        System.out.println("2-----------------------------");
        
        User user = new User();
        user.setUid(31);
        user.setUsername("hello2");
        user.setPassword("world2");
        
        System.out.println("insert result: " + dbExector.executeSave(user));
        /*
        System.out.println("3-----------------------------");
        Person p = new Person();
        p.setPid(10);
        p.setAddress("huzhou");
        p.setAge(24);
        p.setBirthday(new Timestamp(System.currentTimeMillis()));
        p.setName("wangjie2");
        p.setPhoneNum("0987654321");
        
        System.out.println("person insert result: " + dbExector2.executeSave(p));
        */
        
        
        
    }
    
    
    
}
