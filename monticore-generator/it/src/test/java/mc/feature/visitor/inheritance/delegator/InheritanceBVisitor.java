/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._ast.ASTYB;
import mc.feature.visitor.inheritance.b._ast.ASTZB;
import mc.feature.visitor.inheritance.b._visitor.BInheritanceVisitor;
import mc.feature.visitor.inheritance.b._visitor.BVisitor;
import de.monticore.ast.ASTNode;

public class InheritanceBVisitor implements BInheritanceVisitor {
  final private StringBuilder run;
  
  public InheritanceBVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void visit(ASTNode node) {
    run.append("InheritanceBVisitor.vASTNode");
  }
  
  @Override
  public void endVisit(ASTNode node) {
    run.append("InheritanceBVisitor.eASTNode");
  }
  
  @Override
  public void handle(ASTXA node) {
    run.append("InheritanceBVisitor.hXA");
    BInheritanceVisitor.super.handle(node);
  }
  
  @Override
  public void traverse(ASTXA node) {
    run.append("InheritanceBVisitor.tXA");
    BInheritanceVisitor.super.traverse(node);
  }
  
  @Override
  public void visit(ASTXA node) {
    run.append("InheritanceBVisitor.vXA");
  }
  
  @Override
  public void endVisit(ASTXA node) {
    run.append("InheritanceBVisitor.eXA");
  }
  
  @Override
  public void handle(ASTXB node) {
    run.append("InheritanceBVisitor.hXB");
    BInheritanceVisitor.super.handle(node);
  }
  
  @Override
  public void traverse(ASTXB node) {
    run.append("InheritanceBVisitor.tXB");
    BInheritanceVisitor.super.traverse(node);
  }
  
  @Override
  public void visit(ASTXB node) {
    run.append("InheritanceBVisitor.vXB");
  }
  
  @Override
  public void endVisit(ASTXB node) {
    run.append("InheritanceBVisitor.eXB");
  }
  
  @Override
  public void handle(ASTYB node) {
    run.append("InheritanceBVisitor.hYB");
    BInheritanceVisitor.super.handle(node);
  }
  
  @Override
  public void traverse(ASTYB node) {
    run.append("InheritanceBVisitor.tYB");
    BInheritanceVisitor.super.traverse(node);
  }
  
  @Override
  public void visit(ASTYB node) {
    run.append("InheritanceBVisitor.vYB");
  }
  
  @Override
  public void endVisit(ASTYB node) {
    run.append("InheritanceBVisitor.eYB");
  }
  
  @Override
  public void handle(ASTZB node) {
    run.append("InheritanceBVisitor.hZB");
    BInheritanceVisitor.super.handle(node);
  }
  
  @Override
  public void traverse(ASTZB node) {
    run.append("InheritanceBVisitor.tZB");
    BInheritanceVisitor.super.traverse(node);
  }
  
  @Override
  public void visit(ASTZB node) {
    run.append("InheritanceBVisitor.vZB");
  }
  
  @Override
  public void endVisit(ASTZB node) {
    run.append("InheritanceBVisitor.eZB");
  }
  
  // realthis pattern
  private BVisitor realThis;
  
  @Override
  public void setRealThis(BVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public BVisitor getRealThis() {
    return realThis;
  }
  
}
