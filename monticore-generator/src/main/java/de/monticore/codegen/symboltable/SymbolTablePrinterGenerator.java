/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;

public interface SymbolTablePrinterGenerator {
  
  /**
   * TODO: Write me!
   * 
   * @param genEngine
   * @param genHelper
   * @param handCodedPath
   * @param languageName
   */
  void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, String languageName);
}
