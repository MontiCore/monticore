/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.typesymbols._symboltable;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This resolving delegate can be integrated into any global scopes to find built in Java types such as,
 * e.g., "boolean" or commonly used Java types such as "java.lang.Boolean".
 */
public class BuiltInJavaTypeSymbolResolvingDelegate implements ITypeSymbolResolvingDelegate {

  protected static TypeSymbolsGlobalScope gs;

  public BuiltInJavaTypeSymbolResolvingDelegate() {
    init();
  }

  protected static void init() {
    gs = new TypeSymbolsGlobalScope(new ModelPath(),
        new TypeSymbolsLanguage("Types Symbols Language", "ts") {
          @Override public MCConcreteParser getParser() {
            Log.error("0xTODO Type Symbols do not have a parser!");
            return null;
          }
        });

    TypeSymbolsArtifactScope javalang = new TypeSymbolsArtifactScope("java.lang",
        new ArrayList<>());    gs.addSubScope(javalang);

    TypeSymbolsArtifactScope javautil = new TypeSymbolsArtifactScope("java.lang",
        new ArrayList<>());
    gs.addSubScope(javautil);

    javalang.add(new TypeSymbol("String"));
    gs.add(new TypeSymbol("String"));

    javalang.add(new TypeSymbol("Boolean"));
    gs.add(new TypeSymbol("boolean"));

    javalang.add(new TypeSymbol("Integer"));
    gs.add(new TypeSymbol("int"));

    javalang.add(new TypeSymbol("Float"));
    gs.add(new TypeSymbol("float"));

    javalang.add(new TypeSymbol("Double"));
    gs.add(new TypeSymbol("double"));

    javalang.add(new TypeSymbol("Long"));
    gs.add(new TypeSymbol("long"));

    javautil.add(new TypeSymbol("List"));
    javautil.add(new TypeSymbol("Optional"));

    //TODO complete me with other built in types
  }

  @Override public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols,
      String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    return gs.resolveTypeMany(foundSymbols, symbolName, modifier, predicate);
  }

}
