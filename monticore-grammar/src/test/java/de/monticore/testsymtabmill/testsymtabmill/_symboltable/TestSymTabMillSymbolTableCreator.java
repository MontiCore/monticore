/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import java.util.Deque;

@Deprecated
public class TestSymTabMillSymbolTableCreator extends TestSymTabMillSymbolTableCreatorTOP {

  public TestSymTabMillSymbolTableCreator() {
  }

  public TestSymTabMillSymbolTableCreator(
      ITestSymTabMillScope enclosingScope) {
    super(enclosingScope);
  }

  public TestSymTabMillSymbolTableCreator(
      Deque<? extends ITestSymTabMillScope> scopeStack) {
    super(scopeStack);
  }
}
