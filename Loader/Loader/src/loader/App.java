/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * App.java
 ****************************************** */
package loader;

//JSOUP Libraries 
import LCGraphs.GraphHandler;
import LCGraphs.Vertex;
import static java.awt.Color.white;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import org.jsoup.nodes.Document;
//JAVAFX LIBRARIES 
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.R;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.graphstream.algorithm.Dijkstra;
import org.jsoup.nodes.Node;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 * ******************************************
 * Luke Cavanaugh CSC365 Professor Doug Lea public void start
 * ******************************************
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        //set up hbox for url input 
        HBox hbox = new HBox(50);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.setMinSize(200, 200);

        //set up text field to accept url 
        TextField urlPrompt = new TextField();
        urlPrompt.setMinSize(200, 10);
        urlPrompt.setText("https://en.wikipedia.org/wiki/Wikipedia:WPPP");
        hbox.getChildren().add(urlPrompt);

        //add button for processing 
        Button btn = new Button();
        hbox.getChildren().add(btn);
        btn.setText("Process");
        btn.setAlignment(Pos.TOP_LEFT);


        TextArea result = new TextArea();
        result.setMinSize(500, 400);
        result.setEditable(false);

        TextField output = new TextField();
        output.setMinSize(500, 10);
        output.setEditable(false);


        //this will try and grab text from the field
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //create variable to store the doc version 
                Document doc;
                FrequencyTable usersTable;
                //File dir = new File("C:\\Users\\Luke\\Documents\\NetBeansProjects\\Loader\\src\\loader\\serisites\\");
                File dir = new File("/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites");
                File[] directoryListing = dir.listFiles();
                String rootsPath = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/roots.txt";
                File rootsFile = new File(rootsPath.toString());

                String centroid = "";

                try {

                    //takes URL as a string 
                    String link = urlPrompt.getText();
                    //result.setText("Scraping website . . ");

                    //create new infoparser
                    InfoParser runner = new InfoParser();

                    //convert the users url into a doc 
                    doc = runner.getHTML(link);

                    //convert the users doc into hashtable 
                    usersTable = runner.scrape(doc);

                    //create new btree
                    BTree tree = new BTree(0);

                    tree.resetBlock();
                    boolean eof = true;
                    while (eof == true) {
                        try {
                            eof = tree.readFromDisk();
                        } catch (EOFException e) {
                            // If there are no more objects to read, return what we have.

                        }
                    }

                    ArrayList<String> masterList = new ArrayList<String>();
                    ArrayList<String> newURLs = new ArrayList<String>();
                    ArrayList<FrequencyTable> newTables = new ArrayList<FrequencyTable>();
                    masterList = tree.getAllSites();
                    masterList = cleanTree(masterList);
                    GraphHandler dijkstra = new GraphHandler();
                    System.out.println("Loading vertices");
                    dijkstra.loadVertices(dir);
                    System.out.println("done!");

                    System.out.println("Loading edges");
                    dijkstra.loadEdges(false);
                    System.out.println("done!");

                    System.out.println("Computing spanning tree");
                    dijkstra.checkSpanningTrees();
                    System.out.println("done!");

                    /*-----------------------------------*/
                    
                    GraphHandler dijk = new GraphHandler();
                    System.out.println("Loading vertices");
                    dijk.loadVertices(dir);
                    System.out.println("done!");

                    System.out.println("Loading edges");
                    dijk.loadEdges(false);
                    System.out.println("done!");

                    

                    ArrayList<Vertex> path = new ArrayList<Vertex>();
                    path = dijk.computeDijkstra(link);

                    ArrayList<String> ar = new ArrayList<String>();
                    for (Vertex v : path) {
                        ar.add(v.getLabel());
                    }

                    Graph ngraph = new SingleGraph("tutorial");

                    //Graph ngraph
                    for (int i = 0; i < ar.size(); i++) {
                        String current = ar.get(i);

                        org.graphstream.graph.Node node = ngraph.addNode(ar.get(i));

                        node.addAttribute("ui.label", ar.get(i));

                        if (i >= 1) {
                            String previous = ar.get(i - 1);
                            org.graphstream.graph.Edge e = ngraph.addEdge(("edge" + i), current, previous, eof);
                        }

                    }
                    System.out.println("1" + ar.get(0));
                    System.out.println("2" + ar.get(1));
                    // org.graphstream.graph.Edge one = ngraph.addEdge(("root"), ar.get(0), ar.get(1));

                    ngraph.addAttribute("ui.stylesheet", "graph {fill-color: gray; }");

                    //org.graphstream.algorithm.Dijkstra xcx= new Dijkstra();
                    //System.out.println(xcx.getAllPaths(ngraph.getNode(ar.get(0))).toString());
                    ngraph.display();

                } catch (IOException ex) {
                    System.out.println("IO Exception thrown in App");
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    System.out.println("Regular Exception thrown in App");
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        StackPane root = new StackPane();

        root.getChildren()
                .add(hbox);
        Scene scene = new Scene(root, 400, 400);

        //Scene shapes = new Scene(root,900,500);
        scene.getStylesheets()
                .
                add(App.class
                        .getResource(
                                "HashParser.css").toExternalForm());
        primaryStage.setTitle(
                "URL Similarity Processor (USP)");
        primaryStage.setScene(scene);
        //primaryStage.setScene(shapes);

        primaryStage.show();
    }

    public ArrayList<String> cleanTree(ArrayList<String> treeData) {
        ArrayList<String> newData = new ArrayList<String>();
        Set<String> tempSet = new HashSet<>();

        for (String s : treeData) {
            if (s == null || s.length() == 0 || s.contains("#") || !s.contains(".ser")) {
            } else {
                newData.add(s);

            }

        }

        //remove any duplicates by loading into a set and converting back to ArrayList
        tempSet.addAll(newData);
        newData.clear();
        newData.addAll(tempSet);

        return newData;
    }

    /**
     * *****************************************
     * Luke Cavanaugh CSC365 Professor Doug Lea public static void main
     * *****************************************
     */
    public static void main(String[] args) throws IOException, Exception {

        //launch GUI 
        launch(args);

    }

}
