/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCGraphs;

/**
 *
 * @author luke
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Dijk {

    public int counter = 0;
    ArrayList<Vertex> path = new ArrayList<Vertex>();
    static ArrayList<Vertex> pathway = new ArrayList<Vertex>();

    public Dijk(LCGraphs.Vertex root, LCGraphs.Vertex root2) {

        System.out.println("Looking for " + root2.getLabel());
        System.out.println("Looking for " + root.getLabel());

        Queue<Vertex> queue = new LinkedList<>();

        root.setVisited();

        //root2.setVisited();
        queue.add(root);
        //queue.add(root2);

        counter = 0;
        pathway.add(root);

        while (!queue.isEmpty()) {

            Vertex actualVertex = queue.remove();

            //System.out.print("Current Root" + actualVertex.getLabel());
            for (Vertex v : actualVertex.getNeighborList(actualVertex)) {
                System.out.println(v.checkVisited());
                if (v.checkVisited() == false) {
                    pathway.add(v);
                    System.out.println("site added to path");
                }
                
                
                if (v.getLabel().equals(root2.getLabel())) {
                    System.out.println("MATCH");
                    //pathway = (ArrayList<Vertex>) path.clone();
                    return;
                }
                System.out.println("No match");
                v.setVisited();
                queue.add(v);
                counter++;
                //System.out.println(v + " " + counter);
            }

        }

    }

    public ArrayList<Vertex> getAllVisited() {
        return pathway;
    }
}
