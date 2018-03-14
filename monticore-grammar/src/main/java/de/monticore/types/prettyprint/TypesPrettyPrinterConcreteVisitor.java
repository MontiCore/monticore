/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.prettyprint;

import java.util.Iterator;

import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTComplexArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveArrayType;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTTypesNode;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.ASTWildcardType;
import de.monticore.types.types._visitor.TypesVisitor;
import de.se_rwth.commons.Names;

/**
 * This class is responsible for pretty-printing types of the common type system. It is implemented
 * using the Visitor pattern. The Visitor pattern traverses a tree in depth first, the visit and
 * ownVisit-methods are called when a node is traversed, the endVisit methods are called when the
 * whole subtree of a node has been traversed. The ownVisit-Methods stop the automatic traversal
 * order and allow to explictly visit subtrees by calling getVisitor().startVisit(ASTNode)
 * 
 * @author Martin Schindler
 */
public class TypesPrettyPrinterConcreteVisitor extends LiteralsPrettyPrinterConcreteVisitor implements TypesVisitor {
  
  private TypesVisitor realThis = this;
  
   /**
   * Constructor.
   * 
   * @param parent the parent pretty printer, needed to give control to the embedded pretty printer
   * when embedding is detected.
   * @param printer the printer to write to.
   */
  public TypesPrettyPrinterConcreteVisitor(IndentPrinter printer) {
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
   * Prints an array of a complex array type
   * 
   * @param a array type
   */
  @Override
  public void handle(ASTComplexArrayType a) {
    // print primitive type
    a.getComponentType().accept(getRealThis());
    // print dimension
    for (int i = 0; i < a.getDimensions(); i++) {
      getPrinter().print("[]");
    }
  }
  
  /**
   * Prints an array of a primitive array type
   * 
   * @param a array type
   */
  @Override
  public void handle(ASTPrimitiveArrayType a) {
    // print primitive type
    a.getComponentType().accept(getRealThis());
    // print dimension
    for (int i = 0; i < a.getDimensions(); i++) {
      getPrinter().print("[]");
    }
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
      case ASTConstantsTypes.BOOLEAN: 
        getPrinter().print("boolean");
        break;
      case ASTConstantsTypes.BYTE:
        getPrinter().print("byte");
        break;
      case ASTConstantsTypes.CHAR:
        getPrinter().print("char");
        break;
      case ASTConstantsTypes.SHORT:
        getPrinter().print("short");
        break;
      case ASTConstantsTypes.INT:
        getPrinter().print("int");
        break;
      case ASTConstantsTypes.FLOAT:
        getPrinter().print("float");
        break;
      case ASTConstantsTypes.LONG: 
        getPrinter().print("long");
        break;
      case ASTConstantsTypes.DOUBLE: 
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
   * Prints a complex reference type
   * 
   * @param a complex reference type
   */
  @Override
  public void handle(ASTComplexReferenceType a) {
    printList(a.getSimpleReferenceTypeList().iterator(), ".");
  }
  
  /**
   * Prints type arguments (Generics)
   * 
   * @param a type arguments
   */
  @Override
  public void handle(ASTTypeArguments a) {
    getPrinter().print("<");
    printList(a.getTypeArgumentList().iterator(), ", ");
    getPrinter().print(">");
  }
  
  /**
   * Prints a wildcard type of a type argument (Generics)
   * 
   * @param a wildcard type
   */
  @Override
  public void handle(ASTWildcardType a) {
    getPrinter().print("?");
    if (a.isPresentUpperBound()) {
      getPrinter().print(" extends ");
      a.getUpperBound().accept(getRealThis());
    }
    else if (a.isPresentLowerBound()) {
      getPrinter().print(" super ");
      a.getLowerBound().accept(getRealThis());
    }
  }
  
  /**
   * Prints type parameters (Generics)
   * 
   * @param a type parameters
   */
  @Override
  public void handle(ASTTypeParameters a) {
    if (!a.getTypeVariableDeclarationList().isEmpty()) {
      getPrinter().print("<");
      printList(a.getTypeVariableDeclarationList().iterator(), ", ");
      getPrinter().print(">");
    }
  }
  
  /**
   * Prints a type variable declaration (Generics)
   * 
   * @param a type variable declaration
   */
  @Override
  public void handle(ASTTypeVariableDeclaration a) {
    getPrinter().print(a.getName());
    if (a.getUpperBoundList() != null && !a.getUpperBoundList().isEmpty()) {
      getPrinter().print(" extends ");
      printList(a.getUpperBoundList().iterator(), "& ");
    }
  }
    
  /**
   * Prints a list
   * 
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printList(Iterator<? extends ASTTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }
  
  /**
   * This method prettyprints a given node from type grammar.
   * 
   * @param a A node from type grammar.
   * @return String representation.
   */
  public String prettyprint(ASTTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  /**
   * @see de.monticore.types.types._visitor.TypesVisitor#setRealThis(de.monticore.types.types._visitor.TypesVisitor)
   */
  @Override
  public void setRealThis(TypesVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.types.types._visitor.TypesVisitor#getRealThis()
   */
  @Override
  public TypesVisitor getRealThis() {
    return realThis;
  }
 
  
}
