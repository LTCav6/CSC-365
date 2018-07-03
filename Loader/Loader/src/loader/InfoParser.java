/** ******************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * public InfoParser
 ******************************************* */
package loader;

import LCGraphs.Graph;
import LCGraphs.Vertex;
import java.util.regex.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * ******************************************
 * Luke Cavanaugh CSC365 public InfoParser
 * ******************************************
 */
public class InfoParser implements java.io.Serializable {

    public File file; //declare file 

    public static FileWriter writer;

    public static int siteCounter = 0;
    public static Set<String> set = new LinkedHashSet<String>();

    public InfoParser(File file) {
        this.file = file; //set file 
    }

    public InfoParser() {

    }

    public File getFile() {
        return file;
    }

    protected ArrayList<FrequencyTable> readFile() throws Exception, IOException, SocketTimeoutException, NullPointerException {
        String currentURL;
        Document doc1;
        String line = null;
        // Read the file
        BufferedReader in = new BufferedReader(new FileReader(getFile()));
        //create a new array list 
        List<String> list = new ArrayList<String>();
        ArrayList<FrequencyTable> master = new ArrayList<FrequencyTable>();

        //String line = null; 
        //while there are URL's to process
        Scanner input = new Scanner(new FileReader(file));

        //while there are URL's to process
//        while ((line = in.readLine()) != null) {
        while (input.hasNext()) {
            //urlHolder = input.next();

            // while ((line = in.readLine()) != null) {
            try {
                //currentURL = new String(in.readLine());
                currentURL = input.next();
                //System.out.println("current URL is " + currentURL);
                //System.out.println("Returning doc from getHTML . . .");
                doc1 = getHTML(currentURL);
                FrequencyTable tmpTable = new FrequencyTable("temp");
                if (doc1 == null) {
                    //System.out.println("<<<<NULL SAVE>>>>");
                } else {

                    tmpTable = scrape(doc1);;
                    Elements linksOnPage = doc1.select("a");

                    for (Element page : linksOnPage) {
                        String link = page.attr("abs:href");
                        tmpTable.addURL(link);
                    }

                    //System.out.println("Doc is not null");
                    tmpTable.setName(currentURL);
                    master.add(tmpTable);
                }
            } catch (NullPointerException n) {
                //System.out.println("null pointer caught");
            }
        }//end while 

        //close the reader         
        in.close();
        return master;

    }

    protected Document getHTML(String url) throws Exception, IOException, SocketTimeoutException {
        Document doc = null;
        String mailto = "mailto";

        if (url == null || url.length() == 0 || url.toLowerCase().contains(mailto.toLowerCase())) {
            //System.out.println("caught");
        } else {
            //System.out.println("Attempting to connect to site getHTML. . . " + url);
            try {
                Connection.Response response = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .execute();

                int statusCode = response.statusCode();
                if (statusCode != 200) {
                    //System.out.println("Caught Bad URL");
                } else {
                    //System.out.println("URL successfully oonnected: " + url);
                    doc = Jsoup.connect(url).get();
                }

            } catch (IOException | NullPointerException | IllegalArgumentException s) {

                //System.out.println("socket caught");
            }
        }
        return doc;
    }

    /**
     * ******************************************
     * Luke Cavanaugh CSC365 Takes an HTML document and returns a frequency
     * table ******************************************
     */
    protected FrequencyTable scrape(Document doc) {

        FrequencyTable tmpTable = new FrequencyTable("fr1");//creates a local Frequency Table 
        String title = ""; //holds title 
        String description = "";//holds description
        String titleBooster = "";
        String siteText = "";

        //try to grab the website description if it has one 
        try {
            //System.out.println("Scraping HTML from doc . . .");
            //load description 
            description = doc.select("meta[name=description]").first().attr("content");
            //loop to add description 100 times to challenge weight 
            for (int x = 0; x < 100; x++) {
                description = description + " ";
            }//end for 
            //catch a null pointer 
        } catch (NullPointerException e) {
            //System.out.println("Description was null! Replacing description . .");
            description = "generic";
        }

        try {
            //combine all of the data points into one String 
            siteText = description + doc.head().text() + doc.body().text() + doc.title() + titleBooster;
        } catch (NullPointerException z) {
            siteText = description;
        }
        //load into an array of Strings 
        String[] cleanedText = cleanText(siteText);

        //This will loop through the text, placing each word in the frequency table
        for (int i = 0; i < cleanedText.length; i++) {
            tmpTable.put(cleanedText[i], 1);
        }//end for 

        //return the temporary table 
        return tmpTable;
    }

