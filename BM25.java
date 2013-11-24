

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import snippets.ReadTextFile;
import snippets.SentenceProcessor;
import snippets.SnippetGenerator;
import util.DocumentScorePair;
import util.OrderByScore;
import util.TopScoreDocs;

/**
 *
 * @author bili
 */
public class BM25 {

    private IndexReader indexReader;
    public int nD;
    private String indexDir;
    public double averageDocLength = 0;
    public String query = "";
    public TopScoreDocs scores;
    private StopWordList swl = new StopWordList("time.stop");
//    public BM25(IndexReader indexReader) throws CorruptIndexException, IOException {
//        this.indexReader = indexReader;
//        indexDir = indexReader.
//        nD = indexReader.maxDoc();
//        this.avgDocLength(indexDir);
//    }

    public BM25(IndexReader indexReader, String dir) throws CorruptIndexException, IOException {
        this.indexReader = indexReader;
        nD = indexReader.maxDoc();
        indexDir = dir;
        scores = new TopScoreDocs();
        avgDocLength(indexDir);

    }
    /*
     * Return number of docs in the collections
     */
//    public int getND() throws IOException {    
//        return indexReader.maxDoc();
//        
//    }
    /*
     * REturn the number of doc containt term tm
     */

    public int docsContainTerm(Term tm) throws IOException {
        TermDocs termDocs = indexReader.termDocs(tm);
        int count = 0;
//        System.out.println(termDocs.)
        while (termDocs.next()) {
//            System.out.println("doc() "+termDocs.doc());
//            System.out.println("freq() "+termDocs.freq());
//            count += termDocs.freq();
            if (indexReader.docFreq(tm) != 0) {
                count++;
            }
        }
//        System.out.println("count "+count);
        return count;
    }

    public double calIDF(Term term) throws IOException {
        int nq = docsContainTerm(term);
//        System.out.println("nq " +nq);
//        System.out.println(nD-nq+0.5);
//        getND();

        return Math.log((nD - nq + 0.5) / (nq + 0.5));
    }

    public String getContent(String filename) throws IOException {
        String ans = "";

//        BufferedReader br = new BufferedReader(new FileReader(indexDir + "/" + filename));
//        String currentLine;
//
//        while ((currentLine = br.readLine()) != null) {
//
//            ans += currentLine + " ";
//        }
//        ans = ReadTextFile.readTextFile(new File(filename));
//        filename = "docs/"+filename;
        try {
            String string = ReadTextFile.readTextFile(new File(indexDir + "/" + filename));
            String[] sens = SentenceProcessor.process(string);

            for (int i = 0; i < sens.length; i++) {
//                System.out.println(sens[i]);
//                System.out.println("**********************");
                ans += sens[i];
            }
        } catch (Exception e) {
        }

        return ans;
    }

    public String htmlGetContent(String filename, String query) throws IOException {
        query = query.toLowerCase();
        String ans = getContent(filename);
        String[] terms = query.split(" ");
        for (int i = 0; i < terms.length; i++) {
            if (!swl.getWords().contains(terms[i])) {
//                System.out.println(terms[i]);
                ans = ans.replaceAll(" " + terms[i] + " ", " <b style=\"color:blue\">" + terms[i] + "</b> ");
            }
        }
        return ans;
    }

    public String htmlString(String str, String word) {
        word = word.toLowerCase();
        String[] words = word.split(" ");
//        System.out.println(str);
        for (int i = 0; i < words.length; i++) {
            if (!swl.getWords().contains(words[i])) {
                str = str.replaceAll(" " + words[i] + " ", " <b style=\"color:blue\">" + words[i] + "</b> ");
            }
        }
        return str;
    }

    public int docLength(String filename) throws IOException {
        int length = 0;
//        indexReader.
        TermFreqVector t = indexReader.getTermFreqVector(0, "contents");

        for (int i = 0; i < t.getTermFrequencies().length; i++) {
//            System.out.println("t "+t.getTermFrequencies()[i]);
            length += t.getTermFrequencies()[i];
        }
        return length;
    }

    public int docLength(int id) throws IOException {
        int length = 0;
//        indexReader.
        TermFreqVector t = indexReader.getTermFreqVector(id, "contents");
//        System.out.println("size "+t.size());
//        for(int i =0; i<t.size();i++){
//            System.out.println("t "+t.getTerms()[i]);
//        }
        for (int i = 0; i < t.getTermFrequencies().length; i++) {
//            System.out.println("t "+t.getTermFrequencies()[i]);
            length += t.getTermFrequencies()[i];
        }
        return length;
    }

    public String[] listOfDocNames(String dir) throws IOException {
        File f = new File(dir);
        //        String[] fileInDir = f.listFiles();
        File[] listFiles = f.listFiles();
        String[] names = new String[listFiles.length];
        for (int i = 0; i < listFiles.length; i++) {
//            names[i] = listFiles[i].getCanonicalPath();
            names[i] = listFiles[i].getName();
        }

        return names;
    }

    public int getID(String dir, String docName) throws CorruptIndexException, IOException {
        File f = new File(dir);
        //        String[] fileInDir = f.listFiles();
        File[] listFiles = f.listFiles();
        String[] names = new String[listFiles.length];
        for (int i = 0; i < listFiles.length; i++) {
//            System.out.println(getDocID(i));
            if (docName.equals(listFiles[i].getName())) {
                return i;
            }
        }
        return -1;
    }

