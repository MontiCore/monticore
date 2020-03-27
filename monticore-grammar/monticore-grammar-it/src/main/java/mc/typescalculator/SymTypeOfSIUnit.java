/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

public class SymTypeOfSIUnit extends SymTypeExpression {

  public SymTypeOfSIUnit(TypeSymbolLoader typeSymbolLoader){
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
    jp.member(JsonConstants.KIND,
        "mc.typescalculator.SymTypeOfSIUnit");
    jp.member("unit", print());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfSIUnit deepClone() {
    return new SymTypeOfSIUnit(new TypeSymbolLoader(typeSymbolLoader.getName(),typeSymbolLoader.getEnclosingScope()));
  }
}
