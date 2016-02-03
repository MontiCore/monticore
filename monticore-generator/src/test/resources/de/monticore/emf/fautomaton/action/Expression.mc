package de.monticore.emf.fautomaton.action;


grammar Expression{

	ident NUMBER "('1'..'9')('0'..'9')*";

	interface Expression;
	ast Expression = Varname:Name ;
	
	interface Value; 
	ast Value = A:NUMBER;
	
	IncreaseExpression implements Expression = Varname:Name "++";
	
	DecreaseExpression implements Expression = Varname:Name "--";
		
	Assignment implements Expression = Varname:Name "=" (RHS:Name | Value:NUMBER );
	
	ComplexAssigment extends Assignment implements Value= A:NUMBER ;
	
    ast ComplexAssigment = Bds:/java.util.Vector;
}