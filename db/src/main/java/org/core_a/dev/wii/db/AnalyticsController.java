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

    @RequestMapping("/article/import", method = RequestMethod.POST)
    public Article article(@RequestParam(value="name", defaultValue="World") String name) {
        return new Article();
    }
}
