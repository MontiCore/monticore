/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.siunitliterals.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunit.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunit.siunitliterals._visitor.SIUnitLiteralsInheritanceHandler;

/**
 * SIUnits are not represented at runtime, e.g., 5km -> 5
 */
public class SIUnitLiteralsJavaGenVisitor
    extends SIUnitLiteralsInheritanceHandler {

  protected JavaGenVisitorState state;

  public SIUnitLiteralsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTSIUnitLiteral node) {
    node.getNumericLiteral().accept(getTraverser());
  }

  @Override
  public void traverse(ASTSignedSIUnitLiteral node) {
    node.getSignedNumericLiteral().accept(getTraverser());
  }

}
