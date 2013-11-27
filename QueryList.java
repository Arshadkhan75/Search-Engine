






import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bili
 */
public class QueryList {
    public List<String> queries;

    public QueryList(String name) throws FileNotFoundException {
        queries = this.importWordList(name);
        
    }
    
    private List<String> importWordList(String name) throws FileNotFoundException {
        List<String> wordList = new ArrayList<String>();
        Scanner in = new Scanner(new File(name));
        String line = "";
        while(in.hasNext()) {
            line = in.nextLine();
            if(line.charAt(0)!= '*')
                wordList.add(line);
        }
        return wordList;
        
    }

    public List<String> getQueries() {
        return queries;
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        QueryList qList = new QueryList("time.queries");
        List<String> q = qList.getQueries();
        for(String s : q){
            System.out.println(s.charAt(s.length()-1));
        }
        System.out.println(q.size());
    }
}
