/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;

public class MyModelSymbol extends CommonScopeSpanningSymbol {
  
  public static final MyModelKind KIND = new MyModelKind();
  
  public MyModelSymbol(final String name) {
    super(name, KIND);
  }
  
  public Optional<MyElementSymbol> getMyElement(final String name) {
    return getSpannedScope().resolveLocally(name, MyElementSymbol.KIND);
  }
  
  public Collection<MyElementSymbol> getMyElements() {
    return getSpannedScope().resolveLocally(MyElementSymbol.KIND);
  }
  
  static final class MyModelKind implements SymbolKind {
    MyModelKind() {
    }
  }
}
