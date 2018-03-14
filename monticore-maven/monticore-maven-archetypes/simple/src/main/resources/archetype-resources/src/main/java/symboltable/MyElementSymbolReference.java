/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;

public class MyElementSymbolReference extends MyElementSymbol implements SymbolReference<MyElementSymbol> {
  
  private final SymbolReference<MyElementSymbol> reference;
  
  public MyElementSymbolReference(final String name, final Scope definingScopeOfReference) {
    super(name);
    
    reference = new CommonSymbolReference<>(name, MyElementSymbol.KIND, definingScopeOfReference);
  }
  
  @Override
  public MyElementSymbol getReferencedSymbol() {
    return reference.getReferencedSymbol();
  }
  
  @Override
  public boolean existsReferencedSymbol() {
    return reference.existsReferencedSymbol();
  }
  
  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }
  
  @Override
  public boolean isReferencedSymbolLoaded() {
    return reference.isReferencedSymbolLoaded();
  }
  
}
