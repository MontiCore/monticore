/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.GeneratorHelper.*;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

public class CommonSymbolGenerator implements SymbolGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath,
                       Optional<MCProdSymbol> prodSymbol, Optional<ASTSymbolRule> symbolRule) {
    generateSymbol(genEngine, genHelper, handCodedPath, prodSymbol, symbolRule);
  }

  protected void generateSymbol(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                                IterablePath handCodedPath,
                                Optional<MCProdSymbol> prodSymbol, Optional<ASTSymbolRule> symbolRule) {
    if (!prodSymbol.isPresent() && !symbolRule.isPresent()) {
      return;
    }
    String className;
    if (prodSymbol.isPresent()) {
       className = getSimpleName(prodSymbol.get().getSymbolDefinitionKind().isPresent()
              ? prodSymbol.get().getSymbolDefinitionKind().get()
              : prodSymbol.get().getName());
    } else {
      className = symbolRule.get().getType();
    }
    String symbolName = getSimpleTypeNameToGenerate(
            className + SYMBOL,
            genHelper.getTargetPackage(), handCodedPath);
    String builderName = getSimpleTypeNameToGenerate(
            className + SYMBOL + BUILDER,
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()),
            symbolName + ".java");
    final Path builderFilePath = get(getPathFromPackage(genHelper.getTargetPackage()),
            builderName + ".java");

    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    List<String> imports = newArrayList();
    genHelper.getAllCds(genHelper.getCd()).stream()
            .forEach(s -> imports.add(s.getFullName().toLowerCase()));
       genEngine.generateNoA("symboltable.Symbol", filePath, symbolName,
              className, symbolRule, prodSymbol.isPresent(), imports);
      genEngine.generateNoA("symboltable.SymbolBuilder", builderFilePath,
               builderName, className, symbolRule, imports);
  }

}
