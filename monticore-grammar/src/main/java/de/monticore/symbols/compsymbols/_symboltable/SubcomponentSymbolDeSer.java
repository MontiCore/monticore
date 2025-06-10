/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindExpressionDeSer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SubcomponentSymbolDeSer extends SubcomponentSymbolDeSerTOP {

  protected final CompKindExpressionDeSer compKindExprDeSer;

  public SubcomponentSymbolDeSer() {
    compKindExprDeSer = new CompKindExpressionDeSer();
  }

  /**
   * @param compKindExprDeSer the DeSer to use for (de)serializing the super components
   */
  public SubcomponentSymbolDeSer(@NonNull CompKindExpressionDeSer compKindExprDeSer) {
    this.compKindExprDeSer = Preconditions.checkNotNull(compKindExprDeSer);
  }

  protected CompKindExpressionDeSer getCompKindExprDeSer() {
    return this.compKindExprDeSer;
  }

  @Override
  protected void serializeType(CompKindExpression type, CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().memberJson("type", this.getCompKindExprDeSer().serialize(type));
  }

  @Override
  protected CompKindExpression deserializeType(ICompSymbolsScope scope, JsonObject symbolJson) {
    return this.getCompKindExprDeSer().deserialize(scope, symbolJson.getObjectMember("type"));
  }

  @Override
  protected CompKindExpression deserializeType(JsonObject symbolJson) {
    throw new UnsupportedOperationException();
  }
}
