/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;

/**
 * Sets the visibility of classes and interfaces to public, and attributes to
 * protected.
 * 
 * @author Sebastian Oberhoff
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
    Optional<ASTModifier> previousModifier = cdAttribute.getModifierOpt();
    ASTModifier newModifier = previousModifier.isPresent()
        ? previousModifier.get()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setProtected(true);
    cdAttribute.setModifier(newModifier);
  }
  
  /**
   * Sets the visibility of every class to public.
   */
  private void setClassVisibilityPublic(ASTCDClass cdClass) {
    Optional<ASTModifier> previousModifier = cdClass.getModifierOpt();
    ASTModifier newModifier = previousModifier.isPresent()
        ? previousModifier.get()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setPublic(true);
    cdClass.setModifier(newModifier);
  }
  
  /**
   * Sets the visibility of every interface to public.
   */
  private void setInterfaceVisibilityPublic(ASTCDInterface cdInterface) {
    Optional<ASTModifier> previousModifier = cdInterface.getModifierOpt();
    ASTModifier newModifier = previousModifier.isPresent()
        ? previousModifier.get()
        : CD4AnalysisNodeFactory
            .createASTModifier();
    newModifier.setPublic(true);
    cdInterface.setModifier(newModifier);
  }
  
}
