/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regextype.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.regex.regextype._ast.ASTRegExType;
import de.monticore.regex.regextype._visitor.RegExTypeInheritanceHandler;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;

public class RegExTypeJavaGenVisitor extends RegExTypeInheritanceHandler {

  protected JavaGenVisitorState state;

  public RegExTypeJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTRegExType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(SymTypeRelations.normalize(TypeCheck3.symTypeFromAST(node))));
  }
}
