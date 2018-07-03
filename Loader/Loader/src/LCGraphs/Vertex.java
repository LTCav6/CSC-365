/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCGraphs;

//import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class models a vertex in a graph. For ease of the reader, a label for
 * this vertex is required. Note that the Graph object only accepts one Vertex
 * per label, so uniqueness of labels is important. This vertex's neighborhood
 * is described by the Edges incident to it.
 *
 * @author Michael Levet (modified by Luke Cavanaugh)
 * @date June 09, 2015
 */

public class Vertex {

    //static final long serialVersionUID = 4L;
    private ArrayList<Edge> neighborhood;
    private String label;
    private ArrayList<Vertex> neighborList;
    private boolean visited;
    public static int visitedCount = 0;

    public Vertex() {

    }

    /**
     *
     * @param label The unique label associated with this Vertex
     */
    public Vertex(String label) {
        this.label = label;
        this.neighborhood = new ArrayList<Edge>();
        this.neighborList = new ArrayList<Vertex>();
        this.visited = false;
    }

    /**
     * This method adds an Edge to the incidence neighborhood of this graph iff
     * the edge is not already present.
     *
     * @param edge The edge to add
     */
    public void addNeighbor(Edge edge) {
        if (this.neighborhood.contains(edge)) {
            return;
        }

        this.neighborhood.add(edge);
    }

    /**
     *
     * @param other The edge for which to search
     * @return true iff other is contained in this.neighborhood
     */
    public boolean containsNeighbor(Edge other) {
        return this.neighborhood.contains(other);
    }

    /**
     *
     * @param index The index of the Edge to retrieve
     * @return Edge The Edge at the specified index in this.neighborhood
     */
    
    public Edge getNeighbor(int index) {
        return this.neighborhood.get(index);
    }

    /**
     *
     * @param index The index of the edge to remove from this.neighborhood
     * @return Edge The removed Edge
     */
    Edge removeNeighbor(int index) {
        return this.neighborhood.remove(index);
    }

    /**
     *
     * @param e The Edge to remove from this.neighborhood
     */
    public void removeNeighbor(Edge e) {
        this.neighborhood.remove(e);
    }

    /**
     *
     * @return int The number of neighbors of this Vertex
     */
    public int getNeighborCount() {
        return this.neighborhood.size();
    }

    /**
     *
     * @return String The label of this Vertex
     */
    
    public String getLabel() {
        return this.label;
    }

    /**
     *
     * @return String A String representation of this Vertex
     */
    public String toString() {
        //return "Vertex " + label;
        return label;
    }

    /**
     *
     * @return The hash code of this Vertex's label
     */
    
    public int hashCode() {
        return this.label.hashCode();
    }

    /**
     *
     * @param other The object to compare
     * @return true iff other instanceof Vertex and the two Vertex objects have
     * the same label
     */
    public boolean equals(Object other) {
        if (!(other instanceof Vertex)) {
            return false;
        }

        Vertex v = (Vertex) other;
        return this.label.equals(v.label);
    }

    public void setVisited() {
        this.visited = true;
//        visitedCount++;
//        if(visitedCount == 2)
//        System.out.println(this.getLabel() +" VISITED TWICE VISITED TWICE \n\n\n\n\n");
    }

    public boolean checkVisited() {
        return this.visited;
    }

    /**
     *
     * @return ArrayList<Edge> A copy of this.neighborhood. Modifying the
     * returned ArrayList will not affect the neighborhood of this Vertex
     */
    
    public ArrayList<Edge> getNeighbors() {
        return new ArrayList<Edge>(this.neighborhood);
    }

    
    public List<Vertex> getNeighborList(Vertex vertex) {
        for (Edge e : vertex.getNeighbors()) {

            neighborList.add(e.getNeighbor(vertex));
        }
        return neighborList;

    }

}
