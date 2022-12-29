/* (c) https://github.com/MontiCore/monticore */
package automata.visitor;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.visitor.IVisitor;

public class VisitedElementsCounter implements IVisitor {

  int count = 0;

  public int getCount() {
    return count;
  }

  @Override
  public void visit(ASTNode node) {
    count++;
  }

  @Override
  public void visit(IScope node) {
    count++;
  }

  @Override
  public void visit(ISymbol node) {
    count++;
  }
}
