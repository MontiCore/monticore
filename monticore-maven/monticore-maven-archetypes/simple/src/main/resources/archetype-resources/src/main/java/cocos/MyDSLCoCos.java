/* (c) https://github.com/MontiCore/monticore */

package ${package}.cocos;

import ${package}.mydsl._cocos.MyDSLCoCoChecker;

public class MyDSLCoCos {
  
  public MyDSLCoCoChecker getCheckerForAllCoCos() {
    final MyDSLCoCoChecker checker = new MyDSLCoCoChecker();
    checker.addCoCo(new AtLeastOneMyField());
    checker.addCoCo(new MyElementNameStartsWithCapitalLetter());
    checker.addCoCo(new ExistingMyFieldType());
    return checker;
  }
}
