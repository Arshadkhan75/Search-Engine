


//import RecallAndPrecision.StopWordList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
//import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author binh
 */
public class Indexing {
    
    private IndexWriter writer;
    public Indexing(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
           String name = "time.stop";
        
            StopWordList swl = new StopWordList(name);
        Set<String> words = swl.getWords();
        Analyzer stdAn = new StandardAnalyzer(Version.LUCENE_36, words);
//        Analyzer stdAn = new SnowballAnalyzer(Version.LUCENE_36, "English", words);
//        Analyzer ltcAn = new LimitTokenCountAnalyzer(stdAn,Integer.MAX_VALUE);
        IndexWriterConfig iwConf = new IndexWriterConfig(Version.LUCENE_36,stdAn);
        iwConf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        writer = new IndexWriter(dir, iwConf);
    }
    
    public void close() throws IOException {
        writer.close();
    }
    
    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
        
        for (File f: files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()
                    && (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }
        return writer.numDocs(); // return number of docs indexed
    }
    
    private void indexFile(File f) throws Exception {
        System.out.println("Indexing "+ f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
    
    private static class TextFilesFilter implements FileFilter{
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith("");
        }
    }
    
    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new Field("contents", new FileReader(f),Field.TermVector.WITH_POSITIONS_OFFSETS));
        FileReader fr = new FileReader(f);
        
//        fr.
//        doc.add(new Field("text", new FileReader(f),Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("filename",  f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
//        doc.add(new Field("wordlength", fileWordLength(f), Field.Store.YES, Field.Index.NOT_ANALYZED));
        return doc;
    }
    private String fileWordLength(File file) throws IOException{
        int l = 0;
        String line ="";
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getCanonicalPath()));
            while((line=in.readLine())!=null) {
                l+=line.split((" ")).length;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l+"";
    }
    
    public static void main(String[] args) throws Exception{
        String indexDir = "indexDir";
        String dataDir = "docs";
        
        long start = System.currentTimeMillis();
        int numIndexed;
        Indexing indexer = new Indexing(indexDir);
        try{
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally{
        indexer.close();
        
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + "milliseconds");
        
    }
    
}
