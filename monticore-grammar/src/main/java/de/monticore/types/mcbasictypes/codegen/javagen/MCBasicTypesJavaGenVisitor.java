/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesInheritanceHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

/**
 * This Visitor generates Java code for _any_ MCType,
 * not just for MCBasicTypes.
 * Thus, no further Java Generators are needed for MCTypes.
 */
public class MCBasicTypesJavaGenVisitor extends MCBasicTypesInheritanceHandler implements MCBasicTypesVisitor2 {

  protected JavaGenVisitorState state;
  protected ASTMCType rootMCBasicType;

  public MCBasicTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
    rootMCBasicType = null;
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void visit(ASTMCType node) {
    if (rootMCBasicType == null) {
      rootMCBasicType = node;
    }
  }

  @Override
  public void endVisit(ASTMCType node) {
    if (rootMCBasicType == node) {
      this.getPrinter().print(
          SymTypeExpression2JavaConverter.getJavaTypePrint(
              SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node)))
      );
    }
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
