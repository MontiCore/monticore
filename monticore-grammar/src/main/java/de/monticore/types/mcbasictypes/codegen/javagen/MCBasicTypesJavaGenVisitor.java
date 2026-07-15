/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class MCBasicTypesJavaGenVisitor extends MCBasicTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCBasicTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCQualifiedName node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)) + " ");
  }

  @Override
  public void traverse(ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)) + " ");
  }

  @Override
  public void traverse(ASTMCVoidType node) {
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)) + " ");
  }

  @Override
  public void traverse(ASTMCImportStatement node) {
    state._willBeRemoved_logUnimplemented(node);
  }

  @Override
  public void traverse(ASTMCPackageDeclaration node) {
    state._willBeRemoved_logUnimplemented(node);
  }
}
