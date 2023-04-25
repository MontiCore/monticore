/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionParTypes;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesHandler;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesTraverser;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesVisitor2;

@Deprecated(forRemoval = true)
public class MCFunctionTypesPrettyPrinter
    implements MCFunctionTypesVisitor2, MCFunctionTypesHandler {

  protected MCFunctionTypesTraverser traverser;

  protected IndentPrinter printer;

  public MCFunctionTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCFunctionTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCFunctionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  public void handle(ASTMCFunctionParTypes node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    for (int i = 0; i < node.getMCTypeList().size(); i++) {
      node.getMCType(i).accept(getTraverser());
      if (i < node.getMCTypeList().size() - 1) {
        getPrinter().print(", ");
      }
      else if (node.isPresentIsElliptic()) {
        getPrinter().print("...");
      }
    }
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public void handle(ASTMCFunctionType node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getMCFunctionParTypes().accept(getTraverser());
    getPrinter().print(" -> ");
    node.getMCReturnType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

}
