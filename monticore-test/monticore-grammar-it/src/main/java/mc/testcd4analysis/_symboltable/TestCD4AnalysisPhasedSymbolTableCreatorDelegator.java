package mc.testcd4analysis._symboltable;

import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._visitor.TestCD4AnalysisTraverser;

import java.util.ArrayDeque;
import java.util.Deque;

public class TestCD4AnalysisPhasedSymbolTableCreatorDelegator extends TestCD4AnalysisPhasedSymbolTableCreatorDelegatorTOP {

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(ITestCD4AnalysisGlobalScope globalScope){
    super(globalScope);
    Deque<ITestCD4AnalysisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(globalScope);
    TestCD4AnalysisTraverser traverser = TestCD4AnalysisMill.traverser();
    traverser.addTestCD4AnalysisVisitor(new TestCD4AnalysisSTCompleteTypes(scopeStack));
    this.priorityList.add(traverser);
  }

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(){
    super();
    Deque<ITestCD4AnalysisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(globalScope);
    TestCD4AnalysisTraverser traverser = TestCD4AnalysisMill.traverser();
    traverser.addTestCD4AnalysisVisitor(new TestCD4AnalysisSTCompleteTypes(scopeStack));
    this.priorityList.add(traverser);
  }

}
