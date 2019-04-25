<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName", "symTabPrinterName", "symbolTablePackage", "visitorPackage", "symbols")}

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

  JsonPrinter printer = new JsonPrinter();

  public void visit(${languageName}ArtifactScope as) {
    printer.beginObject();
    printer.attribute(JsonConstants.KIND, as.getClass().getName());
    printer.attribute(JsonConstants.NAME, as.getName());
    printer.attribute(JsonConstants.PACKAGE, as.getPackageName());
    printer.attribute(JsonConstants.EXPORTS_SYMBOLS, as.exportsSymbols());
    printer.attribute(JsonConstants.IMPORTS, as.getImports());
    printer.attribute(JsonConstants.SCOPE_SPANNING_SYMBOL,serializeScopeSpanningSymbol(as.getSpanningSymbol()));
  }

  /**
   * @see ${languageName}ScopeVisitor#visit(${languageName}Scope)
   */
  @Override
  public void visit(${languageName}Scope scope) {
    printer.beginObject();
    printer.attribute(JsonConstants.KIND, scope.getClass().getName());
    printer.attribute(JsonConstants.NAME, scope.getName());
    printer.attribute(JsonConstants.IS_SHADOWING_SCOPE, scope.isShadowingScope());
    printer.attribute(JsonConstants.EXPORTS_SYMBOLS, scope.exportsSymbols());
    printer.attribute(JsonConstants.SCOPE_SPANNING_SYMBOL,serializeScopeSpanningSymbol(scope.getSpanningSymbol()));
  }

  protected Optional<String> serializeScopeSpanningSymbol(
      Optional<IScopeSpanningSymbol> spanningSymbol) {
    if (null != spanningSymbol && spanningSymbol.isPresent()) {
      JsonPrinter spPrinter = new JsonPrinter();
      spPrinter.beginObject();
      spPrinter.attribute(JsonConstants.KIND, spanningSymbol.get().getClass().getName());
      spPrinter.attribute(JsonConstants.NAME, spanningSymbol.get().getName());
      spPrinter.endObject();
      return Optional.ofNullable(spPrinter.toString());
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
      printer.beginAttributeList("${symbol.name?lower_case}Symbols");
      scope.getLocal${symbol.name}Symbols().stream().forEach(s -> s.accept(getRealThis()));
      printer.endAttributeList();
    }
</#list>
    Collection<I${languageName}Scope> subScopes = filterRelevantSubScopes(scope.getSubScopes());
    if (!subScopes.isEmpty()) {
      printer.beginAttributeList(JsonConstants.SUBSCOPES);
      subScopes.stream().forEach(s -> s.accept(getRealThis()));
      printer.endAttributeList();
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
    printer.attribute(JsonConstants.KIND, symbol.getClass().getName());
    printer.attribute(JsonConstants.NAME, symbol.getName());
  }

  /**
   * @see ${symbol.name}SymbolVisitor#endVisit(${symbol.name}Symbol)
   */
  @Override
  public void endVisit(${symbol.name}Symbol symbol) {
    printer.endObject();
  }
</#list>


  @Override
  public ${symTabPrinterName} getRealThis() {
    return this;
  }

  public String getSerializedString() {
    return printer.toString();
  }

  protected Collection<I${languageName}Scope> filterRelevantSubScopes(
      Collection<I${languageName}Scope> subScopes) {
    List<I${languageName}Scope> result = new ArrayList<>();
    for (I${languageName}Scope scope : subScopes) {
      // TODO: start DFS to check whether a transitive subscope exports symbols
      result.add(scope);
    }
    return result;
  }

}
