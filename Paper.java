/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsing;

import java.util.ArrayList;

/**
 *
 * @author ahmadrasul
 */

public class Paper {
    
    public int year;
    public String key;
    public String title;
    public String conference;
    public ArrayList<String> authors;
    public ArrayList<String> citations;
    
    public String toString() {
        return "Title: " + title + " Authors: " + authors.toString() + " Conference: " 
                + conference + " Year: " + year + " Key: " + key;
    }
    
    public static int getElement(String name) {
        return switch (name) {
            case "inproceedings" -> 1;
            case "author" -> 2;
            case "title", "sub", "sup", "tt", "i" -> 3;
            case "year" -> 4;
            case "cite" -> 5;
            case "booktitle" -> 6;
            default -> 0;
        };
    }
    
    public static String getElementName(int input) {
        return switch (input) {
            case 1 -> "inproceedings";
            case 2 -> "author";
            case 3 -> "name";
            case 4 -> "year";
            case 5 -> "cite";
            case 6 -> "booktitle";
            default -> "other";
        };
    }
    
    public Paper() {
        year = 0;
        key = "";
        title = "";
        conference = "";
        authors = new ArrayList<String>();
        citations = new ArrayList<String>();
    }
}
