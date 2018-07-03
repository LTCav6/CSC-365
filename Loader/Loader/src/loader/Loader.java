/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * Loader.java
 ****************************************** */
package loader;

import LCGraphs.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Luke
 */
public class Loader {

    static String rootsPath = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/roots.txt";
    static File rootsFile = new File(rootsPath.toString());
    static String sitesPath = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/sites.txt";
    static File sitesFile = new File(sitesPath.toString());
    //declare pathway of where serialized files will be written                  
    static String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/";
    static String cacheExtension = ".ser"; //for adding file extension 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        Scanner scan = new Scanner(System.in);

        //local variables 
        Document doc; //create variable to store the doc version
        String site = ""; //temp variable for the sites
        ArrayList<String> siteList = new ArrayList<String>();//create arraylist to hold sites
        File dir = new File("/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites");
        File[] directoryListing = dir.listFiles();

        //if the sites file exists and URL's do not need to be scraped
        if (new File(sitesPath).exists()) {
            System.out.println("Press 1 to update sites or 2 to skip");
            int ctrl = scan.nextInt();

            if (ctrl == 1) {
                //System.out.println("Sites path found");
                //create a new InfoParser and send it the URLs
                InfoParser URLparser = new InfoParser(sitesFile);
                //convert URLs into an arraylist 
                siteList = URLparser.getSites();

                int updateCounter = 0;
                //for each URL in siteList
                for (String siteURL : siteList) {

                    if (siteURL == null) {
                        siteURL = "https://www.google.com";
                    }
                    //if theres a cached file                 
                    String fileName = URLparser.convertName(siteURL);
                    fileName = fileName.replaceAll("\n", "");
                    fileName = cacheFolder + fileName + ".ser";

                    //System.out.println("Found" + fileName);
                    File seriFile = new File(fileName);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                    if (seriFile.exists()) {
                        //if the page needs updating

                        URL url = new URL(siteURL);
                        URLConnection connection = url.openConnection();

                        try {
                            String lastModifiedURL = connection.getHeaderField("Last-Modified");
                            Date urlTimeStamp = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss").parse(lastModifiedURL);
                            //System.out.println("URL timestamp : " + connection.getHeaderField("Last-Modified"));

                            Date seriFileStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(sdf.format(seriFile.lastModified()));
                            //System.out.println("Serialized timestamp : " + sdf.format(seriFile.lastModified()));

                            if (urlTimeStamp.compareTo(seriFileStamp) > 0) {
                                updateCounter++;
                                //System.out.println("File needs updating");

                                //delete serialized file
                                fileName = fileName.replaceAll("\n", "");
                                File f = new File(fileName);

                                f.delete();
                                //System.out.println("Deleted " + fileName);

                                //siteURL is the string for the URL
                                //fileName is string for file 
                                InfoParser updater = new InfoParser(sitesFile);
                                doc = updater.getHTML(siteURL);
                                //System.out.println("Scraping " + siteURL);
                                FrequencyTable replacement = new FrequencyTable(siteURL);
                                replacement = updater.scrape(doc);
                                replacement.setName(siteURL);

                                updater.serializeTable(replacement);
                                //System.out.println("Table written with name: " + replacement.getName());

                                //convert URL back to HTML 
                                //scrape page
                                //serialize   
                            }
                        } catch (NullPointerException n) {
                        }
                    }
                }
            }
            /**
             * **************************************************************************************
             */
            //CREATE BTREEE
            //create new btree
            BTree tree = new BTree(0);

            //if directory exists, load each file into btree
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    //adds to btree
                    tree.put(child.getName());

                }
            }

            String string = tree.toString();
            ArrayList<String> treeSites = new ArrayList<String>(Arrays.asList(string.split("#")));
            treeSites = cleanTree(treeSites);

            //write btree to disk 
            ArrayList<String> writeBlock = new ArrayList<String>();

            int index = 0;
            for (String s : treeSites) {
                // System.out.println("Element: " + s);
                if (index != 7) {
                    //    System.out.println("Inserting into array at: " + index);
                    writeBlock.add(s);
                    //      System.out.println("Successfully added");
                    index++;
                } else {
                    //      System.out.println("Okay made it this far");
                    tree.writeToDisk(writeBlock);
                    //       System.out.println("Full array is " + writeBlock.toString());
                    //        System.out.println("Index is now" + index + " clearing");
                    index = 0;
                    writeBlock.clear();

                }
            }

            if (index != 7) {
                for (int i = index; i < 7; i++) {
                    //    System.out.println("Supplementing");
                    writeBlock.add("#############");

                }
                //  System.out.println("WriteBock : " + writeBlock.toString());
                tree.writeToDisk(writeBlock);
            }

            /**
             * *****************************************************************************
             */
