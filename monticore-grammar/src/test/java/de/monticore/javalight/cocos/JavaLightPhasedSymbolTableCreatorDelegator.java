/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTJavaMethod;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._symboltable.ITestJavaLightArtifactScope;
import de.monticore.testjavalight._symboltable.ITestJavaLightGlobalScope;
import de.monticore.testjavalight._symboltable.TestJavaLightScopesGenitorDelegator;

import java.util.ArrayList;
import java.util.List;

public class JavaLightPhasedSymbolTableCreatorDelegator {

  protected ITestJavaLightGlobalScope globalScope ;

  protected TestJavaLightScopesGenitorDelegator scopesGenitorDelegator ;

  protected List<JavaLightTraverser> priorityList ;

  public JavaLightPhasedSymbolTableCreatorDelegator()  {
    this.globalScope = TestJavaLightMill.globalScope();
    this.scopesGenitorDelegator = TestJavaLightMill.scopesGenitorDelegator();
    this.priorityList = new ArrayList<>();
    priorityList.add(new JavaLightSTCompleteTypesDelegator().getTraverser());
  }

  public ITestJavaLightArtifactScope createFromAST (ASTJavaMethod rootNode)  {
    ITestJavaLightArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
    this.priorityList.forEach(rootNode::accept);
    return as;
  }

}
