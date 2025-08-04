/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals._visitor;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.literals.mccommonliterals._ast.*;
import de.se_rwth.commons.logging.Log;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class MCCommonLiteralsInterpreter extends MCCommonLiteralsInterpreterTOP {

  public MCCommonLiteralsInterpreter() {
    super();
  }

  public MCCommonLiteralsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public MIValue interpret(ASTBooleanLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTCharLiteral node) {
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTStringLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTNatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTSignedNatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTBasicLongLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTSignedBasicLongLiteral node) {
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTBasicFloatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTSignedBasicFloatLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTBasicDoubleLiteral node){
    return createValue(node.getValue());
  }

  @Override
  public MIValue interpret(ASTSignedBasicDoubleLiteral node){
    return createValue(node.getValue());
  }


}
