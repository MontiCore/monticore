/* (c) https://github.com/MontiCore/monticore */

package automata._symboltable;

import automata.AutomataMill;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

public class AutomatonSymbolDeSer
    extends AutomatonSymbolDeSerTOP {

  protected void serializeAddons(AutomatonSymbol a,
      AutomataSymbols2Json s2j) {
    JsonPrinter p = s2j.getJsonPrinter();
    p.beginArray("states");
    for (StateSymbol s : a.getSpannedScope().getLocalStateSymbols()) {
      p.value(s.getName());
    }
    p.endArray();
  }

  protected void deserializeAddons(AutomatonSymbol a, JsonObject j) {
    IAutomataScope s = a.getSpannedScope();
    for (JsonElement e : j.getArrayMember("states")) {
      String name = e.getAsJsonString().getValue();
      StateSymbol state = AutomataMill.stateSymbolBuilder().
          setName(name).build();
      s.add(state);
    }
  }
}