package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;

import views.html.*;

import calculator.*;

public class Application extends Controller {

	/**
	 * Form to let user key in formula
	 */
	public static class Formula {
		@Required public String formula;
	}
  
 	// Actions

	// Home page
  	public static Result index() {
    	return ok(
    		index.render(form(Formula.class))
    	);
  	}

  	// Handle formula submission
  	public static Result calculate() {
  		Form<Formula> form = form(Formula.class).bindFromRequest();
      
  		if(form.hasErrors()) {
  			return badRequest(index.render(form));
  		} else {
  			Formula data = form.get();
        float result = 0;
        boolean status = false;
        String ret_string;

        try
        {
          Parser parser = new Parser(data.formula);
          parser.generate_RPN();
          result = parser.evaluate_RPN();
          ret_string = "Enjoy the calculator.";
          status = true;
        }
        catch(CalculatorException exp)
        {
          ret_string = exp.getMessage();
        }

  			// Calculation is done here

  			return ok(
  				calculate.render(data.formula, status, result, ret_string)
  			);
  		}
  	} 
}