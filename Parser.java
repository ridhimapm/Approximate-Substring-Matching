/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* PARSER FILE PROVIDED BY DBLP */

package Parsing;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 *
 * @author ahmadrasul
 */

public class Parser {
    
    // current line from database being parsed
    int line = 0;

    //parsing variables
    private Paper paper;
    private Conference conf;
    StringBuffer authorName;
    private int current = -1;
    private int parent = -1;
    
    // database variables
    private Connection conn;
    PreparedStatement inproc, conference, author, cite;
            
    private class ConfigHandler extends DefaultHandler {
        
        public void startElement(String namespaceURI, String localName,
                String rawName, Attributes atts) throws SAXException {
            
            // if element is a paper
            if (rawName.equals("inproceedings")) {
                paper = new Paper();
                paper.key = atts.getValue("key");
                current = 1;
                parent = 1;
            }
            // if element is a conference
            else if (rawName.equals("proceedings")) {
                conf = new Conference();
                conf.key = atts.getValue("key");
                current = 1;
                parent = 2;
            } 
            // if element is an author
            else if (rawName.equals("author") && parent == 1) {
                authorName = new StringBuffer();
            }
            
            // getting current element
            switch (parent) {
                case 1 -> current = Paper.getElement(rawName);
                case 2 -> current = Conference.getElement(rawName);
                case -1 -> {
                    parent = 0;
                    current = 0;
                }
                default -> current = 0;
            }
            
            // increment lines parsed
            line++;
        }
        
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            
            // for paper
            if (parent == 1) {
                
                String str = new String(ch, start, length).trim();
                
                // store current value in appropriate variable
                switch (current) {
                    case 2 -> authorName.append(str);
                    case 3 -> paper.title = str;
                    case 4 -> paper.year = Integer.parseInt(str);
                    case 5 -> paper.citations.add(str);
                    case 6 -> paper.conference = str;
                    default -> {}
                }
            }
            
            // for conference
            else if (parent == 2) {
                
                String str = new String(ch, start, length).trim();
                
                // store current value in appropriate variable
                if (current == 2) {
                    conf.name = str;
                } else if (current == 3) {
                    conf.detail = str;
                }
            }
        }
        
        public void endElement(String namespaceURI, String localName,
                String rawName) throws SAXException {
            
            // paper author
            if (rawName.equals("author") && parent == 1) {
                paper.authors.add(authorName.toString().trim());
            }
            
            // if element is paper
            if (Element.getElement(rawName) == 1) {
                
                // reset parent
                parent = 1;
                
                try {
                    
                    // if variables are empty
                    if (paper.title.equals("") || paper.conference.equals("")
                            || paper.year == 0) {
                        System.out.println("Error in parsing " + paper);
                        System.exit(0);
                    }
                    
                    // prepare statements
                    inproc.setString(1, paper.title);
                    inproc.setInt(2, paper.year);
                    inproc.setString(3, paper.conference);
                    inproc.setString(4, paper.key);
                    inproc.addBatch();
                    
                    // for array list of authors
                    for (String authors: paper.authors) {
                        author.setString(1, authors);
                        author.setString(2, paper.key);
                        author.addBatch();
                    }
                    
                    // for array list of citations
                    for (String cited: paper.citations) {
                        if (!cited.equals("...")) {
                            cite.setString(1, paper.key);
                            cite.setString(2, cited);
                            cite.addBatch();
                        }
                    }
                }
                
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Line: " + line);
                    System.exit(0);
                }
            }
            
            // if element is conference
            else if (Element.getElement(rawName) == 2) {
                
                // reset parent
                parent = -1;
                
                try {
                
                    // if conference name is empty
                    if (conf.name.equals(""))
                        conf.name = conf.detail;
                    
                    // if all conference variable are empty
                    if (conf.name.equals("") || conf.detail.equals("")
                            || conf.key.equals("")) {
                        System.out.println("Error in parsing " + paper);
                        System.exit(0);
                    }
                    
                    // prepare statements
                    conference.setString(1, conf.key);
                    conference.setString(2, conf.name);
                    conference.setString(3, conf.detail);
                    conference.addBatch();
                }
                
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Line: " + line);
                    System.exit(0);
                }
            }
            
            // execute batch every 10000 lines parsed
            if (line % 10000 == 0) {
            
                try {
                    inproc.executeBatch();
                    conference.executeBatch();
                    author.executeBatch();
                    cite.executeBatch();
                    conn.commit();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        private void Message(String mode, SAXParseException exception) {
            System.out.println(mode + " Line: " + exception.getLineNumber()
                    + " URI: " + exception.getSystemId() + "\n" + " Message: "
                    + exception.getMessage());
        }

        public void warning(SAXParseException exception) throws SAXException {

            Message("**Parsing Warning**\n", exception);
            throw new SAXException("Warning encountered");
        }

        public void error(SAXParseException exception) throws SAXException {

            Message("**Parsing Error**\n", exception);
            throw new SAXException("Error encountered");
        }

        public void fatalError(SAXParseException exception) throws SAXException {

            Message("**Parsing Fatal Error**\n", exception);
            throw new SAXException("Fatal Error encountered");
        }

    }
    
    Parser(String uri) throws SQLException {
        
        try {
            
            System.out.println("Parsing the dataset!");
            
            // establish connection
            conn = DBConn.getConn();
            conn.setAutoCommit(false);
            
            // prepare queries
            inproc = conn.prepareStatement("insert into paper(title,year,conference,paper_key) values (?,?,?,?)");
            conference = conn.prepareStatement("insert into conference(conf_key,name,detail) values (?,?,?)");
            author = conn.prepareStatement("insert into author(name,paper_key) values (?,?)");
            cite = conn.prepareStatement("insert into citation(paper_cite_key,paper_cited_key) values (?,?)");
            
            /* adding expansion limit
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            String JDK_ENTITY_EXPANSION_LIMIT = "jdk.xml.entityExpansionLimit";
            dbf.setAttribute(JDK_ENTITY_EXPANSION_LIMIT, "250000");*/
            
            // setting up XML SAX parser
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            ConfigHandler handler = new ConfigHandler();
            parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", true);
            parser.parse(new File(uri), handler);
            
            // execute queries
            try {
                inproc.executeBatch();
                conference.executeBatch();
                author.executeBatch();
                cite.executeBatch();
                conn.commit();
            } 
            
            catch (SQLException e) {
                e.printStackTrace();
            }
            
            // close connection
            conn.close();
            System.out.println("Parsing complete!");
        }
        
        catch (IOException e) {
            System.out.println("Error reading URI: " + e.getMessage());
        } 
        
        catch (SAXException e) {
            System.out.println("Error in parsing: " + e.getMessage());
        }
        
        catch (ParserConfigurationException e) {
            System.out.println("Error in XML parser configuration: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
        Parser file = new Parser("/Users/ahmadrasul/Desktop/Dataset/dblp.xml");
    }
}