//CREATE GRAPH
//            //System.out.println(x.getURLs().toString());
//            Graph graph = new Graph();
//            ArrayList<FrequencyTable> tbls = new ArrayList<FrequencyTable>();
//            InfoParser newlinks = new InfoParser();
//
//            if (directoryListing != null) {
//                for (File child : directoryListing) {
//                    FileInputStream fil = new FileInputStream(cacheFolder + child.getName());
//                    ObjectInputStream in = new ObjectInputStream(fil);
//                    FrequencyTable tmp = (FrequencyTable) in.readObject();
//                    tbls.add(tmp);
//                    Vertex vertex = new Vertex(newlinks.convertName(tmp.getName()) + ".ser");
//                    graph.addVertex(vertex, false);
//                    in.close();
//                }
//            }
            //System.out.println(graph.vertexKeys());
            //System.out.println(graph.
//            for (FrequencyTable table : tbls) {
//                String tblName = newlinks.convertName(table.getName()) + ".ser";
//                Set<String> sublinks = table.getURLs();
//                for (String s : sublinks) {
//                    String t = newlinks.convertName(s);
//                    t = t + ".ser";
//                    //System.out.println(t);
//                    if (graph.getVertex(t) != null) {
//                        //System.out.println("Child found: " + t);
//                        //System.out.println(graph.getVertex(tblName));
//                        //System.out.println(graph.getVertex(t));
//                        graph.addEdge(graph.getVertex(tblName), graph.getVertex(t));
//
//                    }
//                }
//            }
//            GraphHandler g = new GraphHandler();
//            g.loadVertices(dir);
//            g.loadEdges();
//            g.checkSpanningTrees();
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
            
            System.out.println("Computing weights");
            //dijkstra.computeWeights();
            System.out.println("done!");
            
            System.out.println("Saving files...");
            dijkstra.save();
            System.out.println("done!");
            
            
            
            

//            ObjectOutputStream oos1;
//            //System.out.println("/home/luke/Documents/NetBeansProjects/Loader/src/loader/graph/" + tblName);
//            FileOutputStream fout = new FileOutputStream("/home/luke/Documents/NetBeansProjects/Loader/src/loader/graph/" + "graph.ser");
//            System.out.println("Writing file");
//            oos1 = new ObjectOutputStream(fout);
//            oos1.writeObject(dijkstra);
//            oos1.close();
//            fout.close();
//            //System.out.println("printingh graph");
            //g.printGraph();
//            for (FrequencyTable table : tbls) {
//                String tblName = newlinks.convertName(table.getName()) + ".ser";
//                Vertex c = (graph.getVertex(tblName));
//                ObjectOutputStream oos1;
//                System.out.println("/home/luke/Documents/NetBeansProjects/Loader/src/loader/graph/" + tblName);
//                FileOutputStream fout = new FileOutputStream("/home/luke/Documents/NetBeansProjects/Loader/src/loader/graph/" + tblName);
//                System.out.println("Writing file");
//                oos1 = new ObjectOutputStream(fout);
//                oos1.writeObject(c);
//                oos1.close();
//                fout.close();
//                break;
//            }
            //System.out.println("\nEdges: " + graph.getEdges());
//            System.out.println(graph.vertexKeys().size());
            //***********************************************************************************
