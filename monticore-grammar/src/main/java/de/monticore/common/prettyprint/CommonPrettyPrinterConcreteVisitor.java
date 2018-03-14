/* (c) https://github.com/MontiCore/monticore */

package de.monticore.common.prettyprint;

import de.monticore.common.common._ast.ASTCardinality;
import de.monticore.common.common._ast.ASTCompleteness;
import de.monticore.common.common._ast.ASTModifier;
import de.monticore.common.common._ast.ASTStereoValue;
import de.monticore.common.common._ast.ASTStereotype;
import de.monticore.common.common._visitor.CommonVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

public class CommonPrettyPrinterConcreteVisitor extends TypesPrettyPrinterConcreteVisitor implements CommonVisitor {
  
  private CommonVisitor realThis = this;
  
  /**
   * Constructor.
   * 
   * @param parent the parent pretty printer, needed to give control to the
   *          embedded pretty printer when embedding is detected.
   * @param printer the printer to write to.
   */
  public CommonPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    super(printer);
  }
    
  /**
   * Prints the start of stereotypes
   * 
   * @param a stereotype
   */
  @Override
  public void handle(ASTStereotype a) {
    getPrinter().print("<<");
    String sep = "";
    for (ASTStereoValue value: a.getValueList()) {
      getPrinter().print(sep);
      value.accept(getRealThis());
      sep = ", ";
    }
    getPrinter().print(">>");
  }
    
  /**
   * Prints stereotype values
   * 
   * @param a stereotype value
   */
  @Override
  public void handle(ASTStereoValue a) {
    getPrinter().print(a.getName());
    if (a.isPresentSource()) {
      getPrinter().print(" = " + a.getSource());
    }
  }
  
  /**
   * Prints cardinalities
   * 
   * @param a cardinality
   */
  @Override
  public void handle(ASTCardinality a) {
    getPrinter().print("[");
    if (a.isMany()) {
      getPrinter().print("*");
    }
    else {
      getPrinter().print(a.getLowerBound());
      if (a.getLowerBound() != a.getUpperBound() || a.isNoUpperLimit()) {
        getPrinter().print("..");
        if (a.isNoUpperLimit()) {
          getPrinter().print("*");
        }
        else {
          getPrinter().print(a.getUpperBound());
        }
      }
    }
    getPrinter().print("]");
  }
  
  /**
   * Prints info about the completeness
   * 
   * @param a completeness
   */
  @Override
  public void handle(ASTCompleteness a) {
    if (a.isComplete()) {
      printer.print("(c) ");
    }
    else if (a.isIncomplete()) {
      printer.print("(...) ");
    }
    else if (a.isLeftComplete()) {
      printer.print("(c,...) ");
    }
    else if (a.isRightComplete()) {
      printer.print("(...,c) ");
    }
  }
  
  /**
   * Prints modifiers
   * 
   * @param a modifier
   */
  @Override
  public void handle(ASTModifier a) {
    // print stereotypes
    if (a.isPresentStereotype()) {
      a.getStereotype().accept(getRealThis());
      getPrinter().print(" ");
    }
    if (a.isPublic()) {
      getPrinter().print("public ");
    }
    if (a.isPrivate()) {
      getPrinter().print("private ");
    }
    if (a.isProtected()) {
      getPrinter().print("protected ");
    }
    if (a.isFinal()) {
      getPrinter().print("final ");
    }
    if (a.isAbstract()) {
      getPrinter().print("abstract ");
    }
    if (a.isLocal()) {
      getPrinter().print("local ");
    }
    if (a.isDerived()) {
      getPrinter().print("derived ");
    }
    if (a.isReadonly()) {
      getPrinter().print("readonly ");
    }
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
  }

  /**
   * @see de.monticore.common.common._visitor.CommonVisitor#setRealThis(de.monticore.common.common._visitor.CommonVisitor)
   */
  @Override
  public void setRealThis(CommonVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.common.common._visitor.CommonVisitor#getRealThis()
   */
  @Override
  public CommonVisitor getRealThis() {
    return realThis;
  }
    
}
