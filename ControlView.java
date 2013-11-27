/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//import RecallAndPrecision.BM25;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.lucene.index.CorruptIndexException;
import util.DocumentScorePair;
import util.TopScoreDocs;

/**
 *
 * @author bili
 */
public class ControlView extends JFrame {

    private void resAll() {
        searchPane.revalidate();
        fullView.revalidate();
        bottomPane.revalidate();;
        leftPane.revalidate();
        this.validate();
    }

    private BM25 bm25; // model
    private SearchPanel searchPane;
    private OnePage leftPane;
    private FullView fullView;
    private int currentClick = 0;
    private BottomPane bottomPane;
//    private SpellChecking spellChecker;
    private CheckerSuggestion checkSuggestions;

    public ControlView(BM25 bm25) throws IOException {
        this.bm25 = bm25;
//        spellChecker = new SpellChecking("spellChecker");
        checkSuggestions = new CheckerSuggestion("spellChecker");

        bottomPane = new BottomPane();
        searchPane = new SearchPanel();

        leftPane = new OnePage();
//        leftPane.d
//        leftPane.setPreferredSize(new Dimension(10, 100));
//        leftPane.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));


        fullView = new FullView(" ");
        setLayout();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      frame.getContentPane().add(hit);
//        frame.add(hit);
        //Set up the content pane.
        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        this.setPreferredSize(new Dimension(900, 600));
        this.pack();
        this.setVisible(true);
    }

