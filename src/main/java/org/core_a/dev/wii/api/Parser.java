package org.core_a.dev.wii.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

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



public class Parser {
    public static void main(String[] args) {
        // port(4566);
        get("/hello", (req, res) -> "Hello World");
        get("/parse/document", (req, response) -> {
            //processRuliweb();
            String a = parseRuliwebTitle( Jsoup.connect("http://localhost:3001/feed/humor/1").get() );
            return "";
        });
        get("/parse/rss", (req, res) -> {
            String url = "http://localhost:3001/rss/humor";
            Document doc = Jsoup.connect(url).get();

            for( Element item : doc.select("item") )
            {
                final String title = item.select("title").first().text();
                final String author = item.select("author").first().text();
                final String link = item.select("link").first().nextSibling().toString();

                System.out.println("----------------------");
                System.out.print(title);
                System.out.print(author);
                System.out.print(link);
                System.out.println("");
            }
            return "b2";
        });
    }

    // get document
    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect("http://localhost:3001/feed/humor/1").get();
    }

    // to morpheme, parse context
    public static String parseRuliwebArticle(Document document) {
        Elements content = document.select("#gaiaViewCont");
        Elements pTag = content.select("p");

        String contents = "";
        for(Element e: pTag){
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
            System.out.println(e.text());
            contents += " " + e.text();
        }   

        return contents;
    }

    public static String parseAgoraTitle(Document document) {
        return "";
    }

    // weight

    // process Ruliweb
    public static void processRuliweb() {
        //task id generate
        Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_NOUN_EXTRACTOR);
        try {
            Document document = getDocument("maybe url");
            String article = parseRuliwebArticle(document);
            String title = parseRuliwebArticle(document);

            workflow.activateWorkflow(true);

            workflow.analyze(article);

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
                                System.out.print(tmpMorpheme.length() + "[" + tmpMorpheme + "]");
                            }
                        }
                        System.out.print(", ");
                    }
                }
            }
            workflow.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(0);
        }
        workflow.close();  	
    }
}
