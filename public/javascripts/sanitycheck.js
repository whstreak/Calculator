<script type="text/javascript">
// This function is to do sanity check for a user into algebraic formula
// To check whether the formula is syntax valid or not

// Return value: 	true  --> the formula is syntax valid
//					false --> the formula is syntax invalid
function sanityCheck(formula)
{
	alert("sanityCheck");
	var parenthesis = new Array();
	// Flag to store what's previous symbol
	// "B": beginning of formula;
	// "O": operator;
	// "D": number;
	// "L": function;
	// "(": left parenthesis;
	// ")": right parenthesis
	var previousSymbol = "B";
	// trim start and end spaces from formula
	formula = formula.replace(/^\s\s*/, "").replace(/\s\s*$/, "");

    for (var i=0; i < formula.length; i++)
    {
    	while((i<formula.length) && (formula.charAt(i)==" " || formula.charAt(i)=="\t"))
    		i++;

    	if(isOperator(formula.charAt(i)))
    	{
    		if((previousSymbol == "B") && (formula.charAt(i)=="*" || formula.charAt(i)==""/))
    			return false;
    		else if(previousSymbol == "O" || previousSymbol == "(")
    			return false;

    		if(i == (formula.length-1))
    			return false;

    		previousSymbol = "O";
    	}
    	else if(isDigit(formalua.charAt(i)))
    	{
    		if( previousSymbol == "D" || previousSymbol == "L" || previousSymbol == ")" )
    			return false;
    		while( ((++i) < formula.length) && isDigit(formula.charAt(i)) )
    			;
    		previousSymbol = "D";
    		i--;
    	}
    	else if(isLetter(formula.charAt(i)))
    	{
    		if( previousSymbol == "D" || previousSymbol == "L" || previousSymbol == ")" )
    			return false;
    		while( ((++i) < formula.length) && isLetter(formula.charAt(i)) )
    			;
    		while((i<formula.length) && (formula.charAt(i)==" " || formula.charAt(i)=="\t"))
    			i++;
    		if(formula.charAt(i) != "(")
    			return;
    		while( ((++i) < formula.length) && (formula.charAt(i) != ")") )
    			;
    		if(i == formula.length)
    			return false;

    		previousSymbol = "L";
    	}
    	else if(formula.charAt(i) == "(")
    	{
    		if( previousSymbol == "D" || previousSymbol == "L" || previousSymbol == ")" )
    			return false;
    		if(i == (formula.length-1))
    			return false;

    		parenthesis.push("(");
    		previousSymbol = "(";
    	}
    	else if(formula.charAt(i) == ")")
    	{
    		if( previousSymbol == "B" || previousSymbol == "O" )
    			return false;
    		if( parenthesis.length == 0 || parenthesis.pop() != "(")
    			return false;

    		previousSymbol = ")";
    	}
    	else
    		return false;
    }

    if( parenthesis.length > 0)
    	return false;

    return true;
}

function isOperator(ch)
{
	if(ch=="+" || ch=="-" || ch=="*" || ch=="/")
	{
		return true;
	}
	else
	{
		return false;
	}
}

function isDigit(ch)
{
	if(ch=="." || (ch>="0" && ch<="9"))
	{
		return true;
	}
	else
	{
		return false;
	}
}

function isLetter(ch)
{
	ch = ch & 0x5f;

	return (ch>="A" && ch<="Z");
}

</script>