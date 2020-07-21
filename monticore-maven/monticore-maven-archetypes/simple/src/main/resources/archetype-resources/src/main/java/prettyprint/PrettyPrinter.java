/* (c) https://github.com/MontiCore/monticore */

package ${package}.prettyprint;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._ast.ASTMyField;
import ${package}.mydsl._ast.ASTMyModel;
import ${package}.mydsl._visitor.MyDSLVisitor;

import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

/**
 * Pretty prints models. Use {@link #prettyprint(ASTMyModel)} (ASTMyModel)} to start a pretty printing
 */
public class PrettyPrinter implements MyDSLVisitor {

  private IndentPrinter printer;

  private MyDSLVisitor realThis;

  public PrettyPrinter() {
    this.printer = new IndentPrinter();
    this.realThis = this;
  }

  /**
   * Prints the model
   *
   * @param node
   */
  public String prettyprint(ASTMyModel node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }


  @Override
  public void visit(ASTMyModel node) {
    if (!node.isEmptyPackages()) {
      getPrinter().println("package " + Names.getQualifiedName(node.getPackageList()) + ";");
    }
    getPrinter().println("model " + node.getName() + " {");
    getPrinter().indent();
  }

  @Override
  public void endVisit(ASTMyModel node) {
    getPrinter().unindent();
    getPrinter().println("}");
  }

  @Override
  public void traverse(ASTMyModel node) {
    node.getMyElementsList().forEach(e -> e.accept(getRealThis()));
  }

  @Override
  public void visit(ASTMyElement node) {
    getPrinter().println("element " + node.getName() + " {");
    getPrinter().indent();
  }

  @Override
  public void endVisit(ASTMyElement node) {
    getPrinter().unindent();
    getPrinter().println("}");
  }

  @Override
  public void traverse(ASTMyElement node) {
    node.getMyFieldsList().forEach(e -> e.accept(getRealThis()));
  }

  @Override
  public void visit(ASTMyField node) {
    getPrinter().println(node.getName() + " " + node.getType() + ";");
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MyDSLVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MyDSLVisitor realThis) {
    this.realThis = realThis;
  }
}
