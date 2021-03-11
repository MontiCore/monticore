/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.codegen.mc2cd.TransformationHelper;

import java.util.function.UnaryOperator;

/**
 * Ensures that attributes are spelled with lower case in order to comply with the standard Java
 * convention.
 *
 */
public class JavaAndCdConformNameManipulation implements UnaryOperator<ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    CD4AnalysisTraverser traverser = CD4AnalysisMill.traverser();
    traverser.add4CDBasis(new ManipulateVisitor());
    cdCompilationUnit.accept(traverser);
    return cdCompilationUnit;
  }

  private class ManipulateVisitor implements CDBasisVisitor2 {

    public CD4AnalysisTraverser getTraverser() {
      return traverser;
    }

    public void setTraverser(CD4AnalysisTraverser traverser) {
      this.traverser = traverser;
    }

    CD4AnalysisTraverser traverser;

    @Override
    public void visit(ASTCDAttribute node) {
      node.setName(TransformationHelper.getJavaAndCdConformName(node.getName()));
    }
  }
}
