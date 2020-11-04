package de.monticore.javalight._symboltable;

import de.monticore.javalight._visitor.JavaLightDelegatorVisitor;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;

public class JavaLightSTCompleteTypesDelegator extends JavaLightDelegatorVisitor {

  public JavaLightSTCompleteTypesDelegator(){
    setRealThis(this);

    setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsSTCompleteTypes());
    setMCCommonStatementsVisitor(new MCCommonStatementsSTCompleteTypes());
    setJavaLightVisitor(new JavaLightSTCompleteTypes());
  }

}
