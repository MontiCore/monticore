<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName","className","scopeRules", "symbolNames", "spanningSymbols", "superGrammarPackages")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">
<#assign serializedKind = "${genHelper.getSymbolTablePackage()}.${languageName}Scope">
<#assign serializedASKind = "${genHelper.getSymbolTablePackage()}.${languageName}ArtifactScope">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()}.serialization;

import ${genHelper.getSymbolTablePackage()}.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.*;
import de.monticore.symboltable.serialization.json.*;

import de.se_rwth.commons.logging.Log;

<#list superGrammarPackages as s>
import ${s}.*;
import ${s}.serialization.*;
</#list>

/**
 * Class for serializing and deserializing ${languageName}Scopes
 */
public class ${className} implements IDeSer<I${languageName}Scope> {

<#list symbolNames?keys as symbol>
${symbol}SymbolDeSer ${symbol?lower_case}SymbolDeSer = new ${symbol}SymbolDeSer();
</#list>
  

  public void store(${languageName}ArtifactScope as, ${languageName}Language lang, String symbolPath) {
    store(as, Paths.get(symbolPath, as.getFilePath(lang).toString()));
  }
  
  /**
  * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
  */
  @Override
  public String getSerializedKind() {
    return "${serializedKind}";
  }
  
  public String getSerializedASKind() {
    return "${serializedASKind}";
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(I${languageName}Scope toSerialize) {
    ${languageName}SymbolTablePrinter printer = new ${languageName}SymbolTablePrinter();
    toSerialize.accept(printer);
    return printer.getSerializedString();
  }

/**
   * @throws IOException
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<I${languageName}Scope> deserialize(String serialized) {
    JsonObject scope = JsonParser.parseJsonObject(serialized);
    return deserialize(scope);
  }
  
  public Optional<I${languageName}Scope> deserialize(JsonObject scopeJson) {
    String kind = scopeJson.get(JsonConstants.KIND).getAsJsonString().getValue();
    if (this.getSerializedKind().equals(kind)) {
      return Optional.of(deserialize${languageName}Scope(scopeJson));
    }
    else if (this.getSerializedASKind().equals(kind)) {
      return Optional.of(deserialize${languageName}ArtifactScope(scopeJson));
    }
    return Optional.empty();
  }
  
  protected ${languageName}Scope deserialize${languageName}Scope(JsonObject scopeJson) {
    Optional<String> name = scopeJson.getStringOpt(JsonConstants.NAME);
    Optional<Boolean> exportsSymbols = scopeJson.getBooleanOpt(JsonConstants.EXPORTS_SYMBOLS);
    Optional<Boolean> isShadowingScope = scopeJson.getBooleanOpt(JsonConstants.IS_SHADOWING_SCOPE);
    
    ${languageName}Scope scope = new ${languageName}Scope(isShadowingScope.orElse(false));
    name.ifPresent(scope::setName);
    scope.setExportsSymbols(exportsSymbols.orElse(true));
    
<#list scopeRules as scopeRule>
<#list scopeRule.getAdditionalAttributeList() as attr>
    scope.set${attr.getName()?cap_first}(deserialize${attr.getName()?cap_first}(scopeJson));
</#list>   
</#list>
    
    addSymbols(scopeJson, scope);
    addAndLinkSubScopes(scopeJson, scope);
    deserializeAdditionalAttributes(scope,scopeJson);
    return scope;
  }


  
  protected ${languageName}ArtifactScope deserialize${languageName}ArtifactScope(JsonObject scopeJson) {
    String name = scopeJson.get(JsonConstants.NAME).getAsJsonString().getValue();
    String packageName = scopeJson.get(JsonConstants.PACKAGE).getAsJsonString().getValue();
    List<ImportStatement> imports = JsonUtil.deserializeImports(scopeJson);
    boolean exportsSymbols = scopeJson.get(JsonConstants.EXPORTS_SYMBOLS).getAsJsonBoolean().getValue();
    
    ${languageName}ArtifactScope scope = new ${languageName}ArtifactScope(packageName, imports);
    scope.setName(name);
    scope.setExportsSymbols(exportsSymbols);
    
<#list scopeRules as scopeRule>
<#list scopeRule.getAdditionalAttributeList() as attr>
    scope.set${attr.getName()?cap_first}(deserialize${attr.getName()?cap_first}(scopeJson));
</#list>   
</#list>

    addSymbols(scopeJson, scope);
    addAndLinkSubScopes(scopeJson, scope);
    deserializeAdditionalAttributes(scope,scopeJson);
    return scope;
  }
  
  
  protected void addSymbols(JsonObject scopeJson, ${languageName}Scope scope) {
<#list symbolNames?keys as symbol>  
    if (scopeJson.containsKey("${symbol?lower_case}Symbols")) {
      List<JsonElement> ${symbol?lower_case}Symbols = scopeJson.get("${symbol?lower_case}Symbols").getAsJsonArray().getValues();
      for (JsonElement ${symbol?lower_case}Symbol : ${symbol?lower_case}Symbols) {
        deserialize${symbol}Symbol(${symbol?lower_case}Symbol.getAsJsonObject(), scope);
      }
    }
</#list>     
  }
  
  protected void addAndLinkSubScopes(JsonObject scopeJson, ${languageName}Scope scope) {
    if (scopeJson.containsKey(JsonConstants.SUBSCOPES)) {
      List<JsonElement> elements = scopeJson.get(JsonConstants.SUBSCOPES).getAsJsonArray()
          .getValues();
      for (JsonElement subScopeJson : elements) {
        JsonObject s = subScopeJson.getAsJsonObject();
        Optional<I${languageName}Scope> subScope = deserialize(s);
        if (subScope.isPresent()) {
          addAndLinkSpanningSymbol(s, subScope.get(), scope);
          subScope.get().setEnclosingScope(scope);
          scope.addSubScope(subScope.get());
        }
        else {
          Log.error("Deserialization of subscope "+s+" failed!");
        }
      }
    }
  }
  
  protected void addAndLinkSpanningSymbol(JsonObject subScopeJson, I${languageName}Scope subScope,
      ${languageName}Scope scope) {
    if (subScopeJson.containsKey(JsonConstants.SCOPE_SPANNING_SYMBOL)) {
      JsonObject symbolRef = subScopeJson.get(JsonConstants.SCOPE_SPANNING_SYMBOL)
          .getAsJsonObject();
      String spanningSymbolName = symbolRef.get(JsonConstants.NAME).getAsJsonString().getValue();
      String spanningSymbolKind = symbolRef.get(JsonConstants.KIND).getAsJsonString().getValue();
<#assign elseif = "">
<#list spanningSymbols?keys as symbol>      
      ${elseif} if (spanningSymbolKind.equals(${symbol?lower_case}SymbolDeSer.getSerializedKind())) {
        Optional<${symbol}Symbol> spanningSymbol = scope.resolve${symbol}Locally(spanningSymbolName);
        if (spanningSymbol.isPresent()) {
          subScope.setSpanningSymbol(spanningSymbol.get());
        }
        else {
          Log.error("Spanning symbol of scope "+subScopeJson+" could not be found during deserialization!");
        }
      }
<#assign elseif = "else">
</#list>
      }
<#if symbolNames?keys?size!=0>    
      else {
        Log.error("Unknown kind of scope spanning symbol: "+JsonConstants.SCOPE_SPANNING_SYMBOL);
      }
</#if>
  }
  
<#list symbolNames?keys as symbol>  
  protected void deserialize${symbol}Symbol(JsonObject symbolJson, ${languageName}Scope scope) {
    Optional<${symbol}Symbol> symbol = ${symbol?lower_case}SymbolDeSer.deserialize(symbolJson);
    if (symbol.isPresent()) {
      scope.add(symbol.get());
    }
    else {
      Log.error("Deserialization of "+symbolJson+" failed!");
    }
  }
  
</#list> 

<#list scopeRules as scopeRule>
<#list scopeRule.getAdditionalAttributeList() as attr>
  <#assign attrType=stHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  protected ${attrType} deserialize${attr.getName()?cap_first}(JsonObject scopeJson){
<#switch attrType>
<#case "String">
    return scopeJson.get("${attr.getName()}").getAsJsonString().getValue();
<#break>
<#case "boolean">
    return scopeJson.get("${attr.getName()}").getAsJsonBoolean().getValue();
<#break>
<#case "int">
    return scopeJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsInt();
<#break>
<#case "float">
    return scopeJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsFloat();
<#break>
<#case "double">
    return scopeJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsDouble();
<#break>
<#case "long">
    return scopeJson.get("${attr.getName()}").getAsJsonNumber().getNumberAsLong();
<#break>
<#default>
    Log.error("Unable to deserialize scope attribute ${attr.getName()} of type ${attrType}. Please override the method ${className}#deserialize${attr.getName()?cap_first}(JsonObject) using the TOP mechanism!");
    return null;
</#switch>
  }

</#list>   
</#list>

  /**
   * Override this method to deserialize additional attributes
   * @param scope the scope object deserialized so far
   * @param scopeJson json representation of the serialized scope
   */
  protected void deserializeAdditionalAttributes(I${languageName}Scope scope, JsonObject scopeJson) {
    
  }
}