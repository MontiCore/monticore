package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import java.util.Deque;

public class TestSymTabMillScopeSkeletonCreator extends TestSymTabMillScopeSkeletonCreatorTOP {

  public TestSymTabMillScopeSkeletonCreator(Deque<? extends ITestSymTabMillScope> scopeStack){
    super(scopeStack);
  }

  public TestSymTabMillScopeSkeletonCreator(){
    super();
  }
}
