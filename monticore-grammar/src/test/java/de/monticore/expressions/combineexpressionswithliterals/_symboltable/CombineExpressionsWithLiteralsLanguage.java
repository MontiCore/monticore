/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._symboltable;

public class CombineExpressionsWithLiteralsLanguage extends CombineExpressionsWithLiteralsLanguageTOP {

  public CombineExpressionsWithLiteralsLanguage(){
    super("CombineExpressionsWithLiteralsLanguage","ce");
  }

  @Override
  protected CombineExpressionsWithLiteralsModelLoader provideModelLoader() {
    return new CombineExpressionsWithLiteralsModelLoader(this);
  }
}
