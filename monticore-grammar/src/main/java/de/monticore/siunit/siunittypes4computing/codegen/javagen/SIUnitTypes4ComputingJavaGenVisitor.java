/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.siunittypes4computing.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunit.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunit.siunittypes4computing._visitor.SIUnitTypes4ComputingInheritanceHandler;
import de.monticore.types3.TypeCheck3;

public class SIUnitTypes4ComputingJavaGenVisitor extends SIUnitTypes4ComputingInheritanceHandler {

  protected JavaGenVisitorState state;

  public SIUnitTypes4ComputingJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  @Override
  public void traverse(ASTSIUnitType4Computing node) {
    Preconditions.checkNotNull(node);
    this.getPrinter().print(SymTypeExpression2JavaConverter.getJavaTypePrint(TypeCheck3.symTypeFromAST(node)));
  }
}
