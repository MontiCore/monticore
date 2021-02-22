/* (c) https://github.com/MontiCore/monticore */
package automata3._visitor;

import automata3._ast.ASTNewNT;
import de.se_rwth.commons.logging.Log;

public interface Automata3Traverser
                         extends Automata3TraverserTOP {

  // ----------------------------------------------------------
  // Here we can override any visit, endVisit, ... method
  // Which gets precedence over any (by default) delegated method:

  @Override
  default public void visit(ASTNewNT node) {
    Log.warn("Printed by the Automata3Traverser itself. "
        + "Visit call for ASTNewNT is not delegated to a visitor.");
  }

}
