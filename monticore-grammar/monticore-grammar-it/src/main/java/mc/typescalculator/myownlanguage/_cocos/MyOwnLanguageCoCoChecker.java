/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.myownlanguage._cocos;

public class MyOwnLanguageCoCoChecker extends MyOwnLanguageCoCoCheckerTOP {

  public MyOwnLanguageCoCoChecker getMyOwnLanguageCoCoChecker() {
    MyOwnLanguageCoCoChecker checker = new MyOwnLanguageCoCoChecker();
    checker.addCoCo(new PlusExpressionReturnsInt());
    return checker;
  }
}
