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
public class EdgeSaver{

    String one;
    String two;
    int weight;

    public EdgeSaver(String one, String two, int weight) {
        this.one = one;
        this.two = two;
        this.weight = weight;
    }

    public String getOne(){
        return this.one;
    }
    
    public String getTwo(){
        return this.two;
    }
    
    public int getWeight(){
        return this.weight;
    }
    
    
}
