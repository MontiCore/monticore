/* (c) https://github.com/MontiCore/monticore */

package automata._symboltable;

import automata._visitor.AutomataHandler;
import automata._visitor.AutomataTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;

public class AutomataSymbols2Json extends AutomataSymbols2JsonTOP implements AutomataHandler {

  public AutomataSymbols2Json(){
    super();
    getTraverser().setAutomataHandler(this);
  }

  public AutomataSymbols2Json(AutomataTraverser traverser,
      JsonPrinter printer) {
    super(traverser, printer);
  }

  @Override public void traverse(IAutomataScope node) {
    for (automata._symboltable.AutomatonSymbol s : node.getLocalAutomatonSymbols()) {
      s.accept(getTraverser());
    }
  }

}
