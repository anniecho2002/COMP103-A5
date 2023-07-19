/* Code for COMP103 - 2021T2, Assignment 5
 * Name: Annie Cho
 * Username: choanni
 * ID: 300575457
 */

/**
 * ExpElem: an element of an expression - either a String (an operator) or a number
 *  An operator (or a named constant) is stored in the operator field.
 *  A number is stored in the value field, and "#" is stored in the operator field.
 */

public class ExpElem{
    public final String operator;
    public final double value;

    /** Construct an Expr given an operator */ 
    public ExpElem(String token){
        operator = token;
        value = Double.NaN;
    }

    /** Construct an ExpElem given a number */ 
    public ExpElem(double v){
        operator = "#";
        value = v;
    }
     
    public String toString(){
        return  (operator=="#")?""+value:operator;
    }


}
