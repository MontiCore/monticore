/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class OOTypeSymbolSurrogate extends OOTypeSymbolSurrogateTOP {

  public OOTypeSymbolSurrogate(String name){
    super(name);
  }

  public IOOSymbolsScope getSpannedScope(){
    if (!checkLazyLoadDelegate()) {
      if (spannedScope instanceof IOOSymbolsScope) {
        return (IOOSymbolsScope) spannedScope;
      } else {
        Log.error("0xA7019 Could not cast the spanned scope to an IOOSymbolsScope.");
      }
      throw new IllegalStateException();
    }
    return lazyLoadDelegate().getSpannedScope();
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return lazyLoadDelegate().getSuperClassesOnly();
  }

}
