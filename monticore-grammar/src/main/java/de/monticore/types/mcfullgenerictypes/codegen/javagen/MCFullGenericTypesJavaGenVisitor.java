/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class MCFullGenericTypesJavaGenVisitor extends MCFullGenericTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCFullGenericTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCMultipleGenericType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
