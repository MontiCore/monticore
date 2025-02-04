/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.ISymbolDeSer;

import java.util.Map;

/**
 * Common interface for all global scopes.
 */
public interface IGlobalScope {

  /**
   * Getter- and setter methods for the symbol path of this global scope.
   * The model path contains paths in which files with stored symbol tables
   * are located.
   */
  MCPath getSymbolPath();

  void setSymbolPath(MCPath symbolPath);

  /**
   * Getter- and setter methods for the regular expression of file extensions
   * for the (symbol) files that the global scope considers for symbol resolution
   */
  String getFileExt();

  void setFileExt(String fileExt);

  /**
   * Methods for managing a list with files that this global scope has already loaded
   * or attempted to load. This is used to avoid loading the same stored artifact scope
   * more than once.
   */
  void addLoadedFile(String name);

  void clearLoadedFiles();

  boolean isFileLoaded(String name);

  /**
   * This method initialized global scope attributes such as, e.g., the map of DeSers.
   */
  void init();

  /**
   * This method resets all state-based attributes of the global scope including
   * the lists with resolvers, the model path entries, the list of loaded files, etc.
   * This is useful, e.g., for unit testing the symbol table.
   */
  void clear();

  Map<String, ISymbolDeSer> getSymbolDeSers();

  void setSymbolDeSers(Map<String, ISymbolDeSer> symbolDeSers);

  void putSymbolDeSer(String key, ISymbolDeSer value);

  ISymbolDeSer getSymbolDeSer(String key);

  IDeSer getDeSer();

  void setDeSer(IDeSer deSer);

}
