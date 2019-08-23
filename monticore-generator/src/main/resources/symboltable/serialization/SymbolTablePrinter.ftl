<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName", "symTabPrinterName", "symbolTablePackage", "visitorPackage", "symbols", "scopeRule", "symbolRules")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()}.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ${symbolTablePackage}.*;
import ${visitorPackage}.*;
import de.monticore.symboltable.*;
import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * Prints the Symbol Table of the ${languageName} language.
 *
 */
public class ${symTabPrinterName}
    implements ${languageName}SymbolVisitor, ${languageName}ScopeVisitor {

  protected JsonPrinter printer = new JsonPrinter();

  public void visit(${languageName}ArtifactScope as) {
    printer.beginObject();
    printer.member(JsonConstants.KIND, "${symbolTablePackage}.${languageName}ArtifactScope");
    printer.member(JsonConstants.NAME, as.getName());
    printer.member(JsonConstants.PACKAGE, as.getPackageName());
    printer.member(JsonConstants.EXPORTS_SYMBOLS, as.exportsSymbols());
    printer.beginArray(JsonConstants.IMPORTS);
    as.getImports().forEach(x -> printer.value(x.toString()));
    printer.endArray();
    printer.member(JsonConstants.SCOPE_SPANNING_SYMBOL,serializeScopeSpanningSymbol(as.getSpanningSymbol()));
  }

  /**
   * @see ${languageName}ScopeVisitor#visit(${languageName}Scope)
   */
  @Override
  public void visit(${languageName}Scope scope) {
    printer.beginObject();
    printer.member(JsonConstants.KIND, "${symbolTablePackage}.${languageName}Scope");
    printer.member(JsonConstants.NAME, scope.getName());
    printer.member(JsonConstants.IS_SHADOWING_SCOPE, scope.isShadowingScope());
    printer.member(JsonConstants.EXPORTS_SYMBOLS, scope.exportsSymbols());
    printer.member(JsonConstants.SCOPE_SPANNING_SYMBOL,serializeScopeSpanningSymbol(scope.getSpanningSymbol()));
    
<#if scopeRule.isPresent()>
<#list scopeRule.get().getAdditionalAttributeList() as attr>
    printer.member("${attr.name}", serialize${attr.name}(scope));
</#list>
</#if>
  }
  
<#if scopeRule.isPresent()>
<#list scopeRule.get().getAdditionalAttributeList() as attr>
  <#assign attrType=attr.getMCType().getBaseName()>
  <#if attrType == "boolean" || attrType == "Boolean">
    <#if attr.getName()?starts_with("is")>
      <#assign methodName=attr.getName()>
    <#else>
      <#assign methodName="is" + attr.getName()?cap_first>
    </#if>
  <#else>
    <#assign methodName="get" + attr.getName()?cap_first>
  </#if>
<#if attrType == "boolean" || 
     attrType == "int" || 
     attrType == "float" || 
     attrType == "double" || 
     attrType == "long" || 
     attrType == "String">
<#assign retType=attrType>
<#assign retStatement="scope."+methodName+"()">
<#else>
<#assign retType="String">
<#assign retStatement="String.valueOf(scope."+methodName+"())">
</#if>
  protected ${retType} serialize${attr.name}(${languageName}Scope scope) {
    return ${retStatement};
  }
</#list>
</#if>

  protected Optional<String> serializeScopeSpanningSymbol(
      Optional<IScopeSpanningSymbol> spanningSymbol) {
    if (null != spanningSymbol && spanningSymbol.isPresent()) {
      JsonPrinter spPrinter = new JsonPrinter();
      spPrinter.beginObject();
      spPrinter.member(JsonConstants.KIND, spanningSymbol.get().getClass().getName());
      spPrinter.member(JsonConstants.NAME, spanningSymbol.get().getName());
      spPrinter.endObject();
      return Optional.ofNullable(spPrinter.getContent());
    }
    return Optional.empty();
  }

   /**
   * @see ${languageName}ScopeVisitor#traverse(${languageName}Scope)
   */
  @Override
  public void traverse(${languageName}Scope scope) {
<#list symbols as symbol>
    if (!scope.getLocal${symbol.name}Symbols().isEmpty()) {
      printer.beginArray("${symbol.name?lower_case}Symbols");
      scope.getLocal${symbol.name}Symbols().stream().forEach(s -> s.accept(getRealThis()));
      printer.endArray();
    }
</#list>
    Collection<I${languageName}Scope> subScopes = filterRelevantSubScopes(scope.getSubScopes());
    if (!subScopes.isEmpty()) {
      printer.beginArray(JsonConstants.SUBSCOPES);
      subScopes.stream().forEach(s -> s.accept(getRealThis()));
      printer.endArray();
    }
  }

  /**
   * @see ${languageName}ScopeVisitor#endVisit(${languageName}Scope)
   */
  @Override
  public void endVisit(${languageName}Scope scope) {
    printer.endObject();
  }
<#list symbols as symbol>

  /**
   * @see ${symbol.name}SymbolVisitor#visit(${symbol.name}Symbol)
   */
  @Override
  public void visit(${symbol.name}Symbol symbol) {
    printer.beginObject();
    printer.member(JsonConstants.KIND, "${symbolTablePackage}.${symbol.name}Symbol");
    printer.member(JsonConstants.NAME, symbol.getName());
    <#if symbolRules[symbol.name]??>
    <#list symbolRules[symbol.name].getAdditionalAttributeList() as attr>
    printer.member("${attr.name}", serialize${attr.name}(symbol));
    </#list>
    </#if>
  }

  /**
   * @see ${symbol.name}SymbolVisitor#endVisit(${symbol.name}Symbol)
   */
  @Override
  public void endVisit(${symbol.name}Symbol symbol) {
    printer.endObject();
  }
  
<#if symbolRules[symbol.name]??>
<#list symbolRules[symbol.name].getAdditionalAttributeList() as attr>
  <#assign attrType=genHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  <#if attrType == "boolean" || attrType == "Boolean">
    <#if attr.getName()?starts_with("is")>
      <#assign methodName=attr.getName()>
    <#else>
      <#assign methodName="is" + attr.getName()?cap_first>
    </#if>
  <#else>
    <#assign methodName="get" + attr.getName()?cap_first>
  </#if>
<#if attrType == "boolean" || 
     attrType == "int" || 
     attrType == "float" || 
     attrType == "double" || 
     attrType == "long" || 
     attrType == "String">
<#assign retType=attrType>
<#assign retStatement="symbol."+methodName+"()">
<#else>
<#assign retType="String">
<#assign retStatement="String.valueOf(symbol."+methodName+"())">
</#if>
  protected ${retType} serialize${attr.name}(${symbol.name}Symbol symbol) {
    return ${retStatement};
  }
  
</#list>
</#if>

</#list>


  @Override
  public ${symTabPrinterName} getRealThis() {
    return this;
  }

  public String getSerializedString() {
    return printer.getContent();
  }

  protected Collection<I${languageName}Scope> filterRelevantSubScopes(
      Collection<I${languageName}Scope> subScopes) {
    List<I${languageName}Scope> result = new ArrayList<>();
    for (I${languageName}Scope scope : subScopes) {
      if(hasSymbolsInSubScopes(scope)) {
        result.add(scope);
      }
    }
    return result;
  }
  
  protected boolean hasSymbolsInSubScopes(I${languageName}Scope scope) {
    boolean hasSymbolsInSubScopes = false;
    for (I${languageName}Scope subScope : scope.getSubScopes()) {
      hasSymbolsInSubScopes |= hasSymbolsInSubScopes(subScope);
    }
    return hasSymbolsInSubScopes | scope.getSymbolsSize()>0;
  }
  
}
