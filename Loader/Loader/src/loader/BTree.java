/** *****************************************
 * Luke Cavanaugh
 * CSC365
 * Professor Doug Lea
 * BTree.java
 * This program built out of the shell of 
 * //https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BTree.java.html
 * from Princeton University
 ****************************************** */

package loader;


import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class BTree {

    private static final int M = 8;
    private static final int BUFFERSIZE = 2100;
    private ArrayList returnList = new ArrayList<String>();

    private static Node root;       // root of the B-tree
    private static int height;      // height of the B-tree
    static private int numPairs;           // number of key-keyue pairs in the B-tree
    private static int block;

    // helper B-tree node data type
    private static final class Node {

        private int m;                        // number of children
        private Entry[] children = new Entry[M];   // the array of children

        // create a node with k children
        private Node(int k) {
            m = k;
        }
    }

    private static class Entry {

        private String key;
        private Node next;     // helper field to iterate over array entries

        public Entry(String key, Node next) {

            this.key = key;
            this.next = next;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTree(int block) {
        this.block = block;
        root = new Node(0);
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false}
     * otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of keys
     *
     * @return the number of keys
     */
    public int size() {
        return numPairs;
    }

    public int height() {
        return height;
    }

    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }
        return search(root, key, height);
    }

    private String search(Node x, String key, int ht) {
        Entry[] children = x.children;

        // external node
        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (eq(key, children[j].key)) {
                    return children[j].key;
                }
            }
        } // internal node
        else {
            for (int j = 0; j < x.m; j++) {
                if (j + 1 == x.m || less(key, children[j + 1].key)) {
                    return search(children[j].next, key, ht - 1);
                }
            }
        }
        return null;
    }

    public void put(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //System.out.println("Adding " + key);
        Node u = insert(root, key, height);
        numPairs++;
        if (u == null) {
            return;
        }

        // need to split root
        Node t = new Node(2);
        t.children[0] = new Entry(root.children[0].key, root);
        t.children[1] = new Entry(u.children[0].key, u);
        root = t;
        height++;
    }

    private Node insert(Node h, String key, int ht) {

        int j;
        Entry t = new Entry(key, null);

        // external node
        if (ht == 0) {
            for (j = 0; j < h.m; j++) {
                if (less(key, h.children[j].key)) {
                    break;
                }
            }
        } // internal node
        else {
            for (j = 0; j < h.m; j++) {
                if ((j + 1 == h.m) || less(key, h.children[j + 1].key)) {
                    Node u = insert(h.children[j++].next, key, ht - 1);
                    if (u == null) {
                        return null;
                    }
                    t.key = u.children[0].key;
                    t.next = u;
                    break;
                }
            }
        }

        for (int i = h.m; i > j; i--) {
            h.children[i] = h.children[i - 1];
        }
        h.children[j] = t;
        h.m++;
        if (h.m < M) {
            return null;
        } else {
            return split(h);
        }
    }

    // split node in half
    private Node split(Node h) {
        Node t = new Node(M / 2);
        h.m = M / 2;
        for (int j = 0; j < M / 2; j++) {
            t.children[j] = h.children[M / 2 + j];
        }
        return t;
    }

   
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean eq(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    public void writeToDisk(ArrayList<String> batch) throws IOException {

        int j = 0;
        RandomAccessFile f = new RandomAccessFile("/home/luke/Documents/NetBeansProjects/Loader/src/loader/persist/cache.dat", "rw"); //creates RAF
        FileChannel fileChannel = f.getChannel(); //gets filechannel instance

        //increments blocks
        if (block > 0) {
            j = 1;
        }

        //position for writing 
        fileChannel.position(j + (BUFFERSIZE * block));
        
        //allocate 2100 
        ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);

        //for each string in the array 
        for (String b : batch) {
            //catch an strings too short 
            if (b.length() < 9) {
                //System.out.println("Caught element when writing");
            } else {
                //System.out.println("b is :" + b);
                //convert into file text
                String converted;
                converted = converter(b);
                //System.out.println("Length: " + converted.length());
                //System.out.println("converted site: " + converted);
                //add to buffer 
                buffer.put(converted.getBytes()); //stores node in buffer

            }

        }
        //increment block
        block++;
        
        //flip to write 
        buffer.flip();
        
        //write to file 
        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
        fileChannel.close();
        f.close();

    }

    public String toString() {
        return toString(root, height, "#");

    }

    private String toString(Node h, int ht, String indent) {

        StringBuilder s = new StringBuilder();
        Entry[] children = h.children;

        if (ht == 0) {
            for (int j = 0; j < h.m; j++) {
                s.append("#" + children[j].key);
            }
        } else {
            for (int j = 0; j < h.m; j++) {
                if (j > 0) {
                    s.append("#" + children[j].key);
                }
                s.append(toString(children[j].next, ht - 1, "#"));
            }
        }
        return s.toString();
    }

    public static void resetBlock() {
        block = 0;
    }

    public static String converter(String str) {

        String filler = "!";
        String newStr;

        int length = str.length();

        //System.out.println("String in converter" + str);

        length = 300 - length;

        for (int i = 0; i < length; i++) {

            str += filler;
        }
        //System.out.println("String in converter" + str);
        //System.out.println("String length" + str.length());
        return str;
    }

    public boolean readFromDisk() throws IOException, IllegalArgumentException {
        
        boolean success = true;

        try {
            ArrayList<String> arraylist = new ArrayList<String>();

            //System.out.println("Reading ..");

            int j = 0;
            //set file path
            RandomAccessFile read = new RandomAccessFile("/home/luke/Documents/NetBeansProjects/Loader/src/loader/persist/cache.dat", "rw");
            
            //loads file path into channel 
            FileChannel channel = read.getChannel();
            
            //increments blocks 
            if (block > 0) {
                j = 1;
            }

            //System.out.println("Block is " + block);
            //create mew buffer with size 2100
            ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);

            //System.out.println(buffer.toString());
            
            //set position of where to begin writing
            channel.position(j + (BUFFERSIZE * block));

            //returns the number of bytes read 
            int bytesRead = channel.read(buffer);

            //set limit to number of bytes read
            buffer.limit(bytesRead);

            //create array of bytes 
            byte[] bytes = new byte[BUFFERSIZE];
            
            //rewind buffer
            buffer.rewind();
            
            //returns the bytes at position 
            buffer.get(bytes);
            
            String desc = new String(bytes);
            String[] arr = desc.split("!");

            //for each string in the array load them into master list 
            for (String s : arr) {
                returnList.add(s);
            }

            channel.close();
            read.close();

            block++;
        } catch (IllegalArgumentException i) {
            //System.out.println("done reading");
            success = false;
        }

        return success;
    }

    public ArrayList<String> getAllSites() {
        return returnList;
    }

    private static BTree printFileContents() throws IOException {

        BTree b = new BTree(0);

        FileReader fr = new FileReader("/home/luke/Documents/NetBeansProjects/Loader/src/loader/serisites/");
        BufferedReader br = new BufferedReader(fr);
        for (int i = 0; i < numPairs; i++) {

            b.put(br.readLine());

        }

        fr.close();

        br.close();

        return b;
    }

}
