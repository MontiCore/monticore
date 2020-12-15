/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbolrules.symbolruletest._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SymbolruleTestSymbols2Json extends SymbolruleTestSymbols2JsonTOP {


  public  SymbolruleTestSymbols2Json()  {
    this(new de.monticore.symboltable.serialization.JsonPrinter());
  }

  public  SymbolruleTestSymbols2Json(de.monticore.symboltable.serialization.JsonPrinter printer)  {
    this.printer = printer;
    setRealThis(this);
  }

  @Override
  public void serializeTest1SuperTypes (java.util.List<de.monticore.types.check.SymTypeExpression> superTypes)  {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeTest2SuperTypes (java.util.List<de.monticore.types.check.SymTypeExpression> superTypes)  {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeITestSuperTypes (java.util.List<de.monticore.types.check.SymTypeExpression> superTypes)  {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeSymbolruleTestScopeSymType (Optional<SymTypeExpression> symType)  {
    SymTypeExpressionDeSer.serializeMember(printer, "symType", symType);
  }

}
