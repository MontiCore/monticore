/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class TemporalBasisMill extends TemporalBasisMillTOP {

  /**
   * the symbol file of the temporal types,
   * e.g., de.monticore.temporal.TimePoint
   */
  protected static final String TEMPORAL_TYPES_FILE =
      "de/monticore/temporal.symtabdefinitionsym";

  /**
   * the model name of the symbol file of the temporal functions,
   * e.g., de.monticore.temporal.getYear
   */
  protected static final String TEMPORAL_FUNCTIONS_MODEL_NAME =
      "de.monticore.temporal.TemporalFunctions";

  /**
   * Provides the symbols of the temporal type system,
   * i.e., the types (e.g., de.monticore.temporal.TimePoint)
   * and the functional style operations (e.g., de.monticore.temporal.getYear)
   * of the library monticore-libraries:temporal-symbols,
   * by adding the library to the symbol path of the global scope
   * and loading the functions.
   * The types are loaded on demand, as their symbol file is found
   * by their names, the symbol file of the functions is not.
   * <p>
   * Requires the primitive types and String
   * (s. {@link BasicSymbolsMill#initializePrimitives()},
   * {@link BasicSymbolsMill#initializeString()}).
   * Like these, it has to be called again
   * after the global scope has been reset.
   */
  public static void initializeTemporalSymbols() {
    getMill()._initializeTemporalSymbols();
  }

  protected void _initializeTemporalSymbols() {
    Optional<Path> libraryPath = getTemporalSymbolsLibraryPath();
    if (libraryPath.isPresent()) {
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
      gs.getSymbolPath().addEntry(libraryPath.get());
      gs.loadFileForModelName(TEMPORAL_FUNCTIONS_MODEL_NAME);
    }
  }

  /**
   * @return the jar (or directory) containing the symbol files
   *     of the library monticore-libraries:temporal-symbols
   */
  protected Optional<Path> getTemporalSymbolsLibraryPath() {
    URL url = TemporalBasisMill.class.getClassLoader()
        .getResource(TEMPORAL_TYPES_FILE);
    if (url == null) {
      Log.error("0xFD3A2 unable to find the symbol file "
          + TEMPORAL_TYPES_FILE + " of the temporal symbols,"
          + " is the library monticore-libraries:temporal-symbols"
          + " on the classpath?");
      return Optional.empty();
    }
    try {
      if (url.getProtocol().equals("jar")) {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        return Optional.of(Path.of(connection.getJarFile().getName()));
      }
      // a directory, e.g., when running in an IDE
      Path typesFile = Path.of(url.toURI());
      int depth = Path.of(TEMPORAL_TYPES_FILE).getNameCount();
      return Optional.of(typesFile.getRoot().resolve(
          typesFile.subpath(0, typesFile.getNameCount() - depth)));
    }
    catch (IOException | URISyntaxException e) {
      Log.error("0xFD3A3 unable to access the symbol file "
          + TEMPORAL_TYPES_FILE + " of the temporal symbols at " + url, e);
      return Optional.empty();
    }
  }

}
