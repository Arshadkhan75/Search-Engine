

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import util.DocumentScorePair;
import util.OrderByScore;
import util.TopScoreDocs;

/**
 *
 * @author bili
 */
public class PrecisionAndRecall {

    public static void main(String[] args) throws CorruptIndexException, IOException {
        String indexDir = "indexDir";
        String docs = "docs";
        String query = "KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP SUPPRESSING THE BUDDHISTS";

        Directory dir = FSDirectory.open(new File(indexDir));
        IndexReader indexReader = IndexReader.open(dir);

        BM25 bm25 = new BM25(indexReader, docs);

        QueryList qList = new QueryList("time.queries");
        RelevantList relList = new RelevantList("time.rel");
//        for(int i = 0; i<qList.queries.size(); i++) {
//            System.out.println((i+1)+" "+qList.queries.get(i));
//        query = qList.queries.get(0);
        String rels = "";
//        TopScoreDocs score = bm25.score(query);

//            score.top(10);
//            System.out.println();
        String ans = "";
        for (int j = 0; j < qList.queries.size(); j++) {
            String str = "";
            query = qList.queries.get(j);
            rels = relList.queries.get(j);
            TopScoreDocs score = bm25.score(query);
            ans = score.allHits();
            String[] words = rels.split(" ");
//            for (int i = 0; i < words.length; i++) {
////                    if(ans.c)
////                    System.out.println(words[i]);
//                
////                ans = ans.replaceAll(" " + words[i] + " ", " <b>" + words[i] + "</b> ");
//            }
            String[] splits = ans.split((" "));
            int counter = rels.split((" ")).length;

//            String[] ansWord = ans.split(( " "));
            for (int k = 0; k < splits.length; k++) {
                str+=" "+splits[k];
                
                for (int i = 0; i < words.length; i++) {
//                    System.out.println(words[i]);
                    if (splits[k].equals(words[i])) {
                        counter--;                        
                    }           
//                    System.out.println(counter);
                }                
                if(counter ==0) break;

                

            }
//            System.out.println(str);
            
            str+= " ";
            int retrieved = str.split(" ").length -2;
            for (int l = 0; l < words.length; l++) {
//                System.out.print(words[l]+ " ");
////                    if(ans.c)
////                    System.out.println(words[i]);
//                
                str = str.replaceAll(" " + words[l] + " ", " [ " + words[l] + " ] ");
            }
            int relevant = 0;
            for(int i =0; i<str.length(); i++) {
                if(str.charAt(i)=='[') {
                    relevant++;
                }
            }
            System.out.println((j+1)+ " " +  " : retrieved "+ retrieved + 
                    " relevant "+relevant + " know relevant " + words.length);
//            System.out.println((j+1) + "  "+ans);
//                   System.out.println(ans.replaceAll("304", "*"));
//        String str = target.replaceAll("304", "*");
//        System.out.println(str);
        }
    }
//        }
}
