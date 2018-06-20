/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.prettyprint;

import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;

import de.se_rwth.commons.Names;

import java.util.Iterator;

/**
 * This class is responsible for pretty-printing types of the common type system. It is implemented
 * using the Visitor pattern. The Visitor pattern traverses a tree in depth first, the visit and
 * ownVisit-methods are called when a node is traversed, the endVisit methods are called when the
 * whole subtree of a node has been traversed. The ownVisit-Methods stop the automatic traversal
 * order and allow to explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 * 
 * @author Martin Schindler
 */
public class MCBasicTypesPrettyPrinterConcreteVisitor extends LiteralsPrettyPrinterConcreteVisitor implements MCBasicTypesVisitor {

  private MCBasicTypesVisitor realThis = this;

   /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public MCBasicTypesPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    super(printer);
  }

  /**
   * Prints qualified names
   *
   * @param a qualified name
   */
  @Override
  public void visit(ASTQualifiedName a) {
    getPrinter().print(Names.getQualifiedName(a.getPartList()));
  }





  /**
   * Prints a void type.
   *
   * @param a void type
   */
  @Override
  public void visit(ASTVoidType a) {
    getPrinter().print("void");
  }

  /**
   * Prints a primitive type.
   *
   * @param a primitive type
   */
  @Override
  public void visit(ASTPrimitiveType a) {
    switch (a.getPrimitive()) {
      case ASTConstantsMCBasicTypes.BOOLEAN:
        getPrinter().print("boolean");
        break;
      case ASTConstantsMCBasicTypes.BYTE:
        getPrinter().print("byte");
        break;
      case ASTConstantsMCBasicTypes.CHAR:
        getPrinter().print("char");
        break;
      case ASTConstantsMCBasicTypes.SHORT:
        getPrinter().print("short");
        break;
      case ASTConstantsMCBasicTypes.INT:
        getPrinter().print("int");
        break;
      case ASTConstantsMCBasicTypes.FLOAT:
        getPrinter().print("float");
        break;
      case ASTConstantsMCBasicTypes.LONG:
        getPrinter().print("long");
        break;
      case ASTConstantsMCBasicTypes.DOUBLE:
        getPrinter().print("double");
        break;
      default: getPrinter().print("");
    }
  }

  /**
   * Prints a simple reference type
   *
   * @param a simple reference type
   */
  @Override
  public void visit(ASTSimpleReferenceType a) {
    // print qualified name
    getPrinter().print(Names.getQualifiedName(a.getNameList()));
    // optional type arguments are printed automatically by visitor concept
  }




  /**
   * This method prettyprints a given node from type grammar.
   *
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  /**
   * @see MCBasicTypesVisitor#setRealThis(MCBasicTypesVisitor)
   */
  @Override
  public void setRealThis(MCBasicTypesVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see MCBasicTypesVisitor#getRealThis()
   */
  @Override
  public MCBasicTypesVisitor getRealThis() {
    return realThis;
  }

  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTMCBasicTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }
}
