/* Code for COMP103 - 2021T2, Assignment 5
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        /*# YOUR CODE HERE */
        ExpElem item = expr.getItem();
        String operator = item.operator;
        double answer = 0;
        int numChild = expr.numberOfChildren();
        if(operator.equals("#")){ return item.value; }
        else if(operator.equals("PI")){ return Math.PI; }
        else if(operator.equals("E")){ return Math.E; }
        else if(operator.equals("+")){
            for (GTNode<ExpElem> c : expr){ answer = answer + evaluate(c); }
            return answer;
        }
        else if(operator.equals("-")){
            answer = evaluate(expr.getChild(0));
            for (int i = 1; i < numChild; i++){ answer = answer - evaluate(expr.getChild(i)); }
            return answer;
        }
        else if(operator.equals("*")){
            answer = evaluate(expr.getChild(0));
            for (int i = 1; i < numChild; i++){ answer = answer * evaluate(expr.getChild(i)); }
            return answer;
        }
        else if(operator.equals("/")){
            answer = evaluate(expr.getChild(0));
            for (int i = 1; i < numChild; i++){ answer = answer / evaluate(expr.getChild(i)); }
            return answer;
        }
        else if(operator.equals("log")){
            if(numChild == 1){ return Math.log10(evaluate(expr.getChild(0))); }
            else if(numChild == 2){ return Math.log10(evaluate(expr.getChild(0))) / Math.log10(evaluate(expr.getChild(1))); }
            else if(numChild > 2){ 
                UI.println("Too many operands."); 
                return Double.NaN;
            }
        }
        else if(operator.equals("ln")){ return Math.log(evaluate(expr.getChild(0))); }
        else if(operator.equals("^")){ return Math.pow(evaluate(expr.getChild(0)), evaluate(expr.getChild(1))); }
        else if(operator.equals("sqrt")){ return Math.sqrt(evaluate(expr.getChild(0))); }
        else if(operator.equals("sin")){ return Math.sin(evaluate(expr.getChild(0))); }
        else if(operator.equals("cos")){ return Math.cos(evaluate(expr.getChild(0))); }
        else if(operator.equals("tan")){ return Math.tan(evaluate(expr.getChild(0))); }
        else if(operator.equals("dist")){
            if(numChild == 4){ return Math.sqrt(
                                      Math.pow(evaluate(expr.getChild(0)) - evaluate(expr.getChild(2)), 2) + 
                                      Math.pow(evaluate(expr.getChild(1)) - evaluate(expr.getChild(3)), 2)); }
            else if (numChild == 6){ return Math.sqrt(
                                            Math.pow(evaluate(expr.getChild(0)) - evaluate(expr.getChild(3)), 2) +
                                            Math.pow(evaluate(expr.getChild(1)) - evaluate(expr.getChild(4)), 2) +
                                            Math.pow(evaluate(expr.getChild(2)) - evaluate(expr.getChild(5)), 2));
            }
            else{
                UI.println("Cannot calculate distance. Use 4 or 6 operands please.");
                return Double.NaN;
            }
        }
        else if(operator.equals("avg")){
            if(numChild >= 1){
                answer = 0;
                for (int i = 0; i < numChild; i++){ answer = answer + evaluate(expr.getChild(i)); }
                return answer/numChild;
            }
        }
        else{
            UI.printf("%s is not a valid operator. \n", operator);
            return Double.NaN;
        }
        return answer;
    }

    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
                                                      // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}

