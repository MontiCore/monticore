/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._cocos;

public class CombineExpressionsWithLiteralsCoCoChecker extends CombineExpressionsWithLiteralsCoCoCheckerTOP {

  public CombineExpressionsWithLiteralsCoCoChecker getCombineExpressionsWithLiteralsCoCoChecker() {
    CombineExpressionsWithLiteralsCoCoChecker checker = new CombineExpressionsWithLiteralsCoCoChecker();
    checker.addCoCo(new NoClassExpressionForGenerics());
    return checker;
  }
}
