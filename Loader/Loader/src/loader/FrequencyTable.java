/** ******************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * FrequencyTable.java
 ****************************************** */
package loader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.select.Elements;

/**
 * ******************************************
 * Luke Cavanaugh CSC365 Professor Doug Lea FrequencyTable.java
 * ******************************************
 */
public class FrequencyTable implements Serializable {

    private static final long serialVersionUID = 6529685098267757690L;
    public FreqNode[] frequencyTable;
    private final double sizePerc;
    private int tblSize;
    private int numNodes;
    private String name;
    private float avgSim;
    private Set<String> pageURLs;

    public FrequencyTable(String name) {
        this.name = name;
        tblSize = 800;
        numNodes = 0;
        sizePerc = 0.75;
        frequencyTable = new FreqNode[tblSize];//create the array
        this.avgSim = 0;
        this.pageURLs = new HashSet<String>();
    }

    /**
     * ******************************************
     * Luke Cavanaugh CSC365 Professor Doug Lea getName This method returns name
     * of the hash table
     *
     * @return *****************************************
     */
    public String getName() {
        return name;
    }

    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * setName 
     * This method sets name of
     * the hash table
     *
     * @param newName *****************************************
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * getName 
     * This method returns name
     * of the hash table
     *
     * @return *****************************************
     */
    public float getAvgSim() {
        return avgSim;
    }

    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * setName This method sets name of
     * the hash table
     *
     * @param newName *****************************************
     */
    public void setAvgSim(float avg) {
        avgSim = avg;
    }
/**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * getName 
     * This method returns name
     * of the hash table
     *
     * @return *****************************************
     */
    public Set<String> getURLs() {
        return pageURLs;
    }

    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * setName This method sets name of
     * the hash table
     *
     * @param newName *****************************************
     */
    public void addURL(String url) {
        pageURLs.add(url);
    }
    
    
    
    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * getValues 
     * This method returns an
     * ArrayList of the values of the hash table
     *
     * @return *****************************************
     */
    public ArrayList<Double> getValues() {

        ArrayList<Double> values = new ArrayList<Double>(); //create new arraylist to return
        FreqNode tmpNode; //create temp node 

        //traverse entire table 
        for (FreqNode node : frequencyTable) {
            //set node 
            tmpNode = node;
            while (true) {
                if (tmpNode == null) {
                    break;
                } else {
                    //add value to node in table and cast to double 
                    values.add((node.getValue() * 1.0));
                    //set it to the next node 
                    tmpNode = tmpNode.getNext();
                } //end else 
            }//end if 
        }
        // Return the keys
        return values;
    }

    /**
     * *****************************************
     * Luke Cavanaugh 
     * CSC365 
     * This method adds a new node to
     * the table
     *
     * @param key
     * @param value ****************************************
     */
    public void put(String key, int value) {

        // Hold the tmpNode node
        FreqNode tmpNode;

        //calculate the index by hashcode mod table length 
        int index = Math.abs(key.hashCode()) % (frequencyTable.length);

        //if location is empty place new node 
        if (frequencyTable[index] == null) {
            frequencyTable[index] = new FreqNode(key, value);

            numNodes++; //incremement the nodes (per demo recommendation with Professor)  
        } else {
            //set temp node
            tmpNode = frequencyTable[index];

            //for each node 
            while (true) {
                //check for duplicate value and increment 
                if (key.equals(tmpNode.getKey())) {
                    //increment
                    tmpNode.setValue(value + tmpNode.getValue());
                    return; //success    
                }//end if  
                else if (tmpNode.getNext() == null) {
                    //set the new node at the next free
                    tmpNode.setNext(new FreqNode(key, value));
                    //increase nodes 
                    numNodes++;

                    //check if nodes are greater than table proportions
                    if (numNodes > (frequencyTable.length * sizePerc)) {
                        extendTable();  //increase size 
                    }
                    return; //success
                } else {
                    //continue traversal of table 
                    tmpNode = tmpNode.getNext();
                }//end else
            }//end while 
        }//end else 
    }//end put 

    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365 
     * This method returns a key in the index 
     *****************************************
     */
    public int get(String key) {

        // Hold the tmpNode node
        FreqNode tmpNode;

        //calculate the index by hashcode mod table length 
        int index = Math.abs(key.hashCode()) % (frequencyTable.length);

        //if mapped location is empty
        if (frequencyTable[index] == null) {
            return 0; //does not effect the cosine similarity 
        } else {
            //value is at the tmpNode 
            tmpNode = frequencyTable[index];
            //for each value in table 
            while (true) {
                //if tmpNode is null 
                if (tmpNode == null) {
                    return 0;
                } //else if the key is equal to this nodes key 
                else if (key.equals(tmpNode.getKey())) {
                    //return the value of this key 
                    return tmpNode.getValue();
                } else {
                    //grab the next element as a last resort 
                    tmpNode = tmpNode.getNext();
                }//end else 
            }//end else if 
        }//end else
    }//end get 

    /**
     * *****************************************
     * Luke Cavanaugh 
     * CSC365 
     * This method extends the table if
     * necessary *****************************************
     */
    private void extendTable() {

        FreqNode[] freqTable = frequencyTable;//create temp table
        numNodes = 0; //set nodes to 0 
        FreqNode tmpNode;  //temp variable
        frequencyTable = new FreqNode[frequencyTable.length * 2];//doubles the size 

        //for every element in the table 
        for (FreqNode node : freqTable) {
            //set the node 
            tmpNode = node;
            while (true) {
                //if the node currently has a value  
                if (tmpNode == null) {
                    break;
                }//else  
                else {
                    //place the key and value at the current position
                    this.put(tmpNode.getKey(), tmpNode.getValue());
                    tmpNode = tmpNode.getNext();
                }//end else
            }//end while 
        }//end for 
    }//end extendTable 



    /**
     * ******************************************
     * Luke Cavanaugh 
     * CSC365
     * Creates nodes in table
     * *****************************************
     */
    public class FreqNode implements Serializable {

        private static final long serialVersionUID = 6529685098267757691L;
        private String key; //holder for key 
        private int value;//holder for value 
        private FreqNode tmpNode;//temp holder for the pending node

        //constructor 
        public FreqNode(String key, int value) {
            this.key = key;//sets key 
            this.value = value;//sets value 
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public FreqNode getNext() {
            return this.tmpNode;
        }

        public void setNext(FreqNode tmpNode) {
            this.tmpNode = tmpNode;
        }
    }//endFreqNode
}//endFrequencyTable 
