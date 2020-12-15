/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scoperules.scoperuletest._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

public class ScoperuleTestSymbols2Json extends ScoperuleTestSymbols2JsonTOP {

  public  ScoperuleTestSymbols2Json()  {
    this(new de.monticore.symboltable.serialization.JsonPrinter());
  }

  public  ScoperuleTestSymbols2Json(de.monticore.symboltable.serialization.JsonPrinter printer)  {
    this.printer = printer;
    setRealThis(this);
  }

  @Override
  public void serializeScoperuleTestScopeSymType (Optional<SymTypeExpression> symType)  {
    SymTypeExpressionDeSer.serializeMember(printer, "symType", symType);
  }

}
