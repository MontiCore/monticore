package mc.testcd4analysis._symboltable;

import java.util.ArrayDeque;
import java.util.Deque;

public class TestCD4AnalysisPhasedSymbolTableCreatorDelegator extends TestCD4AnalysisPhasedSymbolTableCreatorDelegatorTOP {

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(ITestCD4AnalysisGlobalScope globalScope){
    super(globalScope);
    Deque<ITestCD4AnalysisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(globalScope);
    this.priorityList.add(new TestCD4AnalysisSTCompleteTypes(scopeStack));
  }

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(){
    super();
    Deque<ITestCD4AnalysisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(globalScope);
    this.priorityList.add(new TestCD4AnalysisSTCompleteTypes(scopeStack));
  }

}
