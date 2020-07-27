/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;

public class SymTypeOfSIUnit extends SymTypeExpression {

  public SymTypeOfSIUnit(OOTypeSymbolSurrogate typeSymbolSurrogate){
    this.typeSymbolSurrogate = typeSymbolSurrogate;
  }

  @Override
  public String print() {
    return typeSymbolSurrogate.getName();
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
    OOTypeSymbolSurrogate surrogate = new OOTypeSymbolSurrogate(typeSymbolSurrogate.getName());
    surrogate.setEnclosingScope(typeSymbolSurrogate.getEnclosingScope());
    return new SymTypeOfSIUnit(surrogate);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if(!(sym instanceof SymTypeOfSIUnit)){
      return false;
    }
    SymTypeOfSIUnit symSi = (SymTypeOfSIUnit) sym;
    if(this.typeSymbolSurrogate== null ||symSi.typeSymbolSurrogate==null){
      return false;
    }
    if(!this.typeSymbolSurrogate.getEnclosingScope().equals(symSi.typeSymbolSurrogate.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbolSurrogate.getName().equals(symSi.typeSymbolSurrogate.getName())){
      return false;
    }
    return this.print().equals(symSi.print());
  }
}
