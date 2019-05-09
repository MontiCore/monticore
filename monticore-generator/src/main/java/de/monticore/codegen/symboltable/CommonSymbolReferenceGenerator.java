/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

import java.nio.file.Path;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;

public class CommonSymbolReferenceGenerator implements SymbolReferenceGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCProdSymbol ruleSymbol, boolean isScopeSpanningSymbol) {
    final String ruleName = ruleSymbol.getSymbolDefinitionKind().isPresent() ? ruleSymbol.getSymbolDefinitionKind().get() : ruleSymbol.getName();
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleName + "SymbolReference"),
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.SymbolReference", filePath, ruleSymbol.getAstNode().get(),
              className, ruleName);
    }
  }
}
