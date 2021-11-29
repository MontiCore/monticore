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

  public List<TypeVarSymbol> getTypeParameterList(){
    return lazyLoadDelegate().getSpannedScope().getLocalTypeVarSymbols();
  }

  public List<SymTypeExpression> getSuperClassesOnly(){
    return lazyLoadDelegate().getSuperClassesOnly();
  }

  @Override
  public List<SymTypeExpression> getSuperTypesList() {
    if (!checkLazyLoadDelegate()) {
      return Lists.newArrayList();
    }
    return super.getSuperTypesList();
  }

  @Override
  public String getFullName() {
    if (checkLazyLoadDelegate()) {
      return lazyLoadDelegate().getFullName();
    }
    return getName();
  }
}
