package org.core_a.dev.wii.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
    private final String USER_AGENT = "wii/1.0";

    public static void main(String[] args) {
        port(3002);

        post("/parse/rss", (req, res) -> {
            Gson gson = new GsonBuilder().create();
            Task task = gson.fromJson(req.queryParams("task"), Task.class);

            for( String url : task.getUrls() )
            {
                Document doc = Jsoup.connect(url).get();

                for( Element item : doc.select("item") )
                {
                    final String title = item.select("title").first().text();
                    final String author = item.select("author").first().text();
                    final String link = item.select("link").first().text();
                    Row parsedRow = new Row(title, author, link);

                    System.out.println("----------------------");
                    System.out.println("title\t" + title);
                    System.out.println("author\t" + author);
                    System.out.println("link\t" + link);
                    System.out.println("");

                    Morpheme morpheme = processRuliweb(link);
                    parsedRow.setMorpheme(morpheme);

                    System.out.println("class\t" + parsedRow);
                    System.out.println("");
                    System.out.println("");
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

    // weight

    // process Ruliweb
    public static Morpheme processRuliweb(String url) {
        Morpheme morpheme = new Morpheme(); 
        List<String> titleList = new ArrayList<String>();
        List<String> articleList = new ArrayList<String>();
        //
        //task id generate
        Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_NOUN_EXTRACTOR);
        try {
            Document document = getDocument(url);
            System.out.println("get, " + url);
            String article = parseRuliwebArticle(document);
            String title = parseRuliwebTitle(document);

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
//                                System.out.print(tmpMorpheme.length() + "[" + tmpMorpheme + "]");
                                System.out.print(tmpMorpheme);
                                articleList.add(tmpMorpheme);
                            }
                        }
                        System.out.print(", ");
                    }
                }
            }

            System.out.println("");

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
                                System.out.print(tmpMorpheme);
                                titleList.add(tmpMorpheme);
                            }
                        }
                        System.out.print(", ");
                    }
                }
            }
            System.out.print("\t\t ------ " + url);

            workflow.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(0);
        }
        workflow.close();  	
        morpheme.setTitle(titleList);
        morpheme.setContent(articleList);

        return morpheme;
    }

    private void sendPost(String param) throws Exception {

        String url = "http://172.16.2.11:8080/article/import";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
        
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
