/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.FullCompKindExprDeSer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SubcomponentSymbolDeSer extends SubcomponentSymbolDeSerTOP {

  private FullCompKindExprDeSer compKindExprDeSer;

  public SubcomponentSymbolDeSer() {

  }

  protected FullCompKindExprDeSer getCompKindExprDeSer() {
    return this.compKindExprDeSer;
  }

  /**
   * @param compKindExprDeSer the DeSer to use for (de)serializing the super components
   */
  public SubcomponentSymbolDeSer(@NonNull FullCompKindExprDeSer compKindExprDeSer) {
    this.compKindExprDeSer = Preconditions.checkNotNull(compKindExprDeSer);
  }

  @Override
  protected void serializeType(CompKindExpression type, CompSymbolsSymbols2Json s2j) {
    s2j.getJsonPrinter().memberJson("type", this.getCompKindExprDeSer().serializeAsJson(type));
  }

  @Override
  protected CompKindExpression deserializeType(JsonObject symbolJson) {
    return null;
  }
}