    public double avgDocLength(String dir) throws CorruptIndexException, IOException {
        double length = 0.0;
        String[] files = listOfDocNames(dir);

        for (int i = 0; i < files.length; i++) {
            length += docLength(getID(dir, files[i]));
        }
        averageDocLength = length / files.length;
        return length / files.length;
    }

    public String getDocID(int docId) throws CorruptIndexException, IOException {
        indexReader.numDocs();
        IndexSearcher searcher = new IndexSearcher(indexReader);
//        searcher.
        Document document = searcher.doc(docId);
        return document.get("filename");
    }

    public int tf(Term tm, String docName) throws IOException {
        TermDocs termDocs = indexReader.termDocs(tm);
        while (termDocs.next()) {
            Document doc = indexReader.document(termDocs.doc());

            if (doc.get("filename").equals(docName)) {
//                System.out.println("doc() " + termDocs.doc());
//                System.out.println("freq() " + termDocs.freq());
                return termDocs.freq();
            }
        }
        return 0;
    }

    public double part2(Term term, String docName) throws IOException {
        int tf = tf(term, docName);
//        System.out.println(tf);
        final double k1 = 1.2;
        final double b = 0.75;
        double avg = this.averageDocLength;

        int docLength = this.docLength(this.getID(indexDir, docName));
//        double up = tf * (k1 + 1);
        double up = tf;
        double down = tf + k1 * (1 - b + (b * docLength) / avg);
//        double down = tf + k1 * (1 - b + (b * docLength) / avg);
//        System.out.println(up+ " "+down);
        return up / down;
    }

    public TopScoreDocs score(String current) throws FileNotFoundException, CorruptIndexException, IOException {
        this.query = current;
        current = current.toLowerCase();
        int numDoc = indexReader.maxDoc();
        String field = "contents";
        scores = new TopScoreDocs();
//        QueryList qList = new QueryList("time.queries");
//        query = qList.queries.get(82);
//        System.out.println(current);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36, swl.getWords());
        for (int i = 0; i < numDoc; i++) {
//            for (int j = 0; j < qList.queries.size(); j++) {
            Document document = indexReader.document(i);
            double score = 0.0;
            TokenStream stream = analyzer.tokenStream(field, new StringReader(current));
            if (stream != null) {
//                OffsetAttribute offsetAttribute = stream.getAttribute(OffsetAttribute.class);
                CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);

                while (stream.incrementToken()) {
//                    int startOffset = offsetAttribute.startOffset();
//                    int endOffset = offsetAttribute.endOffset();
                    String term = charTermAttribute.toString();
                    Term tm = new Term(field, term);
                    score += this.calIDF(new Term(field, term)) * this.part2(tm, document.get("filename"));
                }
            }
            if (score > 0.0) {
                scores.add(new DocumentScorePair(document.get("filename"), score));
            }
        }
        Collections.sort(scores.getList(), new OrderByScore());
        return scores;
    }
    /*
     * Method to get content of the snippets
     */

    public String getSnippets(String query, String docID) throws IOException, Exception {
        String ans = "";
//        String content = this.getContent(docID);
//        String[] terms = query.toLowerCase().split(" ");
//        for (int i = 0; i < terms.length; i++) {
////            System.out.println(content.indexOf(terms[i]));
//            if (!swl.getWords().contains(terms[i])) {
//                int index = content.indexOf(terms[i]);
//                if (index != -1) {
////                System.out.println("index " + index);
//                    try {
//                        for (i = index; i < (index + 156); i++) {
//
//                            System.out.print(content.charAt(i));
//                            ans += content.charAt(i);
//
//                        }
//                    } catch (StringIndexOutOfBoundsException e) {
//                    }
//                }
//            }
//        }
        ans = SnippetGenerator.generate(docID, query);
        return ans;
    }
//    public void setQuery(String current) {
//        query = current;
//    }

    public static void main(String[] args) throws CorruptIndexException, IOException {
        if (args.length == 0) {

            System.out.println("No Command Line arguments");

        } else if (args.length == 2) {
            String indexDir = "indexDir";
            String docs = "docs";
//        String query = "KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP SUPPRESSING THE BUDDHISTS";
////        String query = "EFFORTS OF AMBASSADOR HENRY CABOT LODGE TO GET VIET NAM'S PRESIDENT DIEM TO CHANGE HIS POLICIES OF POLITICAL REPRESSION";
////        String query = "viet";
//        String s = "diem (capricorn), and after what the u.s . officially called his \" brutal \" crackdown on the buddhists, washington obviously could not string along with him";
            Directory dir = FSDirectory.open(new File(indexDir));
            IndexReader indexReader = IndexReader.open(dir);
//
            BM25 bm25 = new BM25(indexReader, docs);
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

        } else {
            System.out.println("Unknown parameters. Enter a query and a filename for ouput");
        }


//        System.out.println(bm25.htmlString(s, "diem"));
//        bm25.score("KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP SUPPRESSING THE BUDDHISTS");
//        bm25.scores.top(10);
//        System.out.println(bm25.getContent("171"));
//        System.out.println(bm25.htmlGetContent("171", query.toLowerCase()));
    }
}
