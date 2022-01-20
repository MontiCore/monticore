/* (c) https://github.com/MontiCore/monticore */
package foo._symboltable;

import foo.FooMill;
import foo._ast.ASTFooArtifact;

public class FooScopesGenitor extends FooScopesGenitorTOP {

  @Override
  public IFooArtifactScope createFromAST(ASTFooArtifact rootNode) {
    IFooArtifactScope artifactScope = FooMill.artifactScope();
    artifactScope.setEnclosingScope(FooMill.globalScope());
    artifactScope.setName(rootNode.getNest().getName());

    putOnStack(artifactScope);
    initArtifactScopeHP1(artifactScope);
    rootNode.accept(getTraverser());
    initArtifactScopeHP2(artifactScope);

    return artifactScope;
  }

}