//            //Compute spanning trees with BFS
//            String rt = "https://en.wikipedia.org/wiki/Linux";
//            String lt = "https://en.wikipedia.org/wiki/ThinkPad";
//            String xt = "https://en.wikipedia.org/wiki/IBM";
//            String yt = "https://en.wikipedia.org/wiki/OpenSUSE";
//            String pt = "https://en.wikipedia.org/wiki/Fedora_Project";
//            String ot = "https://en.wikipedia.org/wiki/CentOS";
//            String et = "https://en.wikipedia.org/wiki/Lubuntu";
//            //String rt = "https://en.wikipedia.org/";
//            rt = newlinks.convertName(rt) + ".ser";
//            lt = newlinks.convertName(lt) + ".ser";
//            xt = newlinks.convertName(xt) + ".ser";
//            yt = newlinks.convertName(yt) + ".ser";
//            pt = newlinks.convertName(pt) + ".ser";
//            ot = newlinks.convertName(ot) + ".ser";
//            et = newlinks.convertName(et) + ".ser";
            //Vertex root = new Vertex(rt);
            //graph.getVertex(rt);
            //graph.addVertex(root,true);
            //System.out.println(bfs.getAllVisited());
//            HashMap<String, Vertex> verticesX = new HashMap<String, Vertex>(graph.returnVertices());
//            HashMap<Integer, Edge> edgesX = new HashMap<Integer, Edge>(graph.returnEdges());
//            graph.resetGraph(verticesX, edgesX);
            //BFS tcp1 = new BFS(graph.getVertex(rt), graph.getVertex(lt), graph.getVertex(xt), graph.getVertex(yt), graph.getVertex(pt), graph.getVertex(ot), graph.getVertex(et));
            //System.out.println("Nodes visited: " + tcp.getAllVisited());
            //System.out.println("Size of graph: " + graph.vertexKeys().size());
            //InfoParser serGraph = new InfoParser();
            //serGraph.serializeGraph(graph);
//            Set<String> vert;
//            vert = graph.vertexKeys();
//            System.out.println(vert);
//            //for (Vertex v : vert) {
//            ObjectOutputStream oos1;
//            FileOutputStream fout = new FileOutputStream("/home/luke/Documents/NetBeansProjects/Loader/src/loader/vertices.ser");
//            oos1 = new ObjectOutputStream(fout);
//            oos1.writeObject(vert);
//            System.out.println("vertices written");
//            oos1.close();
//            fout.close();
//            // }
//            HashMap<String, Vertex> verticesy = null;
//            FileInputStream verter = new FileInputStream("/home/luke/Documents/NetBeansProjects/Loader/src/loader/vertices.ser");
//            ObjectInputStream in1 = new ObjectInputStream(verter);
//            verticesy = (HashMap) in1.readObject();
//            verter.close();
//            in1.close();
            //HashMap verticesy = (HashMap<String, Vertex>) in.readObject();
            //graph.resetGraph(verticesy, edgesX);
            //Graph graph2 = new Graph();
            //System.out.println(graph2.vertexKeys());
//            BFS tcp = new BFS(graph.getVertex(rt), graph.getVertex(lt), graph.getVertex(xt), graph.getVertex(yt), graph.getVertex(pt), graph.getVertex(ot), graph.getVertex(et));
//            System.out.println("Nodes visited: " + tcp.getAllVisited());
////
//            System.out.println("Size of graph: " + graph.vertexKeys().size());
        } /**
         * **********************************************************************************************************************
         * Else if the files have not been serialized yet
         */
        else {

            //System.out.println("Sites path not found");
            //create new InfoParser of roots
            InfoParser rootsParser = new InfoParser(rootsFile);

            //System.out.println("Calling writeSublinks");
            //call method to gather sublinks and write to sitesPath
            rootsParser.writeSublinks();

            //System.out.println("Loading sites!!!");
            InfoParser sitesParser = new InfoParser(sitesFile);

            //create the arraylist to hold all 1,000 of the Frequency Tables 
            ArrayList<FrequencyTable> masterList = new ArrayList<FrequencyTable>(sitesParser.readFile());
            //load the root file into program

            Hashtable<String, Long> table = new Hashtable<String, Long>();
            System.out.println("TABLE SIZE " + masterList.size());

            //Construct a new InfoParser with rootsFile file 
            for (FrequencyTable tmp : masterList) {
                sitesParser.serializeTable(tmp);

            }

        }

    }//end main 

    public static ArrayList<String> cleanTree(ArrayList<String> treeData) {
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

}//end class 

