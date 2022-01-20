/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class TypeSymbolSurrogate extends TypeSymbolSurrogateTOP {

  public TypeSymbolSurrogate(String name){
    super(name);
  }

  public IBasicSymbolsScope getSpannedScope(){
    return lazyLoadDelegate().getSpannedScope();
  }

  @Override
  public List<TypeVarSymbol> getTypeParameterList(){
    if (!checkLazyLoadDelegate()) {
      return Lists.newArrayList();
    }
    return lazyLoadDelegate().getSpannedScope().getLocalTypeVarSymbols();
  }

  @Override
  public List<SymTypeExpression> getSuperClassesOnly(){
    if (!checkLazyLoadDelegate()) {
      return Lists.newArrayList();
    }
    return lazyLoadDelegate().getSuperClassesOnly();
  }

  @Override
  public String getFullName() {
    if (checkLazyLoadDelegate()) {
      return lazyLoadDelegate().getFullName();
    }
    return getName();
  }
}