    /**
     * ******************************************
     * Luke Cavanaugh CSC365 Takes a string and returns a cleaned string
     * ******************************************
     */
    //remove commas and periods
    private String[] cleanText(String siteText) {

        //runs a regular expression that cleans the data 
        String[] words = siteText.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

        //return array of words 
        return words;
    }

    /**
     * ******************************************
     * Luke Cavanaugh CSC365 Method to return an arraylist of sites
     * ******************************************
     */
    protected ArrayList<String> getSites() throws Exception {
        //System.out.println("Reading file " + file.toString());
        ArrayList<String> siteList = new ArrayList<String>(); //create a new array list and add each site as one node
        String urlHolder; //gets URL 
        String line = "https://www.google.com";

        // Read the file
        //BufferedReader in = new BufferedReader(new FileReader(file));
        Scanner in = new Scanner(new FileReader(file));
        while (in.hasNext()) {
            urlHolder = in.next();
            siteList.add(urlHolder);
            //while there are URL's to process
//        while ((line = in.readLine()) != null) {

//            urlHolder = in.readLine();
            //System.out.println(urlHolder);
            //urlHolder += "\n";
        }
        return siteList;
    }//end getSites

    public void getPageLinks(String site, int depth) throws IOException, IllegalArgumentException {
        int MAX_DEPTH = 6;

        if ((site.length() > 5) && (site.length() < 200)) {

            if ((depth < MAX_DEPTH) && (siteCounter < 200) && (!set.contains(site))) {
                set.add(site);
                System.out.println(">> Depth: " + depth + " [" + site + "]");
                try {
                    writer.write(site);
                    writer.write(System.lineSeparator());
                    siteCounter++;

                    Document document = Jsoup.connect(site).get();
                    Elements linksOnPage = document.select("a[href]");

                    depth++;
                    for (Element page : linksOnPage) {
                        getPageLinks(page.attr("abs:href"), depth);
                    }
                } catch (IOException | IllegalArgumentException e) {
                    System.err.println("For '" + site + "': " + e.getMessage());

                }
            }
        }
    }

    //this method takes the root sites and gets all sublinks. It then writes them to the sites file. 
    protected boolean writeSublinks() throws Exception {

        ArrayList<String> rootList = new ArrayList<String>(); //create arraylist to hold roots or sites
        ArrayList<String> siteList = new ArrayList<String>(); //create arraylist to hold roots or sites
        FrequencyTable tmpTable = new FrequencyTable("temp");
        Document tempDoc = null;
        writer = new FileWriter("/home/luke/Documents/NetBeansProjects/Loader/src/loader/sites.txt");

        //load roots into arraylist list
        rootList = getSites();
        //System.out.println(Arrays.toString(rootList.toArray()));

        //System.out.println("loading root list...");
        //for each string in the list 
        for (String line : rootList) {
            if (line != null) {
                siteCounter = 0;
                getPageLinks(line, 0);
            }

        }
        writer.close();
        return true;
    }

//    //this method takes the root sites and gets all sublinks. It then writes them to the sites file. 
//    protected boolean writeSublinks() throws Exception {
//
//        ArrayList<String> rootList = new ArrayList<String>(); //create arraylist to hold roots or sites
//        ArrayList<String> siteList = new ArrayList<String>(); //create arraylist to hold roots or sites
//        FrequencyTable tmpTable = new FrequencyTable("temp");
//        Document tempDoc = null;
//
//        //load roots into arraylist list
//        rootList = getSites();
//        //System.out.println(Arrays.toString(rootList.toArray()));
//
//        //System.out.println("loading root list...");
//
//        FileWriter writer = new FileWriter("/home/luke/Documents/NetBeansProjects/Loader/src/loader/sites.txt");
//        //for each string in the list 
//        for (String line : rootList) {
//
//            if (line !=null){
//            //System.out.println("Attempting to connect to site line. . . " + line);
//            try {
//
//                tempDoc = Jsoup.connect(line).ignoreHttpErrors(true).get();
//                //System.out.println("URL successfully connected: " + line);
//
//            } catch (MalformedURLException m) {
//                //System.out.println("sub socket caught");
//            }
//            
////            catch (IOException e) {
////                System.out.println("sub socket caught");
////            }
//
//            Elements elements = tempDoc.select("a");
//
//            for (Element element : elements) {
//                String site = element.attr("abs:href");
//
//                //System.out.println("Writing site to file: " + site);
//                writer.write(site);
//                writer.write(System.lineSeparator());
//
//            }
//        }
//
//        }
//        writer.close();
//
//        return true;
//    }
    public void serializeTable(FrequencyTable URL) throws FileNotFoundException, IOException {
        String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/";
        String cacheExtension = ".ser"; //for adding file extension 
        String newName;

        newName = convertName(URL.getName());
        //System.out.println("SERIALIZING OLD NAME: " + URL.getName());
        //System.out.println("SERIALIZING NEW NAME: " + newName);
        //System.out.println("Full pathname is : \t" + cacheFolder + newName + cacheExtension);
        ObjectOutputStream oos;
        try (FileOutputStream fout = new FileOutputStream(cacheFolder + newName + cacheExtension)) {
            oos = new ObjectOutputStream(fout);
            oos.writeObject(URL);
            oos.close();
        } catch (FileNotFoundException f) {
            System.out.println("Website failed to serialize" + newName);
        }

    }

