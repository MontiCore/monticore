/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.siunitliterals.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunit.siunitliterals._visitor.SIUnitLiteralsInheritanceHandler;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * SIUnits are not represented at runtime, e.g., 5km -> 5.0
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
    // integral literals are of type double, e.g., 5km -> ((double) 5)
    printConverted(getPrinter(),
        getSIUnitValueType(typeOf(node)),
        typeOf(node.getNumericLiteral()),
        p -> node.getNumericLiteral().accept(getTraverser())
    );
  }

}
