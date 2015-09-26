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


@RestController
public class AnalyticsController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

//    @RequestMapping("/article/import")
//    public Article article(@RequestParam(value="json", defaultValue="World") String json) {
//        return new Article(json);
//    }
    @RequestMapping("/article/import")
    public Row row(@RequestParam(value="json") String json) {
        Gson gson = new GsonBuilder().create();
        Row row = gson.fromJson(json, Row.class);
        return row;
    }
}