    public void serializeGraph(Graph graph) throws FileNotFoundException, IOException {
        String cacheFolder = "/home/luke/Documents/NetBeansProjects/Loader/src/loader/graph/";
        String cacheExtension = ".ser"; //for adding file extension 
        String newName;

//        FileOutputStream fout = new FileOutputStream(cacheFolder + newName + cacheExtension);
//                oos = new ObjectOutputStream(fout);
//                oos.writeObject(graph);
        newName = "graph";
        //System.out.println("SERIALIZING OLD NAME: " + URL.getName());
        //System.out.println("SERIALIZING NEW NAME: " + newName);
        //System.out.println("Full pathname is : \t" + cacheFolder + newName + cacheExtension);
        ObjectOutputStream oos1;
        System.out.println(cacheFolder + newName + cacheExtension);
        try (FileOutputStream fout = new FileOutputStream(cacheFolder + newName + cacheExtension)) {
            oos1 = new ObjectOutputStream(fout);
            System.out.println("Writing vertices");
            oos1.writeObject(graph);
            System.out.println("vertices written");
            oos1.close();
        } catch (FileNotFoundException f) {
            System.out.println("graph failed to serialize" + newName);
        } catch (StackOverflowError s) {
            System.out.println("stackoverflow ");
        }

    }

    public String convertName(String URL) {
        URL = URL.replace("https://", "^H_S^");
        URL = URL.replace("http://", "^H_P^");
        URL = URL.replace("/", "^Slsh^");
        URL = URL.replace("-", "^Dsh^");
        URL = URL.replace(":", "^Cln^");
        URL = URL.replace("#", "^Hsh^");
        URL = URL.replace("$", "^Dlr^");
        URL = URL.replace("?", "^Qst^");
        URL = URL.replace("<", "^Lef^");
        URL = URL.replace(">", "^Rig^");
        URL = URL.replace(".html", "^htm^");
        URL = URL.replace("\n", "");
        return URL;
    }

    protected String reverseName(String URL) {

        URL = URL.replace("^H_S^", "https://");
        URL = URL.replace("^H_P^", "http://");
        URL = URL.replace("^Slsh^", "/");
        URL = URL.replace("^Dsh^", "-");
        URL = URL.replace("^Cln^", ":");
        URL = URL.replace("^Hsh^", "#");
        URL = URL.replace("^Dlr^", "$");
        URL = URL.replace("^Qst^", "?");
        URL = URL.replace("^Lef^", "<");
        URL = URL.replace("^Rig^", ">");
        URL = URL.replace("^htm^", ".html");
        return URL;

    }

}//end InfoParser 
