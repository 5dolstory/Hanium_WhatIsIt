package org.core_a.dev.wii.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import static spark.Spark.*;
import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.hannanum.WorkflowFactory;
import de.jetwick.snacktory.*;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult09.SimpleMAResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.SimpleMAResult22.SimpleMAResult22;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult09.SimplePOSResult09;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult22.SimplePOSResult22;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Parser {
    public static class ParseResult {
        private String Content;
        private Morpheme Morpheme;
        private String WriteAt;

        public String getContent() { return Content; }
        public void setContent(String Content) { this.Content = Content; }
        public Morpheme getMorpheme() { return Morpheme; }
        public void setMorpheme(Morpheme Morpheme) { this.Morpheme = Morpheme; }
        public String getWriteAt() { return WriteAt; }
        public void setWriteAt(String WriteAt) { this.WriteAt = WriteAt; }

    }


    public static void main(String[] args) {
        port(3002);

        post("/parse/rss", (req, res) -> {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Task task = gson.fromJson(req.queryParams("task"), Task.class);
            Pattern pattern = Pattern.compile("articleId=(\\d+)");


            for( String url : task.getUrls() )
            {
                Document doc = Jsoup.connect(url).get();

                for( Element item : doc.select("item") )
                {
                    final String title = item.select("title").first().text();
                    final String author = item.select("author").first().text();
                    final String link = item.select("link").first().text();
                    Matcher matcher = pattern.matcher(link);
                    String no = "";
                    while(matcher.find()) {
                        no = matcher.group(1);
                    }

                    Row parsedRow = new Row(title, author, link);


                    ParseResult parseResult = processRuliweb(link);
                    parsedRow.setProject(task.getProjectName());
                    parsedRow.setNo(no);
                    parsedRow.setMorpheme(parseResult.getMorpheme());
                    parsedRow.setContent(parseResult.getContent());
                    parsedRow.setWriteAt(parseResult.getWriteAt());

//                    System.out.println("\njson string\t" + gson.toJson(parsedRow));
//                    System.out.println("");
                    sendPost(gson.toJson(parsedRow));
                }
            }

            return "";
        });
    }

    // get document
    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    // to morpheme, parse context
    public static String parseRuliwebArticle(Document document) {
        Elements content = document.select("#gaiaViewCont");
        Elements innerTag = content.select("table.read_cont_table");

        String contents = "";
        for(Element e: innerTag){
            contents += " " + e.text();
        }   

        return contents;
    }

    public static String parseAgoraArticle(Document document) {
        return "";
    }

    // to morpheme, parse title
    public static String parseRuliwebTitle(Document document) {
        Elements content = document.select(".tit_user");
        content.select("span").remove();
        Elements pTag = content.select("strong");

        String contents = "";
        for(Element e: pTag){
            contents += " " + e.text();
        }   

        return contents;
    }

    public static String parseAgoraTitle(Document document) {
        return "";
    }

    // to parse date
    public static String parseRuliwebDate(Document document) {
        Elements content = document.select("ul.list_report");
        content.select(".tit").remove();
        Elements pTag = content.select("li.time");

        String contents = "";
        for(Element e: pTag){
            contents += e.text();
        }   

        return contents;
    }
    public static String parseAgoraDate(Document document) {
        return "";
    }
    // weight

    // process Ruliweb
    public static ParseResult processRuliweb(String url) {
        ParseResult parseResult = new ParseResult();
        Morpheme morpheme = new Morpheme(); 
        List<String> titleList = new ArrayList<String>();
        List<String> articleList = new ArrayList<String>();

        // TODO task id generate
        Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_NOUN_EXTRACTOR);
        try {
            Document document = getDocument(url);
            String article = parseRuliwebArticle(document);
            String title = parseRuliwebTitle(document);

            String tmpWriteAt = parseRuliwebDate(document);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd (HH:mm:ss)");
            Date writeAtDate = transFormat.parse(tmpWriteAt);
            DateFormat transFormatToPost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String writeAt = transFormatToPost.format(writeAtDate);

            parseResult.setContent(article);
            parseResult.setWriteAt(writeAt);

            workflow.activateWorkflow(true);

            workflow.analyze(article);

            // article
            LinkedList<Sentence> resultList = workflow.getResultOfDocument(new Sentence(0, 0, false));
            for (Sentence s : resultList) {
                Eojeol[] eojeolArray = s.getEojeols();
                for (int i = 0; i < eojeolArray.length; i++) {
                    if (eojeolArray[i].length > 0) {
                        String[] morphemes = eojeolArray[i].getMorphemes();
                        for (int j = 0; j < morphemes.length; j++) {
                            String tmpMorpheme = morphemes[j];
                            tmpMorpheme = tmpMorpheme.trim();
                            tmpMorpheme = tmpMorpheme.replace(" ","");
                            tmpMorpheme = tmpMorpheme.replace("\u00A0","");
                            if (tmpMorpheme.length() > 0) {
                                articleList.add(tmpMorpheme);
                            }
                        }
                    }
                }
            }

            // title
            workflow.analyze(title);
            resultList = workflow.getResultOfDocument(new Sentence(0, 0, false));
            for (Sentence s : resultList) {
                Eojeol[] eojeolArray = s.getEojeols();
                for (int i = 0; i < eojeolArray.length; i++) {
                    if (eojeolArray[i].length > 0) {
                        String[] morphemes = eojeolArray[i].getMorphemes();
                        for (int j = 0; j < morphemes.length; j++) {
                            String tmpMorpheme = morphemes[j];
                            tmpMorpheme = tmpMorpheme.trim();
                            tmpMorpheme = tmpMorpheme.replace(" ","");
                            tmpMorpheme = tmpMorpheme.replace("\u00A0","");
                            if (tmpMorpheme.length() > 0) {
                                titleList.add(tmpMorpheme);
                            }
                        }
                    }
                }
            }

            workflow.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        workflow.close();  	
        morpheme.setTitle(titleList);
        morpheme.setContent(articleList);

        parseResult.setMorpheme(morpheme);
        return parseResult;
    }

    private static void sendPost(String param) throws Exception {
        param = "json=" + URLEncoder.encode(param, "UTF-8");
        String USER_AGENT = "wii/1.0";
        String url = "http://localhost:8080/article/import";
//        String url = "http://localhost:3001/test";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(param);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + param);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        //print result
        System.out.println(response.toString());
    }
}
