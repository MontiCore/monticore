/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

public class MyElementSymbol extends CommonSymbol {
  
  public static final MyElementKind KIND = new MyElementKind();
  
  public MyElementSymbol(String name) {
    super(name, KIND);
  }
  
  static final class MyElementKind implements SymbolKind {
  }
}
