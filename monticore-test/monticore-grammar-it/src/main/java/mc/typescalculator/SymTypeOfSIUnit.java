/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;

public class SymTypeOfSIUnit extends SymTypeExpression {

  public SymTypeOfSIUnit(TypeSymbol typeSymbol){
    this.typeSymbol = typeSymbol;
  }

  @Override
  public String print() {
    return typeSymbol.getName();
  }

  @Override
  public String printFullName() {
    return typeSymbol.getFullName();
  }

  @Override
  public String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND,
        "mc.typescalculator.SymTypeOfSIUnit");
    jp.member("unit", print());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfSIUnit deepClone() {
    TypeSymbol symbol = new TypeSymbolSurrogate(typeSymbol.getName());
    symbol.setEnclosingScope(typeSymbol.getEnclosingScope());
    return new SymTypeOfSIUnit(symbol);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if(!(sym instanceof SymTypeOfSIUnit)){
      return false;
    }
    SymTypeOfSIUnit symSi = (SymTypeOfSIUnit) sym;
    if(this.typeSymbol== null ||symSi.typeSymbol==null){
      return false;
    }
    if(!this.typeSymbol.getEnclosingScope().equals(symSi.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symSi.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symSi.print());
  }
}
