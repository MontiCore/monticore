/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import java.util.Optional;

public class AssocRoleSymbolDeSer extends AssocRoleSymbolDeSerTOP {

    @Override
    protected void serializeCardinality(Optional<AssocCardinality> cardinality, OOSymbolsSymbols2Json s2j) {
      if (cardinality.isPresent()) {
        s2j.printer.member("cardinality", cardinality.get().serializeCard());
      }
    }
    
    @Override
    protected void serializeField(Optional<FieldSymbol> field, OOSymbolsSymbols2Json s2j) {
      if (field.isPresent()) {
        s2j.printer.member("field", field.get().getFullName());
      }
    }
    
    @Override
    protected void serializeAttributeQualifier(
        Optional<VariableSymbol> attributeQualifier, OOSymbolsSymbols2Json s2j) {
      attributeQualifier.ifPresent(
          fieldSymbol -> s2j.printer.member("attributeQualifier", fieldSymbol.getName()));
    }
    
    @Override
    protected void serializeTypeQualifier(
        Optional<SymTypeExpression> typeQualifier, OOSymbolsSymbols2Json s2j) {
      SymTypeExpressionDeSer.serializeMember(s2j.printer, "typeQualifier", typeQualifier);
    }
    
    @Override
    protected void serializeAssoc(Optional<SymAssociation> assoc, OOSymbolsSymbols2Json s2j) {
      if (assoc != null && assoc.isPresent()) {
        s2j.printer.member("association", handleSymAssociation(assoc.get()));
      }
    }
    
    @Override
    protected void serializeType(SymTypeExpression type, OOSymbolsSymbols2Json s2j) {
      SymTypeExpressionDeSer.serializeMember(s2j.printer, "type", type);
    }
    
    @Override
    protected Optional<AssocCardinality> deserializeCardinality(JsonObject symbolJson) {
      if (symbolJson.hasMember("cardinality")) {
        
        return Optional.ofNullable(
            AssocCardinality.deserializeCard(symbolJson.getStringMember("cardinality")));
      }
      return Optional.empty();
    }
    
    @Override
    protected Optional<FieldSymbol> deserializeField(JsonObject symbolJson) {
      if (symbolJson.hasMember("field")) {
        return OOSymbolsMill.globalScope().resolveField(symbolJson.getStringMember("field"));
      }
      return Optional.empty();
    }
    
    @Override
    protected Optional<VariableSymbol> deserializeAttributeQualifier(JsonObject symbolJson) {
      if (symbolJson.hasMember("attributeQualifier")) {
        final String fieldName = symbolJson.getStringMember("attributeQualifier");
        final SymTypeExpression type = SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
        // crate a surrogate to link to the existing variable
        return Optional.of(
            OOSymbolsMill.fieldSymbolSurrogateBuilder()
                .setName(fieldName)
                .setEnclosingScope(OOSymbolsMill.globalScope())
                .setType(type)
                .build());
      }
      return Optional.empty();
    }
    
    @Override
    protected Optional<SymTypeExpression> deserializeTypeQualifier(JsonObject symbolJson) {
      if (symbolJson.hasMember("typeQualifier")) {
        return Optional.of(SymTypeExpressionDeSer.deserializeMember("typeQualifier", symbolJson));
      }
      return Optional.empty();
    }
    
    @Override
    protected Optional<SymAssociation> deserializeAssoc(JsonObject symbolJson) {
      return symbolJson
          .getIntegerMemberOpt("association")
          .flatMap(
              a ->
                  Optional.ofNullable(
                      CDDeSerHelper.getInstance().getSymAssocForDeserialization().get(a)));
    }
    
    @Override
    protected SymTypeExpression deserializeType(JsonObject symbolJson) {
      return SymTypeExpressionDeSer.deserializeMember("type", symbolJson);
    }
  }
}
