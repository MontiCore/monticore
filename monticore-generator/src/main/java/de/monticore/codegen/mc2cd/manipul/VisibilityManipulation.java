/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;

import java.util.function.UnaryOperator;

/**
 * Sets the visibility of classes and interfaces to public, and attributes to
 * protected.
 *
 */
final class VisibilityManipulation implements UnaryOperator<ASTCDCompilationUnit>, CD4AnalysisVisitor {

  CD4AnalysisVisitor realThis = this;

  @Override
  public CD4AnalysisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4AnalysisVisitor realThis) {
    this.realThis = realThis;
  }

  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    cdCompilationUnit.accept(getRealThis());
    return cdCompilationUnit;
  }

  /**
   * Sets the visibility of every attribute to protected.
   */
  public void visit(ASTCDAttribute cdAttribute) {
    ASTModifier newModifier = cdAttribute.isPresentModifier()
            ? cdAttribute.getModifier()
            : CD4AnalysisMill.modifierBuilder().build();
    newModifier.setProtected(true);
    cdAttribute.setModifier(newModifier);
  }

  /**
   * Sets the visibility of every class to public.
   */
  public void visit(ASTCDClass cdClass) {
    ASTModifier newModifier = cdClass.isPresentModifier()
            ? cdClass.getModifier()
            : CD4AnalysisMill.modifierBuilder().build();
    newModifier.setPublic(true);
    cdClass.setModifier(newModifier);
  }

  /**
   * Sets the visibility of every interface to public.
   */
  public void visit(ASTCDInterface cdInterface) {
    ASTModifier newModifier = cdInterface.isPresentModifier()
            ? cdInterface.getModifier()
            : CD4AnalysisMill.modifierBuilder().build();
    newModifier.setPublic(true);
    cdInterface.setModifier(newModifier);
  }

}
