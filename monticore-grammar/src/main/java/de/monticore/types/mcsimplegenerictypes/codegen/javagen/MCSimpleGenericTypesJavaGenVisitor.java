/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesInheritanceHandler;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

public class MCSimpleGenericTypesJavaGenVisitor extends MCSimpleGenericTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCSimpleGenericTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCBasicGenericType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }
}
