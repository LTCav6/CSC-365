/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * Computer.java
 ****************************************** */
package loader;

import java.util.ArrayList;
import loader.FrequencyTable;

/********************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * Computer.java
 ****************************************** */
public class Computer {
    ArrayList<Double> tblA; //create arraylist of doubles
    ArrayList<Double> tblB; //create arraylist of doubles
    
/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * This method returns the percentage of similarity between two tables
 ****************************************** */
    public int cosineSimilarity(FrequencyTable tableA, FrequencyTable tableB) {
             
        int similarity; //declare the similarity to be returned 
        ArrayList<Double> tmpList; //create temp arraylist of strings 
        tblA = tableA.getValues(); //uses hash sequence getValues 
        tblB = tableB.getValues(); //uses hash sequence getValues
        double dotProduct = 0.0; // set dotproduct to 0 
        double normA = 0.0;      // set normA to 0 
        double normB = 0.0;      // set normB 
        
        //if the size of table A is smaller than table B 
        if (tblA.size() < tblB.size()) {
            //call extend list for tblA 
            tblA = ExtendList(tblA, tblB.size() - tblA.size());
        }//if the size of table B is smaller than table A 
        else if (tblB.size() < tblA.size()) {
            //call extend list for tblB 
            tblB = ExtendList(tblB, tblA.size() - tblB.size());
        }//end if 

        //cosine vector for computing size 
        //https://stackoverflow.com/questions/520241/how-do-i-calculate-the-cosine-similarity-of-two-vectors
        //loop through the tables based on A's length 
        for (int i = 0; i < tblA.size(); i++) {
            //dotProduct += tablA * tablB 
            dotProduct += tblA.get(i) * tblB.get(i);
            //normA += value at a squared 
            normA += Math.pow(tblA.get(i), 2);
            //normB += value at a squared 
            normB += Math.pow(tblB.get(i), 2);
        }//end if 
        
        //similarity = the dotProduct over the sqrt of A multiplied by B 
        similarity = (int) (100 * (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))));
        //int i = d.intValue();
        
        return similarity;
    }
/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * This method will take a shorter table and return one of equal size
 ****************************************** */
    private ArrayList<Double> ExtendList(ArrayList<Double> smlList, int growSize) {

        //create an array list called small list to return the newly sized list 
        ArrayList<Double> smallList = new ArrayList<Double>(smlList);
        
        //loop through the difference between difference in list size 
        for (int i = 0; i < growSize; i++) {
            //add a double value of 0 for each empty position 
            smallList.add(0.0);
        }//end for 
        return smallList;
    }//end ExtendList 
}//end Computer 
