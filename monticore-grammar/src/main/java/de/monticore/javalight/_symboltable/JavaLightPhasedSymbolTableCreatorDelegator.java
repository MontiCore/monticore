/* (c) https://github.com/MontiCore/monticore */
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

  public  JavaLightPhasedSymbolTableCreatorDelegator()  {
    this.globalScope = JavaLightMill.globalScope();
    this.scopesGenitorDelegator = JavaLightMill.scopesGenitorDelegator();
    this.priorityList = new ArrayList<>();
    priorityList.add(new JavaLightSTCompleteTypesDelegator().getTraverser());
  }

  public  IJavaLightArtifactScope createFromAST (ASTClassBodyDeclaration rootNode)  {
    IJavaLightArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
