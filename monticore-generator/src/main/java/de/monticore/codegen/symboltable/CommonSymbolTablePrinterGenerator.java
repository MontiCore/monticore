/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

import java.nio.file.Path;

import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;

public class CommonSymbolTablePrinterGenerator implements SymbolTablePrinterGenerator {
  
  protected static final String TEMPLATE = "symboltable.serialization.SymbolTablePrinter";
  
  /**
   * @see de.monticore.codegen.symboltable.SymbolTablePrinterGenerator#generate(de.monticore.generating.GeneratorEngine,
   * de.monticore.codegen.symboltable.SymbolTableGeneratorHelper,
   * de.monticore.io.paths.IterablePath, java.lang.String, java.lang.String, java.lang.String,
   * java.util.Collection)
   */
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, String languageName) {
    
    String className = getSimpleTypeNameToGenerate(
        getSimpleName(languageName + "SymbolTablePrinter"),
        genHelper.getTargetPackage(), handCodedPath);
    
    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), "serialization", className + ".java");
    
    genEngine.generateNoA(TEMPLATE, filePath, languageName, className,
        genHelper.getSymbolTablePackage(), genHelper.getVisitorPackage(),
        genHelper.getAllSymbolDefiningRules());
  }
}
