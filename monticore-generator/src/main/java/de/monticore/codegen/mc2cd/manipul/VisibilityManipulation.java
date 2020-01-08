/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.utils.ASTNodes;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Sets the visibility of classes and interfaces to public, and attributes to
 * protected.
 * 
 */
final class VisibilityManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    for (ASTCDAttribute cdAttribute : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDAttribute.class)) {
      setProtectedVisibility(cdAttribute);
    }
    
    for (ASTCDClass cdClass : ASTNodes.getSuccessors(cdCompilationUnit, ASTCDClass.class)) {
      setClassVisibilityPublic(cdClass);
    }
    
    for (ASTCDInterface cdInterface : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDInterface.class)) {
      setInterfaceVisibilityPublic(cdInterface);
    }
    
    return cdCompilationUnit;
  }
  
  /**
   * Sets the visibility of every attribute to protected.
   */
  private void setProtectedVisibility(ASTCDAttribute cdAttribute) {
    ASTModifier newModifier = cdAttribute.isPresentModifier()
        ? cdAttribute.getModifier()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setProtected(true);
    cdAttribute.setModifier(newModifier);
  }
  
  /**
   * Sets the visibility of every class to public.
   */
  private void setClassVisibilityPublic(ASTCDClass cdClass) {
    ASTModifier newModifier = cdClass.isPresentModifier()
        ? cdClass.getModifier()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setPublic(true);
    cdClass.setModifier(newModifier);
  }
  
  /**
   * Sets the visibility of every interface to public.
   */
  private void setInterfaceVisibilityPublic(ASTCDInterface cdInterface) {
    ASTModifier newModifier = cdInterface.isPresentModifier()
        ? cdInterface.getModifier()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setPublic(true);
    cdInterface.setModifier(newModifier);
  }
  
}
