/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcarraytypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesInheritanceHandler;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

public class MCArrayTypesJavaGenVisitor extends MCArrayTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCArrayTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCArrayType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }
}
