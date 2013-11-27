

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author bili
 */
public class RelevantList {

    public List<String> queries;

    public RelevantList(String name) throws FileNotFoundException {
        queries = this.importWordList(name);

    }

    private List<String> importWordList(String name) throws FileNotFoundException {
        List<String> wordList = new ArrayList<String>();
        Scanner in = new Scanner(new File(name));
        String line = "";
        while (in.hasNext()) {
            line = in.nextLine();
            if (!line.equals("")) {
//            System.out.println(line);
                String[] words = line.split(" ");
                line = "";
            for(int i =1; i<words.length; i++) {
//                System.out.print(words[1]+ " ");
//                System.out.println(words[1]);
                if(!words[i].equals(""))
                    line+=words[i]+" ";
            }
//            System.out.println();
                wordList.add(line);
            }
        }
        return wordList;

    }

    public List<String> getQueries() {
        return queries;
    }

    public static void main(String[] args) throws FileNotFoundException {
        RelevantList qList = new RelevantList("time.rel");
        List<String> q = qList.getQueries();
        for(String s : q){
//            System.out.println(s.charAt(s.length()-1));
            System.out.println(s);
        }
//        System.out.println(q.size());
//        String s = "1  268 288 304 308 323 326 334";
//        String[] ss = s.split(" ");
//        for(int i = 0; i<ss.length; i++) {
//            System.out.println(ss[i]);
//        }
    }
}