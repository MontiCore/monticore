/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * TODO REPLACE ME WITH CLASS BuiltInJavaTypeSymbolResolvingDelegate IN MONTICORE-GRAMMAR MODULE!
 * (this is not available here in 6.1.0-SNAPSHOT due to bootstrapping)
 */
public class BuiltInJavaTypeSymbolResolvingDelegate implements ITypeSymbolResolvingDelegate {

  protected static TypeSymbolsGlobalScope gs;

  static{
    init();
  }

  public BuiltInJavaTypeSymbolResolvingDelegate() {
    init();
  }

  protected static void init() {
    gs = new TypeSymbolsGlobalScope(new ModelPath(),
        new TypeSymbolsLanguage("Types Symbols Language", "ts") {
          @Override public MCConcreteParser getParser() {
            Log.error("0xA0120 Type Symbols do not have a parser!");
            return null;
          }
        });

    TypeSymbolsArtifactScope javalang = new TypeSymbolsArtifactScope("java.lang",
        new ArrayList<>());
    gs.addSubScope(javalang);

    TypeSymbolsArtifactScope javautil = new TypeSymbolsArtifactScope("java.util",
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
  }

  @Override public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols,
      String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    return gs.resolveTypeMany(foundSymbols, symbolName, modifier, predicate);
  }

}
