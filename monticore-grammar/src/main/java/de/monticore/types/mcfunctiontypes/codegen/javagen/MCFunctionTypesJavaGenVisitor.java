/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfunctiontypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCUnaryFunctionType;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class MCFunctionTypesJavaGenVisitor extends MCFunctionTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCFunctionTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCFunctionType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }

  @Override
  public void traverse(ASTMCUnaryFunctionType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
