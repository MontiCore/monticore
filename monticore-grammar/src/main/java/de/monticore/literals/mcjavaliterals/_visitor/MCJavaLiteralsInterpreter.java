package de.monticore.literals.mcjavaliterals._visitor;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.literals.mcjavaliterals._ast.*;

import static de.monticore.interpreter.ValueFactory.createValue;

public class MCJavaLiteralsInterpreter extends MCJavaLiteralsInterpreterTOP {
  
  public MCJavaLiteralsInterpreter() {
    super();
  }
  
  public MCJavaLiteralsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public Value interpret(ASTIntLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public Value interpret(ASTLongLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public Value interpret(ASTFloatLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public Value interpret(ASTDoubleLiteral node) {
    return createValue(node.getValue());
  }
}
