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
import java.sql.PreparedStatement;
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
        //System.out.println(row.toString());
        //hiveMethod();
        hiveMethod(row);
        return row;
    }

    public static void hiveMethodDummy(Row row) {
        String query = "INSERT INTO article PARTITION (datestamp = \"" + row.getWriteAtDateOnly() + "\") SELECT ";
        query += "(\"" + row.getProject() + "\"), ";
        query += "(\"" + row.getTitle() + "\"), ";
        query += "(\"" + row.getContent() + "\"), ";
        query += "(\"" + row.getAuthor() + "\"), ";
        query += "(\"" + row.getNo() + "\"), ";
        query += "(\"" + row.getUrl() + "\"), ";
        query += "Array(" + row.getMorphemeTitle() + "), ";
        query += "Array(" + row.getMorphemeContent() + "), ";
        query += "(\"" + row.getWriteAt() + "\"), ";
        query += " FROM dummy LIMIT 1;";

//        System.out.println(row.getTitle());
//        System.out.println(row.getContent());
//        System.out.println(row.getUrl());
        System.out.println(query);
    }

    public static void hiveMethod(Row row) {
//        String query = "INSERT INTO article PARTITION (datestamp = ?) SELECT ";
//        query += "(?), "; // project
//        query += "(?), "; // title
//        query += "(?), "; // content
//        query += "(?), "; // author
//        query += "(?), "; // no
//        query += "(?), "; // url
//        query += "Array(?), "; // morpheme title
//        query += "Array(?), "; // morpheme content
//        query += "(?) "; // write at
//        query += " FROM dummy LIMIT 1";

        String query = "INSERT INTO article PARTITION (datestamp = \"" + row.getWriteAtDateOnly() + "\") SELECT ";
        query += "(\"" + row.getProject() + "\"), ";
        query += "(\"" + row.getTitle() + "\"), ";
        query += "(\"" + row.getContent() + "\"), ";
        query += "(\"" + row.getAuthor() + "\"), ";
        query += "(\"" + row.getNo() + "\"), ";
        query += "(\"" + row.getUrl() + "\"), ";
        query += "Array(" + row.getMorphemeTitle() + "), ";
        query += "Array(" + row.getMorphemeContent() + "), ";
        query += "(\"" + row.getWriteAt() + "\") ";
        query += " FROM dummy LIMIT 1";
        System.out.println(row.getTitle());

        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //System.exit(1);
            }
            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "fishz", "");
//            PreparedStatement psmt = con.prepareStatement(query);
//            psmt.setString(1, row.getProject());
//            psmt.setString(2, row.getTitle());
//            psmt.setString(3, row.getContent());
//            psmt.setString(4, row.getAuthor());
//            psmt.setString(5, row.getNo());
//            psmt.setString(6, row.getUrl());
//            psmt.setString(7, row.getMorphemeTitle());
//            psmt.setString(8, row.getMorphemeContent());
//            psmt.setString(9, row.getWriteAt());
//            psmt.executeUpdate();

            Statement stmt = con.createStatement();

            stmt.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
