/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.Names;

import java.util.Iterator;

public class MCBasicTypesPrettyPrinterConcreteVisitor implements MCBasicTypesVisitor {

  private MCBasicTypesVisitor realThis = this;

  public IndentPrinter getPrinter() {
    return printer;
  }

  MCBasicTypesPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    this.printer = printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  // printer to use
  protected IndentPrinter printer = null;


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
  //@Override
  public void visit(ASTSimpleReferenceType a) {
    // print qualified name
    getPrinter().print(Names.getQualifiedName(a.getNameList()));
    // optional type arguments are printed automatically by visitor concept
  }

  @Override
  public void visit(ASTImportStatement a){
    getPrinter().print("import" + a.getQualifiedName().getQName());
  }
 /*
  @Override
  public void visit(ASTBooleanRereferenceType a){
    getPrinter().print("Boolean");
  }

  @Override
  public void visit(ASTStringRereferenceType a){
    getPrinter().print("String");
  }

  @Override
  public void visit(ASTByteRereferenceType a){
    getPrinter().print("Byte");
  }

  @Override
  public void visit(ASTCharRereferenceType a){
    getPrinter().print("Char");
  }

  @Override
  public void visit(ASTShortRereferenceType a){
    getPrinter().print("Short");
  }

  @Override
  public void visit(ASTIntegerRereferenceType a){
    getPrinter().print("Integer");
  }

  @Override
  public void visit(ASTFloatRereferenceType a){
    getPrinter().print("Float");
  }

  @Override
  public void visit(ASTLongRereferenceType a){
    getPrinter().print("Long");
  }

  @Override
  public void visit(ASTDoubleRereferenceType a){
    getPrinter().print("Double");
  }
 */
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
