/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcstructuraltypes._ast.ASTMCBracketType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCIntersectionType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCTupleType;
import de.monticore.types.mcstructuraltypes._ast.ASTMCUnionType;
import de.monticore.types.mcstructuraltypes._visitor.MCStructuralTypesInheritanceHandler;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

public class MCStructuralTypesJavaGenVisitor extends MCStructuralTypesInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCStructuralTypesJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTMCTupleType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }

  @Override
  public void traverse(ASTMCUnionType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }

  @Override
  public void traverse(ASTMCIntersectionType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }

  @Override
  public void traverse(ASTMCBracketType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }
}
