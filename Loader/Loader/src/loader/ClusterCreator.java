/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * ClusterCreator.java
 ****************************************** */
package loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import org.jsoup.nodes.Document;

/**
 *
 * @author Luke
 */
public class ClusterCreator {

    int numClusters = 5; //five clusters by default 
    private File file;

    public ArrayList<Cluster> clusters = new ArrayList<Cluster>();

    public ClusterCreator(int numClusters, File file) {
        this.numClusters = numClusters; //users specified clusters 
        this.file = file; //pathway to folder of default root URL's 

    }

    public void loadRoots() throws FileNotFoundException, IOException, Exception {
        String currentRoot = "";
        Document tmpDoc = null;
        InfoParser rootParser = new InfoParser();
        FrequencyTable tmpTable = new FrequencyTable("name");
        int counter = numClusters;

        // Read the file
        BufferedReader in = new BufferedReader(new FileReader(file));
        //while there are URL's to process
        while (in.ready()) {
            if (counter == 0) {
                break;
            }
            currentRoot = new String(in.readLine());
            tmpDoc = rootParser.getHTML(currentRoot);
            tmpTable = rootParser.scrape(tmpDoc);
            Cluster cluster = new Cluster(tmpTable); //set new clusters centroid to be the root
            clusters.add(cluster);
            counter++;

        }
        //System.out.println("Here are the clusters: " + clusters.toString());
    }

    public void fillClusters(ArrayList<FrequencyTable> sites) {
        //declare the highest percentage score
        int highestScore = 0;
        //declare the index for the highest score
        int highestScoreIndex = 0;
        //temp holder 
        int tmpScore = 0;
        int clustIndex = 0;
        Cluster tmpCluster;

        for (FrequencyTable f : sites) {

            Computer computer = new Computer();
            for (clustIndex = 0; clustIndex < clusters.size(); clustIndex++) {
                //System.out.println("clusters.size is " + clusters.size());
                tmpScore = computer.cosineSimilarity(f, clusters.get(clustIndex).getCentroid());
                //System.out.println("TEMP SCORE IS " + tmpScore);
                //System.out.println("i is " + clustIndex);

                if (tmpScore >= highestScore) {
                    highestScore = tmpScore;
                    highestScoreIndex = clustIndex;
                    //System.out.println("Highest score index is " + highestScoreIndex);
                }

            }
            //System.out.println("Attempting to add site" + f.getName() + "at position " + highestScoreIndex);
            tmpCluster = clusters.get(highestScoreIndex); //copy the cluster element at position highestScoreIndex 
            tmpCluster.addSite(f);                      //add the site to it 
            clusters.set(highestScoreIndex, tmpCluster); //replace previous clusterwith new cluster

        }
    }

    public void printClustersSize() {
        for (Cluster c : clusters) {
            c.siteCount();
        }
    }

    public void runClusterAnalysis() {
        //System.out.println("Now running analysis");
        FrequencyTable tmp = new FrequencyTable("tmp");
        Computer computer = new Computer();

        for (Cluster c : clusters) {
            ArrayList<FrequencyTable> tmpArray = new ArrayList<FrequencyTable>();
            tmpArray = c.sites;
            for (FrequencyTable f : c.sites) {

                for (int i = 0; i < tmpArray.size(); i++) {
                    float runningTotal = 0;

                    //System.out.println("c.sites.size() = " + c.sites.size());
                    runningTotal += computer.cosineSimilarity(f, tmpArray.get(i));
                    f.setAvgSim((runningTotal / tmpArray.size()));

                }
            }
            float highestSim = 0;
            for (FrequencyTable f : c.sites) {
                if (f.getAvgSim() > highestSim) {
                    highestSim = f.getAvgSim();
                    c.setCentroid(f);
                }
            }

        }
    }

    public String findClosestCentroid(FrequencyTable usersTable) {
        Computer computer = new Computer();
        float highestSim = 0;
        float tmpSim;
        String centroidName = "";

        for (Cluster c : clusters) {
            tmpSim = computer.cosineSimilarity(usersTable, c.getCentroid());
            if (tmpSim > highestSim) {
                highestSim = tmpSim;
                centroidName = c.getCentroid().getName();
            }

        }

        return centroidName;
    }

    public FrequencyTable Deserialize(String site) {
        String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/";
        String cacheExtension = ".ser"; //for adding file extension 
        FrequencyTable tmp = new FrequencyTable("Null");
        FileInputStream fin = null;
        ObjectInputStream ois = null;

        //site = site.replace(cacheExtension, "");
        try {
            //System.out.println("Deserialzing " + cacheFolder + site);
            fin = new FileInputStream(cacheFolder + site);
            ois = new ObjectInputStream(fin);
            tmp = (FrequencyTable) ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            //System.out.println("Exception in Deserialize thrown");
        }
        return tmp;

    }

    //create array[numClusters] of arraylists
    //compare each sites cosine similarity against all thousand
    //place site (key) and value (average similarity) into hash table 
    private class Cluster {

        private FrequencyTable centroid;
        private ArrayList<FrequencyTable> sites = new ArrayList<FrequencyTable>();     // helper field to iterate over array entries

        public Cluster(FrequencyTable centroid) {
            this.centroid = centroid;
            this.sites = sites;
            sites.add(centroid); //ensures that the centroid is contained in the arraylist 
        }

        public void setCentroid(FrequencyTable newCentroid) {
            centroid = newCentroid;
        }

        public FrequencyTable getCentroid() {
            return centroid;
        }

        public void addSite(FrequencyTable newTable) {
            sites.add(newTable);
        }

        public void siteCount() {
            //System.out.println("Site count of " + centroid.getName() + " is: " + sites.size());
        }
    }

}
