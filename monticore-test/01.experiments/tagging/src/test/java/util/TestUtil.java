package util;

import de.monticore.fqn.fqnautomata.FQNAutomataMill;
import de.monticore.fqn.fqnautomata._ast.ASTAutomaton;
import de.monticore.fqn.fqnautomata._ast.ASTTransition;
import de.monticore.fqn.fqnautomata._visitor.FQNAutomataTraverser;
import de.monticore.fqn.fqnautomata._visitor.FQNAutomataVisitor2;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {
  public static List<ASTTransition> getTransition(ASTAutomaton astAutomaton){
    List<ASTTransition> ret = new ArrayList<>();
    FQNAutomataTraverser traverser = FQNAutomataMill.traverser();
    traverser.add4FQNAutomata(new FQNAutomataVisitor2() {
      @Override
      public void visit(ASTTransition node) {
        ret.add(node);
      }
    });
    astAutomaton.accept(traverser);
    return ret;
  }
}
