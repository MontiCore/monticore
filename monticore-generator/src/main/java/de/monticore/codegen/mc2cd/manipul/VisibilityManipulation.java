/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

import java.util.function.UnaryOperator;

/**
 * Sets the visibility of classes and interfaces to public, and attributes to
 * protected.
 *
 */
final class VisibilityManipulation implements UnaryOperator<ASTCDCompilationUnit> {

  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDBasis(new VisibilityVisitor());
    cdCompilationUnit.accept(traverser);
    return cdCompilationUnit;
  }

  private class VisibilityVisitor implements CDBasisVisitor2 {
    /**
     * Sets the visibility of every attribute to protected.
     */
    @Override
    public void visit(ASTCDAttribute cdAttribute) {
      cdAttribute.getModifier().setProtected(true);
    }
  }

  /**
   * Sets the visibility of every class to public.
   */
  public void visit(ASTCDClass cdClass) {
    cdClass.getModifier().setPublic(true);
  }

  /**
   * Sets the visibility of every interface to public.
   */
  public void visit(ASTCDInterface cdInterface) {
    cdInterface.getModifier().setPublic(true);
  }

}
