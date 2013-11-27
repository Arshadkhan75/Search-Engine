/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import RecallAndPrecision.BM25;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import util.TopScoreDocs;

/**
 *
 * @author bili
 */
public class Main {

    public static void main(String[] args) throws CorruptIndexException, IOException {
        SpellChecking checker = new SpellChecking("spellChecker");
        String indexDir = "indexDir";
        String docs = "docs";
//        String query = "KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP SUPPRESSING THE BUDDHISTS";
////        String query = "EFFORTS OF AMBASSADOR HENRY CABOT LODGE TO GET VIET NAM'S PRESIDENT DIEM TO CHANGE HIS POLICIES OF POLITICAL REPRESSION";
////        String query = "viet";
//        String field = "contents";
//        String stopwordsFile = "time.stop";
//        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
//        StopWordList swl = new StopWordList(name);
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexReader indexReader = IndexReader.open(dir);
        BM25 bm25 = new BM25(indexReader, docs);
        
        if (args.length == 2) {
            TopScoreDocs score = bm25.score(args[0]);
            String hitsContent = score.allHits();
            System.out.println(score.allHits());
            // output to a file
            File file = new File(args[1] + ".txt");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(hitsContent);
            bw.close();

            System.out.println("Done");

        }
        // run
        ControlView controlView = new ControlView(bm25);

    }
}
