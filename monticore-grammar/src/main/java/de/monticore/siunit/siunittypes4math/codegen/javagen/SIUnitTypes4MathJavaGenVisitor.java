/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.siunittypes4math.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.siunit.siunittypes4math._visitor.SIUnitTypes4MathInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class SIUnitTypes4MathJavaGenVisitor extends SIUnitTypes4MathInheritanceHandler {

  protected JavaGenVisitorState state;

  public SIUnitTypes4MathJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTSIUnitType node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
