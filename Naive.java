/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithms.TopKNaive;

import Algorithms.Distance;
import Algorithms.MaxHeap;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 *
 * @author ahmadrasul
 */

public class Naive {

    public static int counter = 0;
    
    // function calculate smallest edit distance
    public int editDistance(String str1, String str2) {
        
        counter++;
    
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        
        int [] distance = new int[str2.length()+1];
        
        // loops compare distance between two elements
        for (int i = 1; i <= str1.length(); i++) {
            distance[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= str2.length(); j++) {
                int cj = Math.min(1 + Math.min(distance[j], distance[j - 1]),
                        str1.charAt(i - 1) == str2.charAt(j - 1) ? nw : nw + 1);
                nw = distance[j];
                distance[j] = cj;
            }
        }
        
        // return the lowest distance
        Arrays.sort(distance);
        return distance[0];
    }
    
    public static void main(String[] args) {
        
        // database information 
        String url = "jdbc:mysql://localhost:3306/dblp";
        String user = "root";
        String password = "Seminole-19";
        
        // query variables
        String query = "database";
        String title = "SELECT title FROM paper";
        
        // setting up algorithm
        Naive topK = new Naive();
        MaxHeap heap;
        
        // calculating k value
        int k = 10, kChanged = 0;
        if (k%2 != 0 && k != 1) {
            k = k+1;
            kChanged += 1;
        } else if (k == 2) {
            k = k+2;
            kChanged += 2;
        }
        
        // creating heap with k value
        heap = new MaxHeap(k);
        
        // establish connection
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(title);
                ResultSet rs = stmt.executeQuery()) {
            
            // loop to go through result set
            while (rs.next()) {
                
                // calculate edit distance between query and string
                int value = topK.editDistance(query, rs.getString(1));
                
                // if size less than k, insert string into heap
                if (heap.getSize() < k) {
                    heap.insert(value, rs.getString(1));
                }
                
                else if (heap.getSize() == k) {
                    // otherwise, compare distance of query with root of heap
                    if (value < heap.topDistance()) {
                        // if satisfied, delete string at root of heap and insert new stirng
                        heap.maxHeap();
                        heap.remove();
                        heap.insert(value, rs.getString(1));
                    }
                }
                
                // if not satisfied, move to next string and repeat
            }
            
            // reset heap
            while(kChanged > 0) {
                heap.maxHeap();
                heap.remove();
                kChanged--;
            }
            
            // print edit distance and num of calculations
            heap.print();
            System.out.println("Number of Calculations: " + counter);
        }
        
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
}
