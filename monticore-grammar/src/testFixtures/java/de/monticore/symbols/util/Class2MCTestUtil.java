// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.util;

import de.monticore.class2mc.Class2MCResolver;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * Utility class to ease Class2MC usage in Tests.
 */
public class Class2MCTestUtil {

  /**
   * adds Class2MC as a resolver for OOSymbols
   * with default settings.
   */
  static public void initializeClass2MC4OOSymbols() {
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    OOClass2MCResolver resolver = new OOClass2MCResolver();
    globalScope.addAdaptedOOTypeSymbolResolver(resolver);
    globalScope.addAdaptedTypeSymbolResolver(resolver);
  }

  /**
   * adds Class2MC as a resolver for BasicSymbols
   * with default settings.
   * <p>
   * You don't need this if you already used
   * {@link #initializeClass2MC4OOSymbols()}.
   */
  static public void initializeClass2MC4BasicSymbols() {
    IBasicSymbolsGlobalScope globalScope = BasicSymbolsMill.globalScope();
    Class2MCResolver resolver = new Class2MCResolver();
    globalScope.addAdaptedTypeSymbolResolver(resolver);
  }

  /**
   * Adds the path of the given class to the symbol path of the global scope.
   * This is used to allow Class2MC to find specific classes.
   *
   * @param clazz the class that should be available using Class2MC
   */
  static public void addClassPathEntry(Class<?> clazz) {
    try {
      CodeSource codeSource = clazz
          .getProtectionDomain()
          .getCodeSource();
      if (codeSource == null) {
        throw new IllegalArgumentException(
            clazz.getCanonicalName()
                + " has no accessible code source"
        );
      }
      Path classPath = Paths.get(codeSource.getLocation().toURI());
      BasicSymbolsMill.globalScope().getSymbolPath().addEntry(classPath);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
