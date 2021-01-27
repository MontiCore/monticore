/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4analysis._visitor.CD4AnalysisVisitor;
import de.monticore.codegen.mc2cd.TransformationHelper;

import java.util.function.UnaryOperator;

/**
 * Ensures that attributes are spelled with lower case in order to comply with the standard Java
 * convention.
 *
 */
public class JavaAndCdConformNameManipulation implements UnaryOperator<ASTCDCompilationUnit>, CD4AnalysisVisitor {

  CD4AnalysisVisitor realThis = this;

  @Override
  public CD4AnalysisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(CD4AnalysisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    cdCompilationUnit.accept(getRealThis());
    return cdCompilationUnit;
  }

  @Override
  public void visit(ASTCDAttribute node) {
    node.setName(TransformationHelper.getJavaAndCdConformName(node.getName()));
  }
}
