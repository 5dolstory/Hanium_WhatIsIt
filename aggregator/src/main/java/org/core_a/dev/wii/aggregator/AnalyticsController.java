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
    private static String driverNameHive = "org.apache.hive.jdbc.HiveDriver";
    private static String driverNameMysql = "com.mysql.jdbc.Driver";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/report")
    public String order(@RequestParam(value="json") String json) {
        Gson gson = new GsonBuilder().create();
        Order order = gson.fromJson(json, Order.class);
        makeReport(order);
        return "200";
    }

    public static void makeReport(Order order) {
        String amountQuery = " SELECT count(*) FROM article WHERE created_at BETWEEN to_date('" + order.getStartAt() + "') AND to_date('" + order.getEndAt() + "')";
        String keywordQuery = " SELECT explode(morpheme_title) FROM article WHERE created_at BETWEEN to_date('" + order.getStartAt() + "') AND to_date('" + order.getEndAt() + "') UNION SELECT explode(morpheme_content) FROM article WHERE created_at BETWEEN to_date('" + order.getStartAt() + "') AND to_date('" + order.getEndAt() + "')";
        String summaryQuery = "";

        //mysql
        String reportQuery = "INSERT INTO report (project_seq, start_at, end_at, amount, keyword) values(" + order.getProject() + ", '" + order.getStartAt() + "', '" + order.getEndAt() + "', ";

        try {
            try { // hive
                Class.forName(driverNameHive);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //System.exit(1);
            }

            try { // mysql
                Class.forName(driverNameMysql);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //System.exit(1);
            }

            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "fishz", "");

            Statement stmt = con.createStatement();

            ResultSet res = null;
            System.out.println(amountQuery);
            res = stmt.executeQuery(amountQuery);
            int amount = -1;
            while (res.next()) {
                amount = res.getInt(1);
            }
            if (amount > 0) {
                res = stmt.executeQuery(keywordQuery);

                int flag = 0;
                while (res.next()) {
                    if (flag == 0) {
                        flag = 1;
                    } else {
                        summaryQuery += " UNION ";
                    }
                    //System.out.println(String.valueOf(res.getString(1)));
                    summaryQuery += "SELECT '" + res.getString(1) + "' AS keyword, concat_ws(', ', collect_set(no)) AS noes FROM article WHERE array_contains(morpheme_title, '" + res.getString(1) + "') OR array_contains(morpheme_content, '" + res.getString(1) + "') ";
                }

                // TEST
//                summaryQuery = "select \"ost\", concat_ws(', ', collect_set(no)) from article where array_contains(morpheme_content, \"ost\")";
//                summaryQuery += " union select \"내년\", concat_ws(', ', collect_set(no)) from article where array_contains(morpheme_content, \"내년\")";
//
                // TEST

                res = stmt.executeQuery(summaryQuery);
                System.out.println(summaryQuery);
                String keywords = "";
                while (res.next()) {
                    System.out.println(String.valueOf(res.getString(1)) + "\t" +String.valueOf(res.getString(2)));
                    keywords += res.getString(1) + "(" + res.getString(2) + ")|";
                }
                keywords = keywords.substring(0, keywords.length() -1);

                reportQuery += amount + ", '" + keywords + "')";
            } else {
                reportQuery += "0, null)";
            }

            Connection conMysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/wii", "root", "root");

            Statement stmtMysql = conMysql.createStatement();
            stmtMysql.executeUpdate(reportQuery);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
