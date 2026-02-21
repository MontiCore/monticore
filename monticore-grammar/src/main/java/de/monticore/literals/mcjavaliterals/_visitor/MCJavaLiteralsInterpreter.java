/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mcjavaliterals._visitor;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.literals.mcjavaliterals._ast.*;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class MCJavaLiteralsInterpreter extends MCJavaLiteralsInterpreterTOP {
  
  public MCJavaLiteralsInterpreter() {
    super();
  }
  
  public MCJavaLiteralsInterpreter(IModelInterpreter realThis) {
    super(realThis);
  }
  
  @Override
  public MIValue interpret(ASTIntLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public MIValue interpret(ASTLongLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public MIValue interpret(ASTFloatLiteral node) {
    return createValue(node.getValue());
  }
  
  @Override
  public MIValue interpret(ASTDoubleLiteral node) {
    return createValue(node.getValue());
  }
}
