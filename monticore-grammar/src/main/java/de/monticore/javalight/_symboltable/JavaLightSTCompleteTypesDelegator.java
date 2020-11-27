package de.monticore.javalight._symboltable;

import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._visitor.JavaLightDelegatorVisitor;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;

public class JavaLightSTCompleteTypesDelegator {

  protected JavaLightTraverser traverser;

  public JavaLightSTCompleteTypesDelegator(){
    this.traverser = JavaLightMill.traverser();

    traverser.addMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsSTCompleteTypes());
    traverser.addMCCommonStatementsVisitor(new MCCommonStatementsSTCompleteTypes());
    traverser.addJavaLightVisitor(new JavaLightSTCompleteTypes());
  }

  public JavaLightTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(JavaLightTraverser traverser) {
    this.traverser = traverser;
  }
}
