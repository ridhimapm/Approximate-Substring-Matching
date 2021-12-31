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

public class Element {
    
    public static int getElement(String name) {
        return switch (name) {
            case "inproceedings" -> 1;
            case "proceedings" -> 2;
            default -> 0;
        };
    }

    public static String getElementName(int input) {
        return switch (input) {
            case 1 -> "inproceedings";
            case 2 -> "proceedings";
            default -> "other";
        };
    }
}
