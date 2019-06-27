<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","interfaceName", "languageName", "symbolNames")}

<#assign genHelper = glex.getGlobalVar("stHelper")>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

public interface ${className} extends ${interfaceName} {

  ModelPath getModelPath();

  ${languageName}Language get${languageName}Language();

  void cache(String calculatedModelName);

  boolean continueWithModelLoader(String calculatedModelName, ${languageName}ModelLoader modelLoader);
  default boolean checkIfContinueAsSubScope(String symbolName) {
    return false;
  }

<#list symbolNames?keys as symbol>

  default Collection<${symbolNames[symbol]}> resolve${symbol}Many(boolean foundSymbols,
      final String symbolName, final AccessModifier modifier, final Predicate<${symbolNames[symbol]}> predicate) {

    // First, try to resolve the symbol in the current scope and its sub scopes.
    Collection<${symbolNames[symbol]}> resolvedSymbol = resolve${symbol}DownMany(foundSymbols, symbolName,  modifier, predicate);

    if (!resolvedSymbol.isEmpty()) {
      return resolvedSymbol;
    }

    // Symbol not found: try to load corresponding model and build its symbol table
    loadModelsFor${symbol}(symbolName);

    // Maybe the symbol now exists in this scope (or its sub scopes). So, resolve down, again.
    resolvedSymbol = resolve${symbol}DownMany(false, symbolName, modifier, predicate);
    foundSymbols = foundSymbols  | resolvedSymbol.size() > 0;
    if (!foundSymbols && !is${symbol}SymbolAlreadyResolved()){
      set${symbol}SymbolAlreadyResolved(true);
      resolvedSymbol.addAll(resolveAdapted${symbol}(foundSymbols, symbolName, modifier, predicate));
    }
    set${symbol}SymbolAlreadyResolved(false);
    return resolvedSymbol;
  }

  default Collection<${symbolNames[symbol]}> resolveAdapted${symbol}(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<${symbolNames[symbol]}> predicate){
    return Lists.newArrayList();
  }

  default void loadModelsFor${symbol}(String symbolName) {

    ${languageName}ModelLoader modelLoader = get${languageName}Language().getModelLoader();
    Set<String> calculatedModelNames = get${languageName}Language().calculateModelNamesFor${symbol}(symbolName);

    for (String calculatedModelName : calculatedModelNames) {
      if (continueWithModelLoader(calculatedModelName, modelLoader)) {
        modelLoader.loadModelsIntoScope(calculatedModelName, getModelPath(), this);
        cache(calculatedModelNames.iterator().next());
      } else {
        Log.debug("Already tried to load model for '" + symbolName + "'. If model exists, continue with cached version.", ${languageName}GlobalScope.class.getSimpleName());
      }


    }
  }
</#list>











}
