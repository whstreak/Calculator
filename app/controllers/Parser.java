package calculator;

import java.util.*;
import java.lang.reflect.*;
import mathfunction.*;


// To parse a list of tokens from algebraic formula into Reverse Polish Notation (RPN)
// and evaluate the parsed RPN
public class Parser
{
	private String algebraic_string; // Algebraic formula to be parsed  INPUT
	private Tokenizer tokenizer;
	
	public ArrayList<Token> rpn = new ArrayList<Token>(); // List of tokens in RPN order  OUTPUT
	
	public Parser(String algebraic)
	{
		algebraic_string = algebraic;
		tokenizer = new Tokenizer(algebraic_string);
	}
	
	String get_parsed_str()
	{
		return algebraic_string;
	}
	
	public void generate_RPN() throws CalculatorException
	{
		try
		{
			tokenizer.tokenize();
			for (int i=0; i<tokenizer.tokens.size(); i++)
			{
				System.out.println("Token " + i + " is type " + tokenizer.tokens.get(i).get_type() + " " + tokenizer.tokens.get(i).toString());
			}
		}
		catch (CalculatorException ex)
		{
			throw ex;
		}
		
		Stack<Token> stack = new Stack<Token>();
		
		for (int i=0; i < tokenizer.tokens.size(); i++)
		{
			Token token = tokenizer.tokens.get(i);
			System.out.println("Token" + i + " " + token.toString());
			
			switch (token.get_type())
			{
				case Token.FLOAT_POINT_NUMBER :
					rpn.add(token);
					break;
					
				case Token.FUNCTION :
					rpn.add(token);
					break;
					
				case Token.OPERATOR :
					while ( (!stack.empty()) && (stack.peek().get_type() == Token.OPERATOR) &&
							(  ( ((Operator)token).associativity == Operator.ASSOCIATIVITY_LEFT && ((Operator)token).precedence <= ((Operator)stack.peek()).precedence ) ||
							   ( ((Operator)token).precedence < ((Operator)stack.peek()).precedence )
							)
						  )
					{
						rpn.add(stack.pop());
					}
					stack.push(token);		
					break;
					
				case Token.LEFT_PRENTHESIS :
					stack.push(token);
					break;
					
				case Token.RIGHT_PRENTHESIS :
					while (!stack.empty() && stack.peek().get_type() != Token.LEFT_PRENTHESIS)
					{
						rpn.add(stack.pop());
					}
					
					if (stack.empty())
					{
						throw new CalculatorException("Invalid algebraic expression: parentheses not match.");
					}				
					stack.pop();
					break;
					
				default :
					throw new CalculatorException("Invalid algebraic expression: unrecognized token.");
			}
		} // End of for loop
		
		while (!stack.empty())
		{
			if (stack.peek().get_type() != Token.OPERATOR)
				throw new CalculatorException("Invalid algebraic expression");
			
			rpn.add(stack.pop());
		}
	} // End of function generate_RPN()
	
	public float evaluate_RPN() throws CalculatorException
	{
		Stack<Double> result = new Stack<Double>();
		for (int i=0; i < rpn.size(); i++)
		{
			Token token = rpn.get(i);
			switch (token.get_type())
			{
			case Token.FLOAT_POINT_NUMBER :
				result.push(new Double(((FloatNumber)token).get_value()));
				break;
				
			case Token.FUNCTION :
				try
				{
					int arg_num = ((Function)token).number_of_param;
					Class<?>[] arg_types = new Class<?>[arg_num];
					Object [] arguments = new Object[arg_num];
					
					for(int j=0; j < arg_num; j++)
					{
						arg_types[j] = double.class;
						arguments[j] = new Double(((Function)token).args[j]);
					}
					
					Method method = MathFunc.class.getMethod( ((Function)token).function_name, arg_types);
					Object return_value = method.invoke(null, arguments);
					result.push((Double)return_value);
				}
				catch(NoSuchMethodException exp)
				{
					throw new CalculatorException("Invalid algebraic expression: math function not supported.");
				}
				catch(IllegalArgumentException exp)
				{
					throw new CalculatorException("Invalid algebraic expression: wrong arguments to math function.");
				}
				catch(Exception exp)
				{
					System.out.println(((Function)token).function_name + "evaluation error!");
				}
				break;
				
			case Token.OPERATOR :
				if (((Operator)token).unary)
				{
					Double operand = result.pop();
					result.push (new Double( unary_op(((Operator)token).operator, operand.doubleValue())));
				}
				else
				{
					Double right = result.pop();
					Double left = result.pop();
					result.push (new Double( binary_op(((Operator)token).operator, left.doubleValue(), right.doubleValue())));
				}
				break;
				
			default : ;			
			}
		}
		
		return result.pop().floatValue();
	}
	
	double binary_op (char op, double left, double right)
	{
		switch (op)
		{
		case '+': return left + right;
		case '-': return left - right;
		case '*': return left * right;
		case '/': return left / right;
		default :
			return -1;
		}
	}
	
	double unary_op (char op, double operand)
	{
		switch (op)
		{
		case '+': return operand;
		case '-': return -operand;
		default :
			return -1;
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			Parser parser = new Parser("2  + 3 * (-4) + abs(-10)");
			parser.generate_RPN();
			float result = parser.evaluate_RPN();
			
			System.out.println("Algebraic formula " + parser.get_parsed_str() + " results " + result + "!\n");
		}
		catch(CalculatorException exp)
		{
			System.out.println(exp.getMessage());
		}
		
	}
}