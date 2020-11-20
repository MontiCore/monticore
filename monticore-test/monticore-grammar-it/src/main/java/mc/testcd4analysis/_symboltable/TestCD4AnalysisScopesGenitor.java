package mc.testcd4analysis._symboltable;

import de.se_rwth.commons.Names;
import mc.testcd4analysis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;

public class TestCD4AnalysisScopesGenitor extends TestCD4AnalysisScopesGenitorTOP {

  public TestCD4AnalysisScopesGenitor(ITestCD4AnalysisScope enclosingScope){
    super(enclosingScope);
  }

  public TestCD4AnalysisScopesGenitor(Deque<? extends ITestCD4AnalysisScope> scopeStack){
    super(scopeStack);
  }

  public TestCD4AnalysisScopesGenitor(){
    super();
  }

  @Override
  public TestCD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    TestCD4AnalysisArtifactScope artifactScope = new TestCD4AnalysisArtifactScope(Optional.empty(),
        Names.getQualifiedName(rootNode.getPackageList()), new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

}
