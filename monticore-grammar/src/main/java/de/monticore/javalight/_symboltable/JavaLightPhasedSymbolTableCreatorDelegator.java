// (c) https://github.com/MontiCore/monticore
package de.monticore.javalight._symboltable;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._ast.ASTClassBodyDeclaration;
import de.monticore.javalight._visitor.JavaLightTraverser;

import java.util.ArrayList;
import java.util.List;

public class JavaLightPhasedSymbolTableCreatorDelegator {

  protected  IJavaLightGlobalScope globalScope ;

  protected  JavaLightScopesGenitorDelegator scopesGenitorDelegator ;

  protected List<JavaLightTraverser> priorityList ;


  public  JavaLightPhasedSymbolTableCreatorDelegator(IJavaLightGlobalScope globalScope)  {
    this.globalScope = globalScope;
    this.scopesGenitorDelegator = new JavaLightScopesGenitorDelegator(globalScope);
    this.priorityList = new ArrayList<>();
    priorityList.add(new JavaLightSTCompleteTypesDelegator().getTraverser());
  }

  public  JavaLightPhasedSymbolTableCreatorDelegator()  {
    this(JavaLightMill.globalScope());
  }

  public  IJavaLightArtifactScope createFromAST (ASTClassBodyDeclaration rootNode)  {
    IJavaLightArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
