package mc.testcd4analysis._symboltable;

import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._ast.ASTCDCompilationUnit;
import mc.testcd4analysis._visitor.TestCD4AnalysisTraverser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TestCD4AnalysisPhasedSymbolTableCreatorDelegator extends TestCD4AnalysisPhasedSymbolTableCreatorDelegatorTOP {

  protected ITestCD4AnalysisGlobalScope globalScope;

  protected TestCD4AnalysisScopesGenitorDelegator scopesGenitorDelegator;

  protected List<TestCD4AnalysisTraverser> priorityList;

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(ITestCD4AnalysisGlobalScope globalScope){
    this.globalScope = globalScope;
    this.scopesGenitorDelegator = new TestCD4AnalysisScopesGenitorDelegator(globalScope);
    this.priorityList = new ArrayList<>();
    Deque<ITestCD4AnalysisScope> scopeStack = new ArrayDeque<>();
    scopeStack.push(globalScope);
    TestCD4AnalysisTraverser traverser = TestCD4AnalysisMill.traverser();
    traverser.add4TestCD4Analysis(new TestCD4AnalysisSTCompleteTypes(scopeStack));
    this.priorityList.add(traverser);
  }

  public TestCD4AnalysisPhasedSymbolTableCreatorDelegator(){
    this(TestCD4AnalysisMill.globalScope());
  }

  public ITestCD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode){
    ITestCD4AnalysisArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
