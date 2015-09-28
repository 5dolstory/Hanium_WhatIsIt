package org.core_a.dev.wii.db;

import java.io.IOException;
import java.util.LinkedList;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

@RestController
public class AnalyticsController {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/article/import")
    public Row row(@RequestParam(value="json") String json) {
        Gson gson = new GsonBuilder().create();
        Row row = gson.fromJson(json, Row.class);
        hiveMethod();
        return row;
    }

    public static void hiveMethod() {
        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //System.exit(1);
            }
            //replace "hive" here with the name of the user the queries should run as
            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "hive", "");
            Statement stmt = con.createStatement();
            String tableName = "article";
            //stmt.execute("drop table if exists " + tableName);
            //stmt.execute("create table " + tableName + " (key int, value string)");
            // show tables
            String sql = "show tables '" + tableName + "'";
            System.out.println("Running: " + sql);
            ResultSet res = stmt.executeQuery(sql);
            if (res.next()) {
                System.out.println(res.getString(1));
            }
            // describe table
            sql = "describe " + tableName;
            System.out.println("Running: " + sql);
            res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
            }

    /*
            // load data into table
            // NOTE: filepath has to be local to the hive server
            // NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
            String filepath = "/tmp/a.txt";
            sql = "load data local inpath '" + filepath + "' into table " + tableName;
            System.out.println("Running: " + sql);
            stmt.execute(sql);

            // select * query
            sql = "select * from " + tableName;
            System.out.println("Running: " + sql);
            res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
            }

            // regular hive query
            sql = "select count(1) from " + tableName;
            System.out.println("Running: " + sql);
            res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(res.getString(1));
            }
    */
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
