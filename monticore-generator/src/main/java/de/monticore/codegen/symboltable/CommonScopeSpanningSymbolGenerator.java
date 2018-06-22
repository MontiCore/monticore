/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getPackageName;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonScopeSpanningSymbolGenerator implements ScopeSpanningSymbolGenerator {

  public static final String EMPTY_SYMBOL_SUFFIX = "Symbol";

  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {

    generateScopeSpanningSymbol(genEngine, genHelper, handCodedPath, ruleSymbol);
  }

  protected void generateScopeSpanningSymbol(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleSymbol.getName() + EMPTY_SYMBOL_SUFFIX),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + GeneratorHelper.BUILDER + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.ScopeSpanningSymbol", filePath, ruleSymbol.getAstNode().get(),
          className, genHelper.getScopeClassName(ruleSymbol), ruleSymbol);
      genEngine.generate("symboltable.SymbolBuilder", builderFilePath, ruleSymbol.getAstNode().get(), className);
    }
  }

}
