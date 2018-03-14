/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

public class MyFieldSymbol extends CommonSymbol {
  
  public static final MyFieldKind KIND = new MyFieldKind();
  
  private final MyElementSymbol type;
  
  public MyFieldSymbol(final String name, final MyElementSymbol type) {
    super(name, KIND);
    this.type = type;
  }
  
  public MyElementSymbol getType() {
    return type;
  }
  
  static final class MyFieldKind implements SymbolKind {
    MyFieldKind() {
    }
  }
}