    private void setLayout() {
        Container pane = this.getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 0;

//        c.weighty = 0.5;
        pane.add(searchPane, c);

        c.fill = GridBagConstraints.BOTH;
////        c.ipady = 40;      //make this component tall
//        c.weightx = 1.0;
//        c.weighty = 1.0;
//        c.gridwidth = 1;
//        c.gridx = 0;
//        c.gridy = 1;
//        c.ipady = 80;      //make this component tall
        c.weighty = 1.0;
//        c.weighty = 1.0;

        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 1;
        pane.add(leftPane, c);


        c.fill = GridBagConstraints.BOTH;

        c.weighty = 1.0;
        c.gridx = 1;
        c.gridwidth = 1;
        c.gridy = 1;
        pane.add(fullView, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;      //reset
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        pane.add(bottomPane, c);

    }
    // for right panel to display full document

    private class FullView extends JPanel {

        JTextPane textPane = new JTextPane();
        //    private String content; // a html style string
        private JScrollPane scrollPane;
        JTextArea textArea = new JTextArea(6, 20);

        public FullView(String content) {
//            if(content.equals("")) {
//                padded();
//            }
            //        txt = new JTextPane();
            //        this.content = content;
            textPane.setEditorKit(new HTMLEditorKit());
            textArea = new JTextArea();
            textArea.setEnabled(false);
            textArea.setEditable(false);
            textArea.setText(content);
            textArea.setEnabled(false);
            textArea.setEditable(false);

            textPane.setText(textArea.getText());
            textPane.setEditable(false);
            scrollPane = new JScrollPane(textPane);

            
            this.setLayout(new BorderLayout());
            this.add(scrollPane, BorderLayout.CENTER);
//            setBorder(BorderFactory.createLineBorder(Color.ORANGE));


        }

        public void set(String newContent) {
//            if(newContent.equals("")){
//                  padded();                                                
//            }
            textArea.setText(newContent);
            textPane.setText(textArea.getText());

            this.revalidate();
        }
        
//        private void padded() {
//            textArea.setText(("                                                                                                                                                      "));                                                                                                               
//        }
    }

    private class SearchPanel extends JPanel implements ItemListener {

        JCheckBox spellOn;
        JButton search;
        public JTextField searchBox;
        private JPanel top;
//        private JPanel bottom;
        public CheckerSuggestion checkSuggestions;

        private SearchPanel() throws IOException {
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            spellOn = new JCheckBox();
            spellOn.setSelected(true);
            top = new JPanel();
            checkSuggestions = new CheckerSuggestion("spellChecker");
//            bottom.add(new JTextArea("Did you mean:"));
            search = new JButton("Search");
            searchBox = new JTextField(30);

            search.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //Execute when button is pressed
                    String query = searchBox.getText();
                    
                    try {
                        checkSuggestions.check(query);
                        checkSuggestions.revalidate();
//                        leftPane.revalidate();
//                        checkSuggestions.refreshPane();
                    } catch (IOException ex) {
                        Logger.getLogger(ControlView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("Query: " + query);
                    try {
                        fullView.set("");
                        currentClick = 0;
                        TopScoreDocs score = bm25.score(query);
                        try {
                            //                        addLeftPane();
                                                    leftPane.addTopScoreDoc(score);
                        } catch (Exception ex) {
                            Logger.getLogger(ControlView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        score.top(10);
                    } catch (FileNotFoundException ex) {
                    } catch (CorruptIndexException ex) {
                    } catch (IOException ex) {
                    }
                }
            });
//            spellOn.addActionListener(new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println(e.paramString());
////                    if(spellOn.get)
//                    spellOn.setSelected(false);
//                    
//                }
//            });
            spellOn.addItemListener(this);


            top.add(searchBox);
            top.add(search);
            top.add(spellOn);
            top.add(new JLabel("Spell checker on"));
            this.add(top);
            this.add(checkSuggestions);
//            setBorder(BorderFactory.createLineBorder(Color.ORANGE));

        }

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
//            AbstractButton abstractButton = (AbstractButton) itemEvent.getSource();
            int state = itemEvent.getStateChange();
            if (state == ItemEvent.SELECTED) {
//                this.add(bottom);
                checkSuggestions.setVisible(true);
                this.revalidate();
            } else if (state == ItemEvent.DESELECTED) {
//                this.add(bottom);
                checkSuggestions.setVisible(false);
                this.revalidate();
            }
        }
    }
    // inner class for the left pane to display serach result

    public class OnePage extends JPanel {

        private class OneHit extends JPanel {

//            private String displayHTML;
            private String title;
            private String content;
            private HtmlPane contentArea;
            private HtmlPane titleField;

            public OneHit(String title, final String content) {
//                super();
                this.title = title;
                this.content = content;
                contentArea = new HtmlPane(content, false);
                contentArea.setEditable(false);
                titleField = new HtmlPane(title, true);
                this.setLayout(new BorderLayout());
                this.add(titleField, BorderLayout.PAGE_START);
                this.add(contentArea, BorderLayout.CENTER);
//                this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            }
//            private HTML
        }
        private List<OneHit> hitList;
        private int Result_per_page = 7;
        private JPanel body;
//        private int currentClick = 0;

        private OnePage(List<OneHit> hitList) {
            this.hitList = hitList;
            this.setLayout(new BorderLayout());
//        this.add(body, BorderLayout.CENTER);
            this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            disStuff();
        }

        private OnePage() {
            this.hitList = new ArrayList<OneHit>();
            this.setLayout(new BorderLayout());
//        this.add(body, BorderLayout.CENTER);
            this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            disStuff();
        }

//        private void disStuff() {
////            if(hitList>)
//            for (int i = 0; i < 5; i++) {
//                this.add(hitList.get(i));
//            }
//            this.validate();
//        }
        private void disStuff() {
//            this.revalidate();
            try {
                this.remove(body);
            }catch(NullPointerException e){}
//            
            System.out.println("no of hits " +hitList.size());
            if ((Result_per_page * currentClick + Result_per_page) < hitList.size()) {
//                System.out.println("IF");
//                try {
////                    System.out.println("body is "+body);
////                    if(body!=null) {
////                        body.setVisible(false);
////                        body.revalidate();
////                    }
//                } catch (NullPointerException e) {
//                }
//                
                body = new JPanel();
                
//                body.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
                for (int i = Result_per_page * currentClick; i < Result_per_page * currentClick + Result_per_page; i++) {
                    body.add(hitList.get(i));
                }
                body.revalidate();
                this.add(body, BorderLayout.CENTER);
//                this.add(body);
                this.revalidate();
            } else if (Result_per_page * currentClick < hitList.size()) {
//                System.out.println("ELSE");
//                this.remove(body);
//                this.revalidate();
                int newEnd = (Result_per_page * currentClick + Result_per_page) - hitList.size();
//                body.setVisible(false);
                body = new JPanel();
//                body.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));
                for (int i = Result_per_page * currentClick;
                        i < Result_per_page * currentClick + Result_per_page - newEnd; i++) {
                    body.add(hitList.get(i));
                }
                body.revalidate();
                this.add(body, BorderLayout.CENTER);
//                this.add(body);
                this.revalidate();
            }
//            this.validate();
//            this.repaint();
//            this.revalidate();
        }

        public void addTopScoreDoc(TopScoreDocs tops) throws IOException, Exception {
//            for(OneHit one: hitList) {
//                Container parent = one.getParent();
//                
            try {
                    this.remove(body);
                this.revalidate();
//                body.setVisible(false);
                body.revalidate();
                this.revalidate();
//                body.validate();
//                body.repaint();
            } catch (NullPointerException e) {
                this.revalidate();
            }
//                one.setVisible(false);


            this.hitList.clear();
            List<DocumentScorePair> list = tops.getList();
            for (DocumentScorePair d : list) {
//                String s = "" + d.score;
                String s = bm25.getSnippets(searchPane.searchBox.getText(), d.name);
                String ss = " "+s; //dirty hack...
//                System.out.println("s "+s);
                ss= bm25.htmlString(ss, searchPane.searchBox.getText());
                hitList.add(new OneHit(d.name, ss));
            }
            disStuff();
            this.revalidate();
        }
    }

    public class BottomPane extends JPanel {

        private JButton next, previous;

        public BottomPane() {
            next = new JButton("Next");
            previous = new JButton("Previous");
            next.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (leftPane.hitList.size() > 0 && currentClick * leftPane.Result_per_page + leftPane.Result_per_page < leftPane.hitList.size()) {
                        currentClick++;
                    }
                    fullView.set((" "));
                    System.out.println(currentClick);
                    leftPane.disStuff();
                }
            });

            previous.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentClick > 0 && (leftPane.hitList.size() > 0)) {
                        currentClick--;
                    }
                    fullView.set((" "));
                    System.out.println(currentClick);
                    leftPane.disStuff();
                }
            });
            this.add(previous);
            this.add(next);

        }
    }

    public class HtmlPane extends JTextPane {

        private JTextArea textArea;
        private String content; // a html style string
        private String displayUnderline;
//        private JScrollPane scrollPane ;
        private MyAdapter myadapter;

        public HtmlPane(String text, boolean on) {
            this.setEditorKit(new HTMLEditorKit());
            textArea = new JTextArea(6, 20);
            this.content = text;

            textArea.setEditable(false);
            textArea.setText(underLineTitle());
            this.setText(content);


            if (on) {
                this.setText(underLineTitle());
                myadapter = new MyAdapter();
                this.addMouseListener(new MyAdapter());
            }

        }

        private String underLineTitle() {
            displayUnderline = "<u>" + content + "</u>";
            return displayUnderline;
        }

//        public void dis() {
//            this.removeMouseListener(myadapter);
//            this.validate();
//        }
        public class MyAdapter extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent event) {
                try {
//                    System.out.println("prsssed");
//                    System.out.println(bm25.getContent(content));
//                    fullView.set(bm25.getContent(content));
                    fullView.set(bm25.htmlGetContent(content, searchPane.searchBox.getText()));
//                        fullView = new FullView(bm25.getContent(content));
                } catch (IOException ex) {
//                    Logger.getLogger(ControlView.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        public HtmlPane(boolean on) {

            this.content = "";
            this.setEditorKit(new HTMLEditorKit());
            this.setText(content);
            if (on) {
                this.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent event) {
                        try {
//                            System.out.println("prsssed");
//                            System.out.println(bm25.getContent(content));
                            fullView.set(bm25.getContent(content));
//                            fullView = new FullView(bm25.getContent(content));
                        } catch (IOException ex) {
//                            Logger.getLogger(ControlView.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
            }
        }

        public void set(String content) {
            this.content = content;
            this.setEditorKit(new HTMLEditorKit());
            this.setText(content);
            this.revalidate();
        }
    }

    private class CheckerSuggestion extends JPanel {

        public SpellChecking checker;
        private  JPanel con;
        private boolean reset = false;
        String[] suggests_;
// Integer tmp ;

        public CheckerSuggestion(String dicDir) throws IOException {
//            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            con = new JPanel();
//            con.setBorder(BorderFactory.createLineBorder(Color.RED));
            checker = new SpellChecking(dicDir);
//            con.add(new JLabel("Did you mean:"));
            this.add(con);
        }

        public void check(String word) throws IOException {
            word = word.toLowerCase();
            suggests_ = checker.suggests(word);
//            con = new JPanel();
            try {
                this.remove(con);
                con = new JPanel();
                this.add(con);
            }
            catch(NullPointerException e) {}
            
            if (suggests_.length>1) {
                con.add(new JLabel("Did you mean:"));
                con.revalidate();
            }
            for (int i = 0; i < suggests_.length; i++) {
                final Integer tmp = new Integer(i);
                final JLabel label = new JLabel("<html><u>" + suggests_[i] + "</u></html>");
//                label.addMouseListener(this);
//                    label.setFont(new Font("Serif", Font.BOLD, 24));
                label.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        refreshPane();
// 
                        searchPane.searchBox.setText(suggests_[tmp.intValue()]);

                        try {
                            fullView.set("");
                            currentClick = 0;
                            TopScoreDocs score = bm25.score(searchPane.searchBox.getText());
                            try {
                                //                            addLeftPane();
                                //                            leftPane =new OnePage();
                                                            leftPane.addTopScoreDoc(score);
                            } catch (Exception ex) {
                                Logger.getLogger(ControlView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            score.top(10);
                            leftPane.revalidate();

                        } catch (FileNotFoundException ex) {
                        } catch (CorruptIndexException ex) {
                        } catch (IOException ex) {
                        }

                    }
                });

//            System.out.println("MEMMEMEMME2");
                con.add(label);
            }

            this.revalidate();

        }

        public void refreshPane() {
//            System.out.println("refreshing");

            try{
                this.remove(con);
            }catch (NullPointerException e){}
//            con = new JPanel();
//            con.setBorder(BorderFactory.createLineBorder(Color.pink));
//            this.add(con);
            this.revalidate();
        }
    }
}
