/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class OOTypeSymbolSurrogate extends OOTypeSymbolSurrogateTOP {

  public OOTypeSymbolSurrogate(String name){
    super(name);
  }

  public IOOSymbolsScope getSpannedScope(){
    return lazyLoadDelegate().getSpannedScope();
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return lazyLoadDelegate().getSuperClassesOnly();
  }

}
