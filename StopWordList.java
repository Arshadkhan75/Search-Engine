







import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

/**
 *
 * @author bili
 */
public class StopWordList {
    
    private Set<String> words;
    

    public StopWordList(String name) throws FileNotFoundException {
        words = this.importWordList(name);
        
    }
    
    private Set<String> importWordList(String name) throws FileNotFoundException {
        Set<String> wordList = new HashSet<String>();
        Scanner in = new Scanner(new File(name));
        String line = "";
        while(in.hasNext()) {
            line = in.nextLine();
            if(!line.equals("") )
                wordList.add(line);
        }
        
        return wordList;
        
    }

    public Set<String> getWords() {
        return words;
    }
    
    
    
//    public static void main(String[] args) {
//        String name = "time.stop";
//        try {
//            StopWordList swl = new StopWordList(name);
//            System.out.println(swl.getWords().toString());
//            Analyzer az = new StandardAnalyzer(Version.LUCENE_36, swl.getWords());
//        } catch (FileNotFoundException ex) {
//            
//        }
//    }
    
}
