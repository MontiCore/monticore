/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals._visitor;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.ErrorValue;
import de.monticore.literals.mccommonliterals._ast.*;
import de.se_rwth.commons.logging.Log;

import static de.monticore.interpreter.ValueFactory.createValue;

public class MCCommonLiteralsInterpreter extends MCCommonLiteralsInterpreterTOP {

  public MCCommonLiteralsInterpreter() {
    super();
  }

  public MCCommonLiteralsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public Value interpret(ASTNullLiteral node) {
    String errorMsg = "Null should not be used";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }

  @Override
  public Value interpret(ASTBooleanLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTCharLiteral node) {
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTStringLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTNatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedNatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicLongLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicLongLiteral node) {
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicFloatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicFloatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTBasicDoubleLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public Value interpret(ASTSignedBasicDoubleLiteral node){
    return createValue(node.getValue());
  }


}
