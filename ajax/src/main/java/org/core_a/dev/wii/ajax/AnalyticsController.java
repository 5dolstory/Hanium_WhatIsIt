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

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.*;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

@Component
@RestController
public class AnalyticsController {
    public class Report {
        private int Amount;
        private String StartAt;
        private String EndAt;
        private String Keyword;

        public int getAmount() { return Amount; };
        public void setAmount(int Amount) { this.Amount = Amount; };
        public String getStartAt() { return StartAt; };
        public void setStartAt(String StartAt) { this.StartAt = StartAt; };
        public String getEndAt() { return EndAt; };
        public void setEndAt(String EndAt) { this.EndAt = EndAt; };
        public String getKeyword() { return Keyword; };
        public void setKeyword(String Keyword) { this.Keyword = Keyword; };
    }
    public class RelatedArticle {
        private String No;
        private String Title;
        private String Url;
        private Morpheme Morpheme;

        public String getNo() { return No; };
        public void setNo(String No) { this.No = No; };
        public String getUrl() { return Url; };
        public void setUrl(String Url) { this.Url = Url; };
        public String getTitle() { return Title; };
        public void setTitle(String Title) { this.Title = Title; };

        public Morpheme getMorpheme() { return Morpheme; }
        public String getMorphemeTitle() { return "\"" + String.join("\", \"", Morpheme.getTitle()) + "\""; }
        public String getMorphemeContent() { return "\"" + String.join("\", \"", Morpheme.getContent()) + "\""; }
        public void setMorpheme(Morpheme Morpheme) { this.Morpheme = Morpheme; }
    }
    public class Keyword {
        private String Title;
        private int Seq;
        private keyword = new HashMap<String , Integer>();

        public int getOrCreateKeyword(String keyword) { 
            if (keyword.get(keyword) {
                return this.keyword.get(keyword);
            } else {
                return this.keyword.put(keyword, this.keyword.size());
            }
        };
    }

    private static String driverNameHive = "org.apache.hive.jdbc.HiveDriver";
    private static String driverNameMysql = "com.mysql.jdbc.Driver";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/report/{project}")
    public String report(@PathVariable String project) {
        Gson gson = new GsonBuilder().create();
        List<Report> reports = getReport(project);

        return gson.toJson(reports);
    }

    private List<Report> getReport(String project) {
        //FIXME
        String query = "SELECT amount, start_at, end_at, keyword FROM report WHERE project_seq = (select seq from project where name ='" + project + "')";
        List<Report> reports = new ArrayList<Report>();

        try {
            try { // hive
                Class.forName(driverNameMysql);
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

            Connection conMysql = DriverManager.getConnection("jdbc:mysql://localhost:3306/wii", "root", "root");

            Statement stmtMysql = conMysql.createStatement();

            ResultSet res = null;
            res = stmtMysql.executeQuery(query);

            while (res.next()) {
                Report report = new Report();

                report.setAmount(res.getInt(1));
                report.setStartAt(res.getString(2));
                report.setEndAt(res.getString(3));
                report.setKeyword(res.getString(4));
                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    @Cacheable("articles")
    @RequestMapping("/article/{no}")
    public String article(@PathVariable String no) {
        //simulateSlowService();
        Gson gson = new GsonBuilder().create();
        RelatedArticle[] rows = getArticle(no);

        return gson.toJson(rows);
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
        RelatedArticle[] relatedArticles = null;

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
            // morpheme to mahout recomandation
            relatedArticles = getRelatedArticle(row);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relatedArticles;
    }

    public RelatedArticle[] getRelatedArticle(Row row) {
        List<RelatedArticle> rows = new ArrayList<RelatedArticle>();
        Keyword keywords = new Keyword();

        List<String> morphemes = new ArrayList<String>();
        for (Morpheme innerMorphem : row.getMorpheme()) {
            List<String> morphemes = innerMorphem.getTitle();
            morphemes.addAll(innerMorphem.getContent());
        }

        for (string token : row.getTitle()) {
            prefsForUser1.setItemID(0, keyword.getOrCreateKeyword(token));
            prefsForUser1.setValue(0, (float)(3 / morpheme.getTitle().size()));
        }
        for (string token : row.getTitle()) {
            prefsForUser1.setItemID(0, keyword.getOrCreateKeyword(token));
            prefsForUser1.setValue(0, (float)(1 / morpheme.getTitle().size()));
        }

        int flag = 0;
        for (int i = 0; i < morphemes.size(); i++) {

            if (flag == 0) {
                flag = 1;
            } else {
                summaryQuery += " UNION ";
            }
            summaryQuery += "SELECT no, title, concat_ws(', ', collect_set(morpheme_title)) AS morpheme_titl, econcat_ws(', ', collect_set(morpheme_content)) AS morpheme_content, url FROM article WHERE array_contains(morpheme_title, '" + morphemes.get(i) + "') OR array_contains(morpheme_content, '" + morphemes.get(i) + "') ";
        }


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
            res = stmt.executeQuery(summaryQuery);

            List<String> morpheme_title = new ArrayList<String>();
            List<String> morpheme_content = new ArrayList<String>();
            Morpheme morpheme = new Morpheme();

            FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
            PreferenceArray prefsForUser1 = new GenericUserPreferenceArray(res.getFetchSize());
            int i = 1;
            while (res.next()) {
                rows.setNo(res.getString(1));
                rows.setTitle(res.getString(2));
                rows.setUrl(res.getString(5));

                morpheme_title = Arrays.asList(res.getString(3).split(","));
                morpheme_content = Arrays.asList(res.getString(4).split(","));
                morpheme.setTitle(morpheme_title);
                morpheme.setContent(morpheme_content);

                rows.setMorpheme(morpheme);

                for (string token : morpheme.getTitle()) {
                    prefsForUser1.setItemID(i, keyword.getOrCreateKeyword(token));
                    prefsForUser1.setValue(i, (float)(3 / morpheme.getTitle().size()));
                }
                for (string token : morpheme.getTitle()) {
                    prefsForUser1.setItemID(i, keyword.getOrCreateKeyword(token));
                    prefsForUser1.setValue(i, (float)(1 / morpheme.getTitle().size()));
                }
                preferences.put(i, prefsForUser1);
                i++;
            }




            DataModel model = new GenericDataModel(preferences);

            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood =
              new NearestNUserNeighborhood(2, similarity, model);

            Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);

            List<RecommendedItem> recommendations =
                recommender.recommend(row.No, 5);

            int returnKey = 0;
            List<Row> returnRows = new ArrayList<Row>();
            for (RecommendedItem recommendation : recommendations) {
                returnRows.set(returnKey, rows.get(rerecommendations.user));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    @Cacheable("users")
    @RequestMapping("/user/{author}")
    public String user(@PathVariable String author) {
        return author;
    }
}


class RecommenderIntro {

  private RecommenderIntro() {
  }

  public static void main(String[] args) throws Exception {

}

class CreateGenericDataModel {

  private CreateGenericDataModel() {
  }

  public static void main(String[] args) {
    FastByIDMap<PreferenceArray> preferences =
      new FastByIDMap<PreferenceArray>();
    PreferenceArray prefsForUser1 = new GenericUserPreferenceArray(10);
    prefsForUser1.setUserID(0, 1L);
    prefsForUser1.setItemID(0, 101L);
    prefsForUser1.setValue(0, 3.0f);
    prefsForUser1.setItemID(1, 102L);
    prefsForUser1.setValue(1, 4.5f);

    preferences.put(1L, prefsForUser1);

    DataModel model = new GenericDataModel(preferences);
    System.out.println(model);
  }

}

