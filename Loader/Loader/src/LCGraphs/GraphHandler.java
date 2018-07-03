
package LCGraphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import loader.*;
import static loader.InfoParser.writer;


/**
 *
 * @author luke
 */
public class GraphHandler implements java.io.Serializable {

    Graph graph;
    ArrayList<FrequencyTable> tbls = new ArrayList<FrequencyTable>();
    File edgePath = new File("/home/luke/Documents/NetBeansProjects/Loader/src/loader/edges/");
    File[] edgeFile = edgePath.listFiles();

    public GraphHandler() {
        graph = new Graph();

    }

    public GraphHandler(Graph g) {
        graph = new Graph();
        graph.resetGraph(g.returnVertices(), g.returnEdges());

    }

    public void loadVertices(File dir) throws FileNotFoundException, IOException, ClassNotFoundException {
        String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/";
        File[] directoryListing = dir.listFiles();
        //File directoryListing;
        //directoryListing = dir;

        InfoParser newlinks = new InfoParser();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                FileInputStream fil = new FileInputStream(cacheFolder + child.getName());
                ObjectInputStream in = new ObjectInputStream(fil);
                FrequencyTable tmp = (FrequencyTable) in.readObject();
                tbls.add(tmp);
                Vertex vertex = new Vertex(newlinks.convertName(tmp.getName()) + ".ser");
                graph.addVertex(vertex, false);
            }
        }
    }

    public FrequencyTable getTable(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/";
        FileInputStream fil = new FileInputStream(cacheFolder + fileName);
        ObjectInputStream in = new ObjectInputStream(fil);
        FrequencyTable tmp = (FrequencyTable) in.readObject();
        return tmp;
    }

    public void loadEdges(boolean tf) throws FileNotFoundException, IOException, ClassNotFoundException {
        Scanner in = new Scanner(new FileReader(edgePath + ".txt"));
        String tmpEdge;
        String[] parts;
        int foo;

        if (tf == false) {

            while (in.hasNext()) {
                tmpEdge = in.next();
                parts = tmpEdge.split("#");

                graph.addEdge(graph.getVertex(parts[0]), graph.getVertex(parts[1]), Integer.parseInt(parts[2]));

            }
//                for (File child : edgeFile) {
//                    FileInputStream fil = new FileInputStream(edgePath.toString() + child.getName());
//                    ObjectInputStream in = new ObjectInputStream(fil);
//                    EdgeSaver tmp = (EdgeSaver) in.readObject();
//                    graph.addEdge(graph.getVertex(tmp.getOne()), graph.getVertex(tmp.getTwo()), tmp.getWeight());
//                }
        } else {
            for (FrequencyTable table : tbls) {
                InfoParser newlinks = new InfoParser();
                String tblName = newlinks.convertName(table.getName()) + ".ser";
                Set<String> sublinks = table.getURLs();
                for (String s : sublinks) {
                    String t = newlinks.convertName(s);
                    t = t + ".ser";
                    //System.out.println(t);
                    if (graph.getVertex(t) != null) {
                        //System.out.println("Child found: " + t);
                        //System.out.println(graph.getVertex(tblName));
                        //System.out.println(graph.getVertex(t));
                        graph.addEdge(graph.getVertex(tblName), graph.getVertex(t));
                    }

                }
            }

        }
    }

    public void save() throws FileNotFoundException, IOException, ClassNotFoundException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(edgePath + ".txt"), "utf-8"))) {

            for (Edge e : graph.getEdges()) {
                writer.write(e.getOne().getLabel() + "#" + e.getTwo().getLabel() + "#" + e.getWeight() + "\n");


            }
            writer.close();
        }
    }

    public boolean checkSpanningTrees() {
        InfoParser newlinks = new InfoParser();
        String rt = "https://en.wikipedia.org/wiki/Linux";
        String lt = "https://en.wikipedia.org/wiki/ThinkPad";
        String xt = "https://en.wikipedia.org/wiki/IBM";
        String yt = "https://en.wikipedia.org/wiki/OpenSUSE";
        String pt = "https://en.wikipedia.org/wiki/Fedora_Project";
        String ot = "https://en.wikipedia.org/wiki/CentOS";
        String et = "https://en.wikipedia.org/wiki/Lubuntu";
        //String rt = "https://en.wikipedia.org/";
        rt = newlinks.convertName(rt) + ".ser";
        lt = newlinks.convertName(lt) + ".ser";
        xt = newlinks.convertName(xt) + ".ser";
        yt = newlinks.convertName(yt) + ".ser";
        pt = newlinks.convertName(pt) + ".ser";
        ot = newlinks.convertName(ot) + ".ser";
        et = newlinks.convertName(et) + ".ser";
        BFS tcp = new BFS(graph.getVertex(rt), graph.getVertex(lt), graph.getVertex(xt), graph.getVertex(yt), graph.getVertex(pt), graph.getVertex(ot), graph.getVertex(et));

        if (tcp.getAllVisited() == graph.vertexKeys().size()) {
            System.out.println("All elements visited, graph is confirmed");
            return true;
        } else {
            return false;
        }
    }

    public void computeWeights() throws IOException, FileNotFoundException, ClassNotFoundException {
        InfoParser newlinks = new InfoParser();
        List<Vertex> neighbors;
        Computer compare = new Computer();
        System.out.println("Running weight updater");
        int count = 0;
        int sz = graph.getEdges().size();
        for (Edge x : graph.getEdges()) {
            FrequencyTable tbl = getTable(newlinks.convertName(x.getOne().getLabel()));
            FrequencyTable tbl2 = getTable(newlinks.convertName(x.getTwo().getLabel()));
            int weight = compare.cosineSimilarity(tbl, tbl2);
            x.setWeight(weight);
            //System.out.println(x.toString());
            count++;
            System.out.println(count + "/" + sz);
        }

//        for (String v : graph.vertexKeys()) {
//            Vertex vertex = graph.getVertex(v);
//            neighbors = vertex.getNeighborList(vertex);
//            for (Vertex otherV : neighbors) {
//                FrequencyTable tbl = getTable(v);
//                FrequencyTable tbl2 = getTable(newlinks.convertName(otherV.getLabel()));
//                int weight = compare.cosineSimilarity(tbl, tbl2);
//                Edge e = new Edge(vertex, otherV, weight);
//                graph.addEdge(vertex, otherV, weight);
//                graph.g
//                if (e.checkVisited() == false) {
//                    e.setWeight(weight);
////                    e.setVisited();
//        }
////graph.addEdge(vertex, otherV, weight);
//    }
//
    }

    public ArrayList<Vertex> computeDijkstra(String target) {
        
        InfoParser newlinks = new InfoParser();
        String rt = "https://en.wikipedia.org/wiki/Linux";
        String lt = "https://en.wikipedia.org/wiki/ThinkPad";
        String xt = "https://en.wikipedia.org/wiki/IBM";
        String yt = "https://en.wikipedia.org/wiki/OpenSUSE";
        String pt = "https://en.wikipedia.org/wiki/Fedora_Project";
        String ot = "https://en.wikipedia.org/wiki/CentOS";
        String et = "https://en.wikipedia.org/wiki/Lubuntu";
        
        String newTarget;
        //String rt = "https://en.wikipedia.org/";
        newTarget = newlinks.convertName(target) + ".ser";
        rt = newlinks.convertName(rt) + ".ser";
        lt = newlinks.convertName(lt) + ".ser";
        xt = newlinks.convertName(xt) + ".ser";
        yt = newlinks.convertName(yt) + ".ser";
        pt = newlinks.convertName(pt) + ".ser";
        ot = newlinks.convertName(ot) + ".ser";
        et = newlinks.convertName(et) + ".ser";
        
        
        System.out.println(graph.getVertex(newTarget).getLabel());
        
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        Dijk tcp = new Dijk(graph.getVertex(rt),graph.getVertex(newTarget));
        
        path = tcp.getAllVisited();
        

     return path;
    }

//}
//}
    public void printGraph() {
        System.out.println(graph.vertexKeys());
    }

}
