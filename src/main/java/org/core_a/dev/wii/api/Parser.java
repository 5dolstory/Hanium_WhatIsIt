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
        get("/hello", (req, res) -> "Hello World");
        get("/test/parse/document", (req, res) -> {
            Workflow workflow = new Workflow();
            
            try {
                workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
                workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);

                workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
                workflow.appendMorphemeProcessor(new UnknownProcessor(), null);

                //workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
                workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
                workflow.activateWorkflow(true);
                
                String document = "한나눔 형태소 분석기는 KLDP에서 제공하는 공개 소프트웨어 프로젝트 사이트에 등록되어 있다.";
                
                workflow.analyze(document);
                System.out.println(workflow.getResultOfDocument());
                
                document = "日時: 2010년 7월 30일 오후 1시\n"
                    + "場所: Coex Conference Room\n";
                
                workflow.analyze(document);
                System.out.println(workflow.getResultOfDocument());
                
                workflow.close();
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            workflow.close();  	

            return "bye";
        });
        get("/test2", (req, res) -> {
            String word = "무궁화 꽃이 피었습니다.";
             System.out.println("file.encoding=" + System.getProperty("file.encoding"));
            System.out.println(word);
            System.out.println("utf-8 -> euc-kr        : " + new String(word.getBytes("utf-8"), "euc-kr"));
            System.out.println("utf-8 -> ksc5601       : " + new String(word.getBytes("utf-8"), "ksc5601"));
            System.out.println("utf-8 -> x-windows-949 : " + new String(word.getBytes("utf-8"), "x-windows-949"));
            System.out.println("utf-8 -> iso-8859-1    : " + new String(word.getBytes("utf-8"), "iso-8859-1"));
            System.out.println("iso-8859-1 -> euc-kr        : " + new String(word.getBytes("iso-8859-1"), "euc-kr"));
            System.out.println("iso-8859-1 -> ksc5601       : " + new String(word.getBytes("iso-8859-1"), "ksc5601"));
            System.out.println("iso-8859-1 -> x-windows-949 : " + new String(word.getBytes("iso-8859-1"), "x-windows-949"));
            System.out.println("iso-8859-1 -> utf-8         : " + new String(word.getBytes("iso-8859-1"), "utf-8"));
            System.out.println("euc-kr -> utf-8         : " + new String(word.getBytes("euc-kr"), "utf-8"));
            System.out.println("euc-kr -> ksc5601       : " + new String(word.getBytes("euc-kr"), "ksc5601"));
            System.out.println("euc-kr -> x-windows-949 : " + new String(word.getBytes("euc-kr"), "x-windows-949"));
            System.out.println("euc-kr -> iso-8859-1    : " + new String(word.getBytes("euc-kr"), "iso-8859-1"));
            System.out.println("ksc5601 -> euc-kr        : " + new String(word.getBytes("ksc5601"), "euc-kr"));
            System.out.println("ksc5601 -> utf-8         : " + new String(word.getBytes("ksc5601"), "utf-8"));
            System.out.println("ksc5601 -> x-windows-949 : " + new String(word.getBytes("ksc5601"), "x-windows-949"));
            System.out.println("ksc5601 -> iso-8859-1    : " + new String(word.getBytes("ksc5601"), "iso-8859-1"));
            System.out.println("x-windows-949 -> euc-kr     : " + new String(word.getBytes("x-windows-949"), "euc-kr"));
            System.out.println("x-windows-949 -> utf-8      : " + new String(word.getBytes("x-windows-949"), "utf-8"));
            System.out.println("x-windows-949 -> ksc5601    : " + new String(word.getBytes("x-windows-949"), "ksc5601"));
            System.out.println("x-windows-949 -> iso-8859-1 : " + new String(word.getBytes("x-windows-949"), "iso-8859-1"));
            return "bye bye";
        });

        get("/parse/document", (req, response) -> {
            Workflow workflow = WorkflowFactory.getPredefinedWorkflow(WorkflowFactory.WORKFLOW_NOUN_EXTRACTOR);
            
            try {
//                HtmlFetcher fetcher = new HtmlFetcher();
//                JResult res = fetcher.fetchAndExtract("http://localhost:3001/feed/humor/1", 10000, true);
                Document doc = Jsoup.connect("http://localhost:3001/feed/humor/1").get();
                Elements content = doc.select("#gaiaViewCont");
                Elements pTag = content.select("p");

                String contents = "";
                //print all titles in main page
                for(Element e: pTag){
                    System.out.println("text: " +e.text());
                    //System.out.println("html: "+ e.html());
                    contents += " " + e.text();
                }   


                /* Activate the work flow in the thread mode */
                workflow.activateWorkflow(true);

                /* Analysis using the work flow */
                String document = contents;
                workflow.analyze(document);

                LinkedList<Sentence> resultList = workflow.getResultOfDocument(new Sentence(0, 0, false));
                for (Sentence s : resultList) {
                    Eojeol[] eojeolArray = s.getEojeols();
                    for (int i = 0; i < eojeolArray.length; i++) {
                        if (eojeolArray[i].length > 0) {
                            String[] morphemes = eojeolArray[i].getMorphemes();
                            for (int j = 0; j < morphemes.length; j++) {
                                System.out.print(morphemes[j]);
                            }
                            System.out.print(", ");
                        }
                    }
                }

                workflow.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }

            /* Shutdown the work flow */
            workflow.close();  	
            return "bye";
        });
    }
}
