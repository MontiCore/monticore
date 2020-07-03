// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.basictypesymbols._symboltable;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class BasicTypeSymbolsSymbolTablePrinter extends BasicTypeSymbolsSymbolTablePrinterTOP {

  public BasicTypeSymbolsSymbolTablePrinter() {
  }

  public BasicTypeSymbolsSymbolTablePrinter(JsonPrinter printer) {
    super(printer);
  }

  @Override
  public void serializeTypeSuperTypes(List<SymTypeExpression> superTypes) {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeTypeVarSuperTypes(List<SymTypeExpression> superTypes) {
    SymTypeExpressionDeSer.serializeMember(printer, "superTypes", superTypes);
  }

  @Override
  public void serializeVariableType(SymTypeExpression type) {
    SymTypeExpressionDeSer.serializeMember(printer, "type", type);
  }

  @Override
  public void serializeFunctionReturnType(SymTypeExpression returnType) {
    SymTypeExpressionDeSer.serializeMember(printer, "returnType", returnType);
  }

}
