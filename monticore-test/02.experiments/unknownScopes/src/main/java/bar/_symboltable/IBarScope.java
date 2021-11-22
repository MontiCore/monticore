/* (c) https://github.com/MontiCore/monticore */
package bar._symboltable;

import de.monticore.symboltable.SymbolWithScopeOfUnknownKind;

public interface IBarScope extends IBarScopeTOP {

  void add(SymbolWithScopeOfUnknownKind symbol);

}
