package calculator;

import java.util.*;
import java.util.regex.*;

class Token
{
  // token types
  // token types may also be implemented through RTTI
  public static final int FLOAT_POINT_NUMBER = 0;
  public static final int OPERATOR = 1;
  public static final int LEFT_PRENTHESIS = 2;
  public static final int RIGHT_PRENTHESIS = 3;
  public static final int FUNCTION = 4;

  //
  protected int type;

  //
  public int get_type() {return type;}
}


class FloatNumber extends Token
{
  private double value;

  public double get_value() {return value;}

  FloatNumber(double val)
  {
    value = val;
    type = FLOAT_POINT_NUMBER;
  }
  
  public String toString()
  {
    return ("" + value);
  }
}


class Operator extends Token
{
  // operator precedence
  public static final int PRECEDENCE_0 = 0; // '+', '-' will be with precedence 0
  public static final int PRECEDENCE_1 = 1; // '*', '/' will be with precedence 1
  public static final int PRECEDENCE_2 = 2; // unary '+', '-' will be with precedence 2

  // operator associativity
  public static final int ASSOCIATIVITY_LEFT = 0;
  public static final int ASSOCIATIVITY_RIGHT = 1;


  public char operator;
  public int precedence;
  public boolean unary;
  public int associativity;

  Operator(char op)
  {
    operator = op;
    type = OPERATOR;
  }
  
  public String toString()
  {
    return ("Operator " + operator +
        ": Precedence " + precedence +
        ", Unary " + unary +
        ", Associativity " + associativity);
  }
}


class LeftPrenthesis extends Token
{
  LeftPrenthesis()
  {
    type = LEFT_PRENTHESIS;
  }
  
  public String toString()
  {
    return "(";
  }
}


class RightPrenthesis extends Token
{
  RightPrenthesis()
  {
    type = RIGHT_PRENTHESIS;
  }
  
  public String toString()
  {
    return ")";
  }
}


class Function extends Token
{
  public String function_name;
  public int number_of_param;
  public double[] args = new double[10];

  Function(String name)
  {
    function_name = name;
    type = FUNCTION;
  }
  
  public String toString()
  {
    String params = "";
    for (int i=0; i < number_of_param; i++)
    {
      params += args[i];
      params += ",";
    }
    return function_name + "(" + params + ")";
  }
}


public class Tokenizer
{
  private String input;
  private int string_index = 0;

  public ArrayList<Token> tokens = new ArrayList<Token>();

  Tokenizer(String string_to_be_tokenized)
  {
    input = string_to_be_tokenized;
  }

  char get_next_non_blank_char()
  {
    while (input.charAt(string_index) == ' ' || input.charAt(string_index) == '\t')
      string_index++;

    return input.charAt(string_index);
  }

  boolean is_operator(char c)
  {
    return ( c == '+' || c == '-' || c == '*' || c == '/');
  }

  boolean is_digit(char c)
  {
    return ( (c >= '0' && c <= '9') || c == '.');
  }

  boolean is_letter(char c)
  {
    c &= 0x5f;
    return ( c >= 'A' && c <= 'Z');
  }

  public void tokenize() throws CalculatorException
  {
    boolean has_left_operand = false;

    input = input.trim();

    while (string_index < input.length())
    {
      char ch = get_next_non_blank_char();

      // operator
      if (is_operator(ch))
      {
        Operator op = new Operator(ch);

        if (has_left_operand)
        {
          op.unary = false;
          op.associativity = Operator.ASSOCIATIVITY_LEFT;
        }
        else
        {
          op.unary = true;
          op.associativity = Operator.ASSOCIATIVITY_RIGHT;
        }

        if (ch=='+' || ch=='-')
        {
          op.precedence = (op.unary) ? Operator.PRECEDENCE_2 : Operator.PRECEDENCE_0;
        }
        else
        {
          op.precedence = Operator.PRECEDENCE_1;
        }

        tokens.add(op);
      }
      // float number
      else if (is_digit(ch))
      {
        has_left_operand = true;
        int beginIndex = string_index++;

        while ((string_index < input.length()) && is_digit(input.charAt(string_index)))
          string_index++;

        String number = (string_index == input.length()) ?
                        input.substring(beginIndex) : input.substring(beginIndex, string_index);
        Float f = new Float(number);
        FloatNumber f_num = new FloatNumber(f.doubleValue());
        tokens.add(f_num);

        string_index--;
      }
      // left prenthesis
      else if (ch == '(')
      {
        has_left_operand = false;
        tokens.add(new LeftPrenthesis());
      }
      // right prenthesis
      else if (ch == ')')
      {
        has_left_operand = true;
        tokens.add(new RightPrenthesis());
      }
      // function
      else if (is_letter(ch))
      {
        has_left_operand = true;
        int beginIndex = string_index;

        char temp;
        do
        {
          string_index++;

          if (string_index < input.length())
            temp = input.charAt(string_index);
          else
            break;
        }
        while( is_letter(temp) || (temp >= '0' && temp <= '9') );

        String name = (string_index == input.length()) ?
                      input.substring(beginIndex) : input.substring(beginIndex, string_index);

        Function func = new Function(name); 

        //char next_ch = get_next_non_blank_char();
        if ((string_index == input.length()) || get_next_non_blank_char() != '(')
        {
          throw new CalculatorException("Invalid algebraic expression: wrong function format!");
        }

        beginIndex = ++string_index;
        
        while (input.charAt(string_index) != ')')
          string_index++;

        String param_str = input.substring(beginIndex, string_index).trim();
        if (param_str.length() > 0)
        {
          String[] params = param_str.split(",");
          func.number_of_param = params.length;

          Pattern p = Pattern.compile("\\d+\\.?\\d*");
          for (int i=0; i<params.length; i++)
          {
            Matcher m = p.matcher(params[i]);
            if (m.find())
            {
              func.args[i] = Double.parseDouble(m.group());
            }
          }       
        }
        else
          func.number_of_param = 0;

        tokens.add(func);
      }
      // others
      else
      {
        throw new CalculatorException("Invalid algebraic expression: invalid character found!");
      }

      string_index++;
    }
  }

  public static void main(String[] args)
  {
    Tokenizer token_formula = new Tokenizer("3.14+22/(3.14+gauss(3,2,0,1))");
    try
    {
      token_formula.tokenize();
      System.out.println("There are " + token_formula.tokens.size() + " tokens from the formula.");
      for (int i=0; i<token_formula.tokens.size(); i++)
      {
        System.out.println("Token " + i + " is type " + token_formula.tokens.get(i).get_type() + " " + token_formula.tokens.get(i).toString());
      }
    }
    catch(CalculatorException ex)
    {
      System.out.println(ex.getMessage());
    }
  }
}