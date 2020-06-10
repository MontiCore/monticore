/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolLoader;

public class SymTypeOfSIUnit extends SymTypeExpression {

  public SymTypeOfSIUnit(OOTypeSymbolLoader typeSymbolLoader){
    this.typeSymbolLoader = typeSymbolLoader;
  }

  @Override
  public String print() {
    return typeSymbolLoader.getName();
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
    return new SymTypeOfSIUnit(new OOTypeSymbolLoader(typeSymbolLoader.getName(),typeSymbolLoader.getEnclosingScope()));
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if(!(sym instanceof SymTypeOfSIUnit)){
      return false;
    }
    SymTypeOfSIUnit symSi = (SymTypeOfSIUnit) sym;
    if(this.typeSymbolLoader== null ||symSi.typeSymbolLoader==null){
      return false;
    }
    if(!this.typeSymbolLoader.getEnclosingScope().equals(symSi.typeSymbolLoader.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbolLoader.getName().equals(symSi.typeSymbolLoader.getName())){
      return false;
    }
    return this.print().equals(symSi.print());
  }
}
