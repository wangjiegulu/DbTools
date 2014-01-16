package com.wangjie.test;

import com.wangjie.dbtools.*;
import java.io.File;
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
//        String str = App.class.getResource("/db.properties").getPath();
//        System.out.println("----------str: " + str);
        DbExecutor.initDatabase(App.class.getResourceAsStream("/db.properties"));
        
        DbExecutor<User> dbExector = new DbExecutor<User>();
        List<User> users = dbExector.executeQuery("select * from users", User.class);
        
        DbExecutor<Person> dbExector2 = new DbExecutor<Person>();
        List<Person> persons = dbExector2.executeQuery("select * from person", Person.class);

        
        
//        System.out.println("2-----------------------------");
        /*
        User user = new User();
        user.setUid(31);
        user.setUsername("hello2");
        user.setPassword("world2");
        
        System.out.println("insert result: " + dbExector.executeSave(user));
        
        System.out.println("3-----------------------------");
        Person p = new Person();
        p.setPid(10);
        p.setAddress("huzhou22");
        p.setAge(24);
        p.setBirthday(new Timestamp(System.currentTimeMillis()));
        p.setName("wangjie222");
        p.setPhoneNum("098765432155");
        
        System.out.println("person insert result: " + dbExector2.executeSave(p));
        */
        
        System.out.println("3-----------------------------");
        
//        dbExector.execute("delete from person where pid = 5");
        
    }
    
    
    
}
