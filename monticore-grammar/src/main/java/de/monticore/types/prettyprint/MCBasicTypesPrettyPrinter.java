/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.Names;

import java.util.Iterator;

public class MCBasicTypesPrettyPrinter implements MCBasicTypesVisitor2, MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCBasicTypesTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public MCBasicTypesPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  // printer to use
  protected IndentPrinter printer;


  /**
   * Prints qualified names
   *
   * @param a qualified name
   */
  @Override
  public void handle(ASTMCQualifiedName a) {
    getPrinter().print(Names.getQualifiedName(a.getPartsList()));
  }


  /**
   * Prints a void type.
   *
   * @param a void type
   */
  @Override
  public void handle(ASTMCVoidType a) {
    getPrinter().print("void");
  }

  /**
   * Prints a primitive type.
   *
   * @param a primitive type
   */
  @Override
  public void handle(ASTMCPrimitiveType a) {
    getPrinter().print(MCBasicTypesHelper.primitiveConst2Name(a.getPrimitive()));
  }

  @Override
  public void handle(ASTMCImportStatement a){
    getPrinter().print("import " + a.getMCQualifiedName().toString());
    if(a.isStar()){
      getPrinter().print(".*");
    }
    getPrinter().print(";");
  }

  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param separator string for separating list
   */
  protected void printList(Iterator<? extends ASTMCBasicTypesNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }

}
