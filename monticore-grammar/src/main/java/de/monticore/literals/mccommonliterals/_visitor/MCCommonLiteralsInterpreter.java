/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals._visitor;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.literals.mccommonliterals._ast.*;

public class MCCommonLiteralsInterpreter extends MCCommonLiteralsInterpreterTOP {

  public MCCommonLiteralsInterpreter() {
    super();
  }

  public MCCommonLiteralsInterpreter(IMCCommonLiteralsContext context, ModelInterpreter realThis) {
    super(context, realThis);
  }

  @Override
  public Value interpret(ASTNullLiteral node){
    return ValueFactory.createValue(null);
  }

  @Override
  public Value interpret(ASTBooleanLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTCharLiteral node) {
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTStringLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTNatLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedNatLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicLongLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicLongLiteral node) {
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicFloatLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicFloatLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicDoubleLiteral node){
    return ValueFactory.createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicDoubleLiteral node){
    return ValueFactory.createValue(node.getValue());
  }


}
