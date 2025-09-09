/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.function.Supplier;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

/**
 * A deserializer for a {@link SymbolWithScopeOfUnknownKind}.
 *
 * <p>This DeSer only supports deserialization. Calling {@link #serialize(SymbolWithScopeOfUnknownKind, Object)} will
 * fail.</p>
 */
public class SymbolWithScopeOfUnknownKindDeSer implements ISymbolDeSer<SymbolWithScopeOfUnknownKind, Object> {

  private final IDeSer<?, ?, ?> parent;
  private final Supplier<IScope> scopeFactory;

  /**
   * Creates a new {@code SymbolWithScopeOfUnknownKindDeSer}.
   *
   * @param parent        the language DeSer to delegate to when parsing spanned scopes
   * @param scopeFactory  the factory used to create language-specific spanned scopes
   */
  public SymbolWithScopeOfUnknownKindDeSer(IDeSer<?, ?, ?> parent, Supplier<IScope> scopeFactory) {
    this.parent = parent;
    this.scopeFactory = scopeFactory;
  }

  @Override
  public String getSerializedKind() {
    return "de.monticore.symboltable.SymbolWithScopeOfUnknownKind";
  }

  @Override
  public String serialize(SymbolWithScopeOfUnknownKind toSerialize, Object s2j) {
    Log.error("0xA7401 cannot serialize symbol with scope of unknown kind");
    throw new IllegalStateException(); // Normally this statement is not reachable
  }

  @Override
  public SymbolWithScopeOfUnknownKind deserialize(JsonObject symbolJson) {
    SymbolWithScopeOfUnknownKindBuilder builder = new SymbolWithScopeOfUnknownKindBuilder();
    builder.setName(symbolJson.getStringMember(JsonDeSers.NAME));

    if (symbolJson.hasObjectMember(JsonDeSers.SPANNED_SCOPE)) {
      /*
       * It is possible (and in fact part of the motivation behind this feature) that unknown spanned scopes contain
       * known symbols. To properly handle this case, deserialization is delegated to a "parent" deserializer. (Usually,
       * this will be the language-specific DeSer.)
       */
      IScope spannedScope = this.parent.deserializeScope(symbolJson.getObjectMember(JsonDeSers.SPANNED_SCOPE));
      builder.setSpannedScope(spannedScope);
    } else {
      IScope spannedScope = this.scopeFactory.get();
      builder.setSpannedScope(spannedScope);
    }

    return builder.build();
  }

}
