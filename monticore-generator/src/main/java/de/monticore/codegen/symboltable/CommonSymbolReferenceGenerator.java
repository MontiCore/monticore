/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.existsHandwrittenClass;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolReferenceGenerator implements SymbolReferenceGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol ruleSymbol, boolean isScopeSpanningSymbol) {
    final String ruleName = ruleSymbol.getSymbolDefinitionKind().isPresent()?ruleSymbol.getSymbolDefinitionKind().get():ruleSymbol.getName();
    final String className = getSimpleTypeNameToGenerate(getSimpleName(ruleName + "SymbolReference"),
        genHelper.getTargetPackage(), handCodedPath);

    boolean hwSymbolExists = existsHandwrittenClass(getSimpleName(ruleSymbol.getName() + "Symbol"), genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.SymbolReference", filePath, ruleSymbol.getAstNode().get(),
          className, ruleName, isScopeSpanningSymbol, hwSymbolExists);
    }
  }
}
