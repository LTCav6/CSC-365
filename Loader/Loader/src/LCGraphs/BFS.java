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

import java.util.LinkedList;
import java.util.Queue;

public class BFS {

    public int counter = 0;

    public BFS(Vertex root, Vertex root2, Vertex root3, Vertex root4, Vertex root5, Vertex root6, Vertex root7) {

        Queue<Vertex> queue = new LinkedList<>();

        root.setVisited();

        root2.setVisited();

        root3.setVisited();

        root4.setVisited();

        root5.setVisited();

        root6.setVisited();

        root7.setVisited();

        queue.add(root);
        queue.add(root2);
        queue.add(root3);
        queue.add(root4);
        queue.add(root5);
        queue.add(root6);
        queue.add(root7);

        counter = 7;

        while (!queue.isEmpty()) {

            Vertex actualVertex = queue.remove();

            //System.out.print("Current Root" + actualVertex.getLabel());
            for (Vertex v : actualVertex.getNeighborList(actualVertex)) {
                // System.out.println("It's working" + v.getLabel());
                if (!v.checkVisited()) {
                    v.setVisited();
                    queue.add(v);
                    counter++;
                    //System.out.println(v + " " + counter);

                }
            }
        }
    }

    public int getAllVisited() {
        return counter;
    }
}
