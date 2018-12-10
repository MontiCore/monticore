/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getPackageName;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static java.nio.file.Paths.get;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonResolvingFilterGenerator implements ResolvingFilterGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCProdSymbol ruleSymbol) {
    final String ruleName = ruleSymbol.getSymbolDefinitionKind().isPresent() ? ruleSymbol.getSymbolDefinitionKind().get() : ruleSymbol.getName();
    final String className = ruleName + "ResolvingFilter";
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className;

    if (existsHandwrittenClass(handCodedPath, qualifiedClassName)) {
      // ResolvingFilter classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    if (ruleSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.ResolvingFilter", filePath, ruleSymbol.getAstNode().get(), className, ruleName);
    }
  }
}
