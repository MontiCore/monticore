package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import java.util.Deque;

public class TestSymTabMillScopesGenitor extends TestSymTabMillScopesGenitorTOP {

  public TestSymTabMillScopesGenitor(Deque<? extends ITestSymTabMillScope> scopeStack){
    super(scopeStack);
  }

  public TestSymTabMillScopesGenitor(){
    super();
  }
}
