/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight._symboltable;

import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class JavaMethodSymbolDeSer extends JavaMethodSymbolDeSerTOP {

  @Override
  protected void serializeExceptions(List<SymTypeExpression> exceptions, JavaLightSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "exceptions", exceptions);
  }

  @Override
  protected void serializeAnnotations(List<SymTypeExpression> annotations, JavaLightSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "annotations", annotations);
  }

  @Override
  protected void serializeReturnType(SymTypeExpression returnType, JavaLightSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "returnType", returnType);
  }

  @Override
  protected List<SymTypeExpression> deserializeExceptions(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("exceptions", symbolJson,
            OOSymbolsMill.globalScope());
  }

  @Override
  protected List<SymTypeExpression> deserializeAnnotations(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("annotations", symbolJson,
            OOSymbolsMill.globalScope());
  }

  @Override
  protected SymTypeExpression deserializeReturnType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson,
            OOSymbolsMill.globalScope());
  }

}
