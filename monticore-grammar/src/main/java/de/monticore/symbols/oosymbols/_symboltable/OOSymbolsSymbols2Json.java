// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class OOSymbolsSymbols2Json extends OOSymbolsSymbols2JsonTOP {

  public OOSymbolsSymbols2Json() {
  }

  public OOSymbolsSymbols2Json(JsonPrinter printer) {
    super(printer);
  }

  @Override
  public void serializeOOTypeSuperTypes(List<SymTypeExpression> superTypes) {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeFieldType(SymTypeExpression type) {
    SymTypeExpressionDeSer.serializeMember(printer, "type", type);
  }

  @Override
  public void serializeMethodReturnType(SymTypeExpression returnType) {
    SymTypeExpressionDeSer.serializeMember(printer, "returnType", returnType);
  }

}
