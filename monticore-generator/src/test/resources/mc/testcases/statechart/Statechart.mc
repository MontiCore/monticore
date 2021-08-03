/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.statechart;

grammar Statechart { 

  options { 
	parser lookahead = 3
	lexer lookahead = 7 
	compilationunit Statechart	
  }
  
  concept globalnaming { 
	define Statechart.Name;
	define State.Name;
	usage Transition.From;
	usage Transition.To;
  }	
      
  Statechart implements SCStructure= "statechart" Name "{"
    (States:State | Transitions:Transition )* "}";
  
  
  EntryAction= "entry" ":" Block:BlockStatement;
  
  ExitAction= "exit" ":" Block:BlockStatement;
  
  DoAction= "do" ":" Block:BlockStatement;
  
  InternTransition = "-intern>"
    ( ":" 
      (Event:Name ( 
        "(" (Arguments:Argument ( "," Arguments:Argument)*) ")" )? 
      )?
      ("[" PreCondition: Expression "]")?
      ("/" Action: BlockStatement ("[" PostCondition: Expression "]")?)? ";" 
    | ";");
                 
  State implements SCStructure = 
    "state" Name ("<<" (Initial:["initial"] | Final:["final"]) ">>")*
    ( ("{" ("[" Invariant:Expression ( "&&" Invariant:Expression)* "]")?
      (EntryAction:EntryAction)?
      (DoAction)? 
      (ExitAction:ExitAction)? 
      (States:State
        | Transitions:Transition
        | InternTransitions:InternTransition
      )* "}") | ";")
  ;
 
  Transition = From:Name "->" To:Name 
    (":" (Event:Name ( 
      "(" ((Arguments:Argument ( "," Arguments:Argument)*)?) ")" )? )?
    ("[" PreCondition: Expression "]")?
    ("/" Action: BlockStatement ("[" PostCondition: Expression "]")?)? ";"
  | ";");
    
  Argument= ParamType:Name ParamName:Name;
 
  interface SCStructure; 
  
  ast SCStructure =
  	Name
  	States:State*
  	Transitions:Transition*
    method public String toString() {{return name;}} 
  ;
  
  ast State =
    method public String toString() {
        return name;
    }
    method public mc.ast.ASTNode getParentElement() {
      if (get_Parent() != null && get_Parent() instanceof mc.ast.ASTList) {
        return get_Parent().get_Parent();
      }
      return get_Parent();
    }
  ;
  
  ast Transition =
    method public mc.ast.ASTNode getParentElement() {
      if (get_Parent() != null && get_Parent() instanceof mc.ast.ASTList) {
        return get_Parent().get_Parent();
      }
      return get_Parent();
    }
  ;
  
  interface Statement;
  
  BlockStatement implements ("{")=>Statement =
    "{" (Statements:Statement)* "}"
  ;
  
  interface Expression;
  
  ExpressionStatement implements (Expression ";")=>Statement =
    Expression:Expression ";"
  ;
  
  EqualityExpression implements (Name ("=="|"!=") )=>Expression =
        LeftOperand:Name
          Operator:["!=" | "=="] RightOperand:Name
    ;
  
  MethodInvocationWithQualifiedName 
    implements (Name (options{greedy=true;}: "." Name)* "(")=>Expression =
      Name
      (options{greedy=true;}: "." Name)*
      "("
        (Arguments: Expression ("," Arguments: Expression)*)?
      ")"
  ;
  
  FieldAccess implements (Name)=>Expression =
    Name
  ;
  
}
