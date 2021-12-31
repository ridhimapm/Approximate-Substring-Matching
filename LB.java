/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithms.TopKLB;

import Algorithms.Distance;
import Algorithms.MaxHeap;
import Algorithms.QGrams;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 *
 * @author ahmadrasul
 */

public class LB {

    public static String query;
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
    
    public QGrams [] compareQGrams(QGrams qgQuery, QGrams qgStr) {
        
        // generate new q-gram variables
        QGrams newQuery = new QGrams(qgQuery.getSize());
        QGrams newStr = new QGrams(qgStr.getSize());
        
        // create a map to hold values
        TreeMap<Integer,String> tree = new TreeMap<>();
        
        // get q-gram value
        Distance [] qVal = qgQuery.getQGram();
        Distance [] sVal = qgStr.getQGram();
        
        // temp array list to hold string values
        ArrayList<Distance> temp = new ArrayList<>(Arrays.asList(sVal));
        
        for (Distance queries : qVal) {
            for (Distance strings : temp){
                if (strings !=null && queries.getString().equals(strings.getString())) {
                    newQuery.insert(queries.getString(), queries.getDistance());
                    tree.put(strings.getDistance(), strings.getString());
                    temp.remove(strings);
                    break;
                }   
            }
        }
                
        // insert values stored in map to string q-gram
        tree.entrySet().forEach((entry) -> {
            newStr.insert(entry.getValue(), entry.getKey());
        });
        
        // combine q-grams
        QGrams [] combinedValues = new QGrams[2];
        combinedValues[0] = newQuery;
        combinedValues[1] = newStr;
        return combinedValues;
        
    }
    
    public int lowerBound(QGrams [] newqgStr) {
        
        ArrayList<Distance> tempQuery = new ArrayList();
        ArrayList<Distance> tempStr = new ArrayList();
        ArrayList<Distance> finalStr = new ArrayList();
        ArrayList<Distance[]> visitedQGram = new ArrayList<>();
        
        // get q-gram value
        Distance [] qVal = newqgStr[0].getQGram();
        Distance [] sVal = newqgStr[1].getQGram();
        
        // get q-gram length
        int qLen = newqgStr[0].getSize();
        int sLen = newqgStr[1].getSize();
        
        int index = -1;
        
        // initialize tempquery with query q-grams
        for (int i = 0; i < qLen; i++)
            tempQuery.add(qVal[i]);
        
        // initialize tempstr and final str with string q-grams
        for (int i = 0; i < sLen; i++) {
            tempStr.add(sVal[i]);
            finalStr.add(sVal[i]);
        }
        
        // intialize first index
        int m[][] = new int[qLen][sLen];
        for (int i = 0; i < qLen; i++)
            Arrays.fill(m[i], -1);
        
        int j, firstVal, secondVal;
        
        for (int i = 0;  i < tempQuery.size(); i++) {
            
            // set index to -1
            j = -1;
            
            // for all q-gram values of string 
            for (Distance temp: tempStr) {
                if (tempStr.size() > 0) {
                    // if query and string match
                    if (temp.getString().equalsIgnoreCase(tempQuery.get(i).getString())) {
                        // get index of string
                        j = (int)finalStr.indexOf(temp);
                        index = (int)tempStr.indexOf(temp);
                        break;
                    }
                }
            }
            
            // if index isnt empty
            if (j != -1) {
                // if q-gram has not been visited
                if (visitedQGram.isEmpty()) {
                    m[i][j] = (int)Math.ceil((tempQuery.get(i).getDistance()-1)/3.0);
                } else {
                    firstVal = (int)Math.ceil((tempQuery.get(i).getDistance()-1)/3.0);
                    secondVal = findValue(visitedQGram, m, i, j, tempQuery, finalStr);
                    // store lowest value
                    m[i][j] = Math.min(firstVal, secondVal);
                }
                
                // store value in visitedQGram
                Distance tempDist[] = new Distance[2];
                tempDist[0] = tempQuery.get(i);
                tempDist[1] = finalStr.get(j);
                visitedQGram.add(tempDist);
                tempStr.remove(index);
            }
        }
        
        // calculate final result
        int tempResult, finalResult;
        int firstResult = (int)(Math.ceil((query.length()-3+1)/3.0));
        int secondResult = Integer.MAX_VALUE;
        
        for (int i = 0; i < tempQuery.size(); i++) {
            for (int l = 0; l < finalStr.size(); l++) {
                // if value is not empty
                if(m[i][l] != -1 ) {
                    tempResult = m[i][l] + (int)Math.ceil((query.length() - tempQuery.get(i).getDistance()-3+1)/3.0);
                    // store lowest value in second result
                    if(tempResult < secondResult)
                        secondResult = tempResult;
                }
            }
        }
            
        // return the lowest calculated value
        finalResult = Math.min(firstResult, secondResult);
        return finalResult;
    }
    
    public int findValue(ArrayList<Distance[]> visitedQGram, int m[][], int i, int j, ArrayList<Distance> query1, ArrayList<Distance> string) {
        
        int minVal = Integer.MAX_VALUE;
        int qVal, sVal, temp;
        
        for (int k = 0; k < visitedQGram.size(); k++) {
            // if the visitedQGram distances are less than query and string distances
            if((visitedQGram.get(k)[0].getDistance() < query1.get(i).getDistance()) && 
                    (visitedQGram.get(k)[1].getDistance() < string.get(j).getDistance())) {   
                
                // get distance values of q-gram pair
                qVal = query1.indexOf(visitedQGram.get(k)[0]);
                sVal = string.indexOf(visitedQGram.get(k)[1]);
                
                // calculate lowest value
                temp = (int)(m[qVal][sVal] + (Math.max(Math.ceil((query1.get(i).getDistance() - query1.get(qVal).getDistance() - 1) / 3.0),
                                        Math.abs(query1.get(i).getDistance() - query1.get(qVal).getDistance() - 
                                                (string.get(j).getDistance() - string.get(sVal).getDistance())))));
                if(temp<minVal) 
                    minVal=temp;
            }
        }
        return minVal;
    }
    
    public static void main(String[] args) {
        
        // database information
        String url = "jdbc:mysql://localhost:3306/dblp";
        String user = "root";
        String password = "Seminole-19";
        
        // query variables
        query = "spatial".toLowerCase();
        String title = "SELECT title FROM paper";
        
        // setting up algorithm
        LB topK = new LB();
        MaxHeap heap;
        
        // calculating k value
        int k = 15, kChanged = 0;
        if (k%2 != 0 && k != 1) {
            k = k+1;
            kChanged += 1;
        } else if (k == 2) {
            k = k+2;
            kChanged += 2;
        }
        
        // creating heap with k value
        heap = new MaxHeap(k);
        
        // generate q-gram for query
        QGrams qgQuery = new QGrams(query.length()-2);
        qgQuery.constructQGram(query);
        
        String str;
        int count = 0;
        int editDist = 0;
        
        // establish connection
        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(title);
                ResultSet rs = stmt.executeQuery()) {
            
            // loop through result set while counter is less than k value
            while (rs.next() && count < k) {
                str = rs.getString(1);
                // ignore strings below length of 3
                if (str.length() < 3)
                    continue;
                // calculate edit distance between query and string
                int value = topK.editDistance(query, str);
                // insert string into heap
                heap.insert(value, str);
                count++;
            }
            
            // loop through result set
            while (rs.next()) {
                str = rs.getString(1);
                // ignore strings below length of 3
                if (str.length() < 3)
                    continue;
                // generate common positional q-grams
                QGrams qgStr = new QGrams(str.length()-2);
                qgStr.constructQGram(str.toLowerCase());
                // compare query q-gram with resulting string q-gram
                QGrams [] newqgStr = topK.compareQGrams(qgQuery, qgStr);
                
                // do not need to compute lowerbound if size less than 1
                if (newqgStr[0].getSize() < 1)
                    continue;
                
                // compute lowerbound
                int bound = topK.lowerBound(newqgStr);
                // calculate edit distance value
                if (heap.topDistance() > bound) {
                    editDist = topK.editDistance(query, str);
                    // insert new distance to heap
                    if (heap.topDistance() > editDist) {
                        heap.maxHeap();
                        heap.remove();
                        heap.insert(editDist, str);
                    }
                }
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
