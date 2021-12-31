/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithms;

/**
 *
 * @author ahmadrasul
 */

public class Distance {
    
    private int dist;
    private String str;
    
    public Distance(int dist, String str) {
        this.dist = dist;
        this.str = str;
    }
    
    public int getDistance() {
        return dist;
    }
    
    public String getString() {
        return str;
    }
    
    public void setDistance(int dist) {
        this.dist = dist;
    }
    
    public void setString(String str) {
        this.str = str;
    }
}
