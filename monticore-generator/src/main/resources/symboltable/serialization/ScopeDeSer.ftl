<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName","className","scopeRule", "symbolNames")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()}.serialization;

import ${genHelper.getSymbolTablePackage()}.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.stream.JsonReader;
import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.ScopeDeserializationResult;
import de.monticore.symboltable.serialization.SpanningSymbolReference;
import de.se_rwth.commons.logging.Log;


/**
 * Class for serializing and deserializing ${languageName}Scopes
 */
public class ${className} implements IDeSer<I${languageName}Scope> {

<#list symbolNames?keys as symbol>
${symbol}SymbolDeSer ${symbol?lower_case}SymbolDeSer = new ${symbol}SymbolDeSer();
</#list>
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(I${languageName}Scope toSerialize) {
    ${languageName}SymbolTablePrinter ${className?lower_case}SymbolTablePrinter = new ${languageName}SymbolTablePrinter();
    toSerialize.accept(${className?lower_case}SymbolTablePrinter);
    return ${className?lower_case}SymbolTablePrinter.getSerializedString();
  }
  
  /**
   * @throws IOException
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<I${languageName}Scope> deserialize(String serialized) {
JsonReader reader = new JsonReader(new StringReader(serialized));
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (!kind.equals(getSerializedKind())) {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer "
                  + this.getClass().getName() + " failed");
            }
            else {
              Optional<ScopeDeserializationResult<I${languageName}Scope>> deserializedScope = deserialize${languageName}Scope(
                  reader);
              reader.endObject();
              if (deserializedScope.isPresent()) {
                return Optional.ofNullable(deserializedScope.get().getScope());
              }
              return Optional.empty();
            }
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
      reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
  public Optional<ScopeDeserializationResult<I${languageName}Scope>> deserialize${languageName}Scope(JsonReader reader) {
    // Part 1: Initialize all attributes with default values
    I${languageName}Scope scope = null;
    Optional<String> name = Optional.empty();
    boolean isShadowingScope = false;
<#list symbolNames?keys as symbol>
    List<${symbol}Symbol> ${symbol?lower_case}Symbols = new ArrayList<>();
</#list>
    List<ScopeDeserializationResult<I${languageName}Scope>> subScopes = new ArrayList<>();
    Optional<SpanningSymbolReference> spanningSymbol = Optional.empty();
    
    // Part 2: Read all available values from the Json string
    try {
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (!kind.equals(getSerializedKind())) {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer "
                  + this.getClass().getName() + " failed");
            }
            break;
          case JsonConstants.NAME:
            name = Optional.ofNullable(reader.nextString());
            break;
          case JsonConstants.IS_SHADOWING_SCOPE:
            isShadowingScope = reader.nextBoolean();
            break;
          case JsonConstants.SCOPE_SPANNING_SYMBOL:
            spanningSymbol = Optional.ofNullable(deserializeSpanningSymbol(reader));
            break;
<#list symbolNames?keys as symbol>
          case "${symbol?lower_case}Symbols":
            ${symbol?lower_case}Symbols = deserializeLocal${symbol}Symbols(reader);
            break;
</#list>
          case JsonConstants.SUBSCOPES:
            subScopes = deserializeSubScopes(reader);
            break;
          default:
            reader.skipValue();
            break;
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    
    // Part 3: Construct the symbol/scope object
    scope = new ${languageName}Scope(isShadowingScope);
    scope.setName(name.orElse(null));
<#list symbolNames?keys as symbol>
    for (${symbol}Symbol s : ${symbol?lower_case}Symbols) {
      scope.add(s);
    }
</#list>

    for (I${languageName}Scope s : linkSubScopes(scope, subScopes)) {
      scope.addSubScope(s);
    }
    return Optional.of(new ScopeDeserializationResult<I${languageName}Scope>(scope,spanningSymbol));
  }
  
  protected List<I${languageName}Scope> linkSubScopes(I${languageName}Scope scope, List<ScopeDeserializationResult<I${languageName}Scope>> subScopes){
    List<I${languageName}Scope> subScopeList = new ArrayList<>();
    for (ScopeDeserializationResult<I${languageName}Scope> subScope : subScopes) {
      subScope.getScope().setEnclosingScope(scope);
      if (subScope.hasSpanningSymbol()) {
        if (stateSymbolDeSer.getSerializedKind().equals(subScope.getSpanningSymbolKind())) {
          Optional<StateSymbol> spanningStateSymbol = scope.resolveStateLocally(subScope.getSpanningSymbolName());
          if(spanningStateSymbol.isPresent()) {
            subScope.getScope().setSpanningSymbol(spanningStateSymbol.get());
          }
        }
        else if (automatonSymbolDeSer.getSerializedKind().equals(subScope.getSpanningSymbolKind())) {
          Optional<AutomatonSymbol> spanningAutomatonSymbol = scope.resolveAutomatonLocally(subScope.getSpanningSymbolName());
          if(spanningAutomatonSymbol.isPresent()) {
            subScope.getScope().setSpanningSymbol(spanningAutomatonSymbol.get());
          }
        }
        else {
          Log.error("Unknown spanning symbol kind "+subScope.getSpanningSymbolKind()+" in ${languageName}ScopeDeSer");
        }
      }
      subScopeList.add(subScope.getScope());
    }
    return subScopeList;
  }
  
  protected SpanningSymbolReference deserializeSpanningSymbol(JsonReader reader) throws IOException {
    String kind = null;
    String name = null;
    reader.beginObject();
    while (reader.hasNext()) {
      String key = reader.nextName();
      switch (key) {
        case JsonConstants.KIND:
          kind = reader.nextString();
          break;
        case JsonConstants.NAME:
          name = reader.nextString();
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    if (null != kind && null != name) {
      return new SpanningSymbolReference(kind, name);
    }
    return null;
  }
  
  //TODO: Generate this for all symbols in this scope 
<#list symbolNames?keys as symbol>
  protected List<${symbol}Symbol> deserializeLocal${symbol}Symbols(JsonReader reader)
      throws IOException {
    List<${symbol}Symbol> symbols = new ArrayList<>();
    reader.beginArray();
    while (reader.hasNext()) {
      reader.beginObject();
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (kind.equals(${symbol?lower_case}SymbolDeSer.getSerializedKind())) {
              Optional<${symbol}Symbol> ${symbol?lower_case}Symbol = ${symbol?lower_case}SymbolDeSer
                  .deserialize${symbol}Symbol(reader);
              if (${symbol?lower_case}Symbol.isPresent()) {
                symbols.add(${symbol?lower_case}Symbol.get());
                
              }
            }
            else {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer "
                  + this.getClass().getName() + " failed");
            }
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
    }
    reader.endArray();
    return symbols;
  }
  </#list>
  
  protected List<ScopeDeserializationResult<I${languageName}Scope>> deserializeSubScopes(JsonReader reader)
      throws IOException {
    List<ScopeDeserializationResult<I${languageName}Scope>> subScopes = new ArrayList<>();
    reader.beginArray();
    while (reader.hasNext()) {
      reader.beginObject();
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (!kind.equals(getSerializedKind())) {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer "
                  + this.getClass().getName() + " failed");
            }
            else {
              Optional<ScopeDeserializationResult<I${languageName}Scope>> deserializedScope = deserialize${languageName}Scope(
                  reader);
              if (deserializedScope.isPresent()) {
                subScopes.add(deserializedScope.get());
              }
            }
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
    }
    reader.endArray();
    return subScopes;
  }
  
  /**
  * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
  */
  @Override
  public String getSerializedKind() {
    return ${languageName}Scope.class.getName();
  }
}