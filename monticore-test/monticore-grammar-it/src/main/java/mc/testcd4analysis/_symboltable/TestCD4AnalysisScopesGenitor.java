/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import de.se_rwth.commons.Names;
import mc.testcd4analysis._ast.*;

import java.util.ArrayList;
import java.util.Optional;

public class TestCD4AnalysisScopesGenitor extends TestCD4AnalysisScopesGenitorTOP {

  public TestCD4AnalysisScopesGenitor(){
    super();
  }

  public TestCD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    TestCD4AnalysisArtifactScope artifactScope = new TestCD4AnalysisArtifactScope(Optional.empty(),
        Names.getQualifiedName(rootNode.getPackageList()), new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }

}
