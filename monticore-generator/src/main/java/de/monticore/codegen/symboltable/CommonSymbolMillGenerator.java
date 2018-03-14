/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import de.monticore.generating.GeneratorSetup;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

public class CommonSymbolMillGenerator implements SymbolMillGenerator {

  final Map<String, String> builder = new HashMap<>();

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol, Collection<MCProdSymbol> allSymbolDefiningRules) {

    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolMill"),
      genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    final List<String> imports = getBuilderImports(genHelper, allSymbolDefiningRules, handCodedPath);

    if (grammarSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.SymbolMill", filePath, grammarSymbol.getAstNode().get(), className, builder, imports);
    }

  }

  protected List<String> getBuilderImports(SymbolTableGeneratorHelper genHelper, Collection<MCProdSymbol> symbols,
                                           IterablePath handCodedPath) {
    List<String> res = new ArrayList<>();
    for(MCProdSymbol symbol : symbols) {
      String symbolName = getSimpleName(symbol.getFullName() + "Symbol");
      String symbolBuilderName = getSimpleName(symbol.getFullName() + "SymbolBuilder");
      if (genHelper.existsHandwrittenSymbolClass(symbol, handCodedPath)) {
        symbolName += GeneratorSetup.GENERATED_CLASS_SUFFIX;
        symbolBuilderName = getSimpleName(symbol.getFullName() + "Symbol" + GeneratorSetup.GENERATED_CLASS_SUFFIX + "Builder");
      }
      List<String> qualifier = Arrays.asList(genHelper.getTargetPackage(), symbolName, symbolBuilderName);
      String importStatement = Names.getQualifiedName(qualifier);
      res.add(importStatement);

      builder.put(getSimpleName(symbol.getFullName()), symbolBuilderName);
    }
    return res;
  }
}
