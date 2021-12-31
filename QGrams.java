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

public class QGrams {
    
    private Distance[] qGrams; // q-gram is array of Distance objects
    private int size;
    private int maxSize;
    
    public QGrams(int maxSize) {
        this.size = 0;
        this.maxSize = maxSize;
        this.qGrams = new Distance[maxSize];
    }
    
    public Distance[] getQGram() {
        return qGrams;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setQGram(Distance[] qGram) {
        this.qGrams = qGram;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public void insert(String str, int pos){
        qGrams[this.size] = new Distance(pos, str);
        this.size=this.size+1;
    }
    
    public void constructQGram(String input){
        for(int i = 0; i < input.length()-2; i++){
            this.insert(input.substring(i, i+3), i+1);
        }
    }
}
