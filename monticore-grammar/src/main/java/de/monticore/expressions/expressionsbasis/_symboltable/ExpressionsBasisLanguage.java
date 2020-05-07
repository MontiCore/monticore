/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.antlr4.MCConcreteParser;

public class ExpressionsBasisLanguage extends ExpressionsBasisLanguageTOP {
  public ExpressionsBasisLanguage(){
    super("ExpressionsBasis","exp");
  }

  @Override
  protected ExpressionsBasisModelLoader provideModelLoader(){
    return new ExpressionsBasisModelLoader(this);
  }

  @Override
  public MCConcreteParser getParser() {
    return null;
  }
}
