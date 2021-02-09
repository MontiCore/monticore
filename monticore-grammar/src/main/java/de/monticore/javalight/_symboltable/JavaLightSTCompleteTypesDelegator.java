/* (c) https://github.com/MontiCore/monticore */
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

    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traverser.add4JavaLight(new JavaLightSTCompleteTypes());
  }

  public JavaLightTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(JavaLightTraverser traverser) {
    this.traverser = traverser;
  }
}
