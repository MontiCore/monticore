/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._symboltable.JavaLightSTCompleteTypes;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._visitor.TestJavaLightTraverser;

public class JavaLightSTCompleteTypesDelegator {

  protected TestJavaLightTraverser traverser;

  public JavaLightSTCompleteTypesDelegator(){
    this.traverser = TestJavaLightMill.traverser();

    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traverser.add4JavaLight(new JavaLightSTCompleteTypes());
  }

  public TestJavaLightTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(TestJavaLightTraverser traverser) {
    this.traverser = traverser;
  }
}
