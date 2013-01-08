package mathfunction;

import java.lang.Math;

public class MathFunc
{
	public static double abs(double a)
	{
		return Math.abs(a);
	}
	
	public static double sin(double a)
	{
		return Math.sin(a);
	}
	
	public static double cos(double a)
	{
		return Math.cos(a);
	}
	
	public static double tan(double a)
	{
		return Math.tan(a);
	}
	
	public static double cot(double a)
	{
		return 1/Math.tan(a);
	}
	
	public static double gauss(double a, double b, double c, double x)
	{
		return a * Math.exp(-(Math.pow((x-b), 2))/(2*c*c));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
