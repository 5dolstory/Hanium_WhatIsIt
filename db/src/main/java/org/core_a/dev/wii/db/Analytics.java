package org.core_a.dev.wii.db;

import java.io.IOException;
import java.util.LinkedList;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Analytics {
    public static void main(String[] args) {
        port(3003);

        post("/article/import", (req, response) -> {
            Gson gson = new GsonBuilder().create();
            String task_no = req.queryParams("task_no");
            Article article = gson.fromJson(req.queryParams("article"), Article.class);
            return "abc";
        });
    }
}
