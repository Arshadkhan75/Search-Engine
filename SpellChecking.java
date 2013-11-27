

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Arshad 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SpellChecking {

    PlainTextDictionary dictionary;
    SpellChecker spellChecker;
    StandardAnalyzer analyzer;

    public SpellChecking(String spellDir) throws IOException {
//      File dir = new File("spellchecker");
        File dir = new File(spellDir);
        Directory directory = FSDirectory.open(dir);
        spellChecker = new SpellChecker(directory);
        analyzer = new StandardAnalyzer(Version.LUCENE_36);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);

        InputStreamReader rs = new InputStreamReader(new FileInputStream(new File("dictionary.txt")), "UTF-8");

        dictionary = new PlainTextDictionary(rs);

        // spellchecker index.
     spellChecker.indexDictionary(dictionary, config, true);
        //word for suggestion.
    }

    public String[] suggests(String wordForSuggestions) throws IOException {
        String ans = "";

        if (wordForSuggestions.split(" ").length == 1) {
            String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, 4);
//       spellChecker.

            if (suggestions != null && suggestions.length > 0 && !spellChecker.exist(wordForSuggestions)) {
                for (String word : suggestions) {
//                if(!spellChecker.exist(word)){
                    //                spellChecker.setStringDistance(null)
                    float distance = spellChecker.getStringDistance().getDistance(word, wordForSuggestions);
                    //                System.out.println(distance);
                    //                System.out.println("Did you mean:" + word);
                    //                spellChecker.
                    ans += word + " ";
//                }
                }
            } else {
                System.out.println("No suggestions found for word:" + wordForSuggestions);
            }
        }
        System.out.println(ans);
        return ans.split(" ");

    }

//    public static void main(String[] args) throws IOException {
////        SpellChecking checker = new SpellChecking("spellChecker");
////        checker.suggests("cotroversy");
//    }
}