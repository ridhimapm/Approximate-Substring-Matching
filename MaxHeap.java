/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* GEEKSFORGEEKS MAXHEAP CLASS */

package Algorithms;

/**
 *
 * @author ahmadrasul
 */

public class MaxHeap {
    
    // heap is array of distance objects
    private Distance[] Heap;
    private int size;
    private final int maxSize;
  
    public MaxHeap(int maxSize) {
        this.maxSize = maxSize;
        this.size = 0;
        Heap = new Distance[this.maxSize+1];
        // initialize heap with empty distance object
        Distance newDist =new Distance(Integer.MAX_VALUE,null);
        Heap[0]=newDist;
    }

    private int parent(int pos) {
        return pos / 2;
    }
 
    private int leftChild(int pos) {
        return (2 * pos);
    }
 
    private int rightChild(int pos) {
        return (2 * pos) + 1;
    }
 
    private boolean isLeaf(int pos) {
        return pos >=  (size / 2)  &&  pos <= size;
    }
 
    private void swap(int fpos,int spos) {
        Distance temp;
        temp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = temp;
    }
 
    private void maxHeapify(int pos) {
        
        if (!isLeaf(pos)) { 
            if ( Heap[pos].getDistance() < Heap[leftChild(pos)].getDistance()  || Heap[pos].getDistance() < Heap[rightChild(pos)].getDistance()) {
                if (Heap[leftChild(pos)].getDistance() > Heap[rightChild(pos)].getDistance()) {
                    swap(pos, leftChild(pos));
                    maxHeapify(leftChild(pos));
                } else {
                    swap(pos, rightChild(pos));
                    maxHeapify(rightChild(pos));
                }
            }
        }
    }
 
    public void insert(int element, String str) {
        Distance newDist =new Distance(element,str);
        Heap[++size] = newDist;
        
        // maintain max heap
        int current = size;
        while(Heap[current].getDistance() > Heap[parent(current)].getDistance()) {
            swap(current,parent(current));
            current = parent(current);
        }	
    }
    
    public Distance remove() {
        Distance top = Heap[0];
        Heap[0] = Heap[size--];
        if(size > 1)
            maxHeapify(0);
        return top;
    }
 
    public int topDistance(){
        Distance top = Heap[0];
        return top.getDistance();
    }
    
    public int getSize(){
        return this.size;
    }

    public void maxHeap() {
        for (int pos = (size / 2); pos >= 1; pos--) {
            maxHeapify(pos);
        }
    }   
    
    public void print() {
        for (int i = 1; i <= size; i++ ) {
            System.out.println("String: "+ Heap[i].getString() + ", Edit Distance:" + Heap[i].getDistance());      
        }
    }
}
