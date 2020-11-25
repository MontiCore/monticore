/* (c) https://github.com/MontiCore/monticore */
/*
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable;

import de.monticore.io.paths.ModelPath;

/**
 * Common interface for all global scopes.
 */
public interface IGlobalScope {

  /**
   * Getter- and setter methods for the model path of this global scope.
   * The model path contains paths in which files with stored symbol tables
   * are located.
   */
  ModelPath getModelPath();
  void setModelPath(ModelPath modelPath);

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
   * This methods resets all state-based attributes of the global scope including
   * the lists with resolvers, the model path entries, the list of loaded files, etc.
   * This is useful, e.g., for unit testing the symbol table.
   */
  void clear();
}
