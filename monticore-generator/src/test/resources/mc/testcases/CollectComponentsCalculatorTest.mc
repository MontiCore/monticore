/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.statechart;

grammar CollectComponentsCalculatorTest { 

  interface IFoo;

  Bar = "bar";
  
  Foo implements (Bar ";")=>IFoo =
    Bar:Bar ";"
  ;
  
 }
