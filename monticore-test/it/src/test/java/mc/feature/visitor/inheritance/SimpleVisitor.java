/* (c) https://github.com/MontiCore/monticore */
package mc.feature.visitor.inheritance;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.a._visitor.AVisitor2;
import mc.feature.visitor.inheritance.b._visitor.BVisitor2;
import mc.feature.visitor.inheritance.c._visitor.CVisitor2;

public class SimpleVisitor implements AVisitor2, BVisitor2, CVisitor2 {
  StringBuilder run = new StringBuilder();
  
  public void clear() {
    run.setLength(0);
  }
  
  public String getRun() {
    return run.toString();
  }
  
  @Override
  public void visit(ASTXA node) {
    run.append("A");
  }
  
  @Override
  public void visit(ASTXB node) {
    run.append("B");
  }
  
  @Override
  public void visit(ASTXC node) {
    run.append("C");
  }
  
  public void endVisit(de.monticore.ast.ASTNode node) {
  }
  
  public void visit(de.monticore.ast.ASTNode node) {
  }
  
  public void endVisit(de.monticore.symboltable.IScope node) {
  }
  
  public void visit(de.monticore.symboltable.IScope node) {
  }
  
  public void endVisit(de.monticore.symboltable.ISymbol node) {
  }
  
  public void visit(de.monticore.symboltable.ISymbol node) {
  }
  
  
}
