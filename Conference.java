/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Parsing;

/**
 *
 * @author ahmadrasul
 */

public class Conference {
    
    public String key;
    public String name;
    public String detail;
    
    public static int getElement(String name) {
        return switch (name) {
            case "proceedings" -> 1;
            case "booktitle" -> 2;
            case "title" -> 3;
            default -> 0;
        };
    }
    
    public static String getElementName(int input) {
        return switch (input) {
            case 1 -> "proceedings";
            case 2 -> "booktitle";
            case 3 -> "title";
            default -> "other";
        };
    }
    
    public Conference() {
        key = "";
        name = "";
        detail = "";
    }
}
