package org.core_a.dev.wii.db;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RestController
public class AnalyticsController {
    private static String driverNameHive = "org.apache.hive.jdbc.HiveDriver";
    private static String driverNameMysql = "com.mysql.jdbc.Driver";

    private final AtomicLong counter = new AtomicLong();

    @Cacheable("articles")
    @RequestMapping("/article/{no}")
    public String order(@PathVariable String no) {
        //simulateSlowService();
        Gson gson = new GsonBuilder().create();
        Row row = getArticle(no);

        return gson.toJson(row);
    }

    private void simulateSlowService() {
        try {
            long time = 5000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public Row getArticle(String no) {
        String query = "SELECT title, content, author, url, concat_ws(',', morpheme_title) as morpheme_title, concat_ws(',', morpheme_content) as morpheme_content FROM article WHERE no = '" + no + "'";
        Row row = new Row();

        try {
            try { // hive
                Class.forName(driverNameHive);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //System.exit(1);
            }

            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "fishz", "");

            Statement stmt = con.createStatement();

            ResultSet res = null;
            res = stmt.executeQuery(query);

            Morpheme morpheme = new Morpheme();
            List<String> morpheme_title = new ArrayList<String>();
            List<String> morpheme_content = new ArrayList<String>();
            while (res.next()) {
                row.setTitle(res.getString(1));
                row.setContent(res.getString(2));
                row.setAuthor(res.getString(3));
                row.setUrl(res.getString(4));
                morpheme_title = Arrays.asList(res.getString(5).split(","));
                morpheme_content = Arrays.asList(res.getString(6).split(","));
                morpheme.setTitle(morpheme_title);
                morpheme.setContent(morpheme_content);
                row.setMorpheme(morpheme);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }
}
