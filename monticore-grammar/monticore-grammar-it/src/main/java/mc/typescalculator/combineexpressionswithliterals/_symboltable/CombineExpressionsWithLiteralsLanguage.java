/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.combineexpressionswithliterals._symboltable;

import mc.typescalculator.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;

public class CombineExpressionsWithLiteralsLanguage extends CombineExpressionsWithLiteralsLanguageTOP {

  public CombineExpressionsWithLiteralsLanguage(){
    super("CombineExpressionsWithLiterals","cex");
  }

  @Override
  protected CombineExpressionsWithLiteralsModelLoader provideModelLoader() {
    return CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsModelLoaderBuilder().setModelingLanguage(this).build();
  }

}
