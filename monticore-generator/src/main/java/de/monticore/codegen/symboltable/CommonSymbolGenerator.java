/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.GeneratorHelper.BUILDER;
import static de.monticore.codegen.GeneratorHelper.DESER;
import static de.monticore.codegen.GeneratorHelper.SYMBOL;
import static de.monticore.codegen.GeneratorHelper.RESOLVING;
import static de.monticore.codegen.GeneratorHelper.DELEGATE;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;

public class CommonSymbolGenerator implements SymbolGenerator {

  public static final String SYMBOL_SUFFIX = "Symbol";

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, ProdSymbol prodSymbol, boolean isScopeSpanningSymbol) {
    generateSymbol(genEngine, genHelper, handCodedPath, prodSymbol, isScopeSpanningSymbol);
  }

  protected void generateSymbol(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                                IterablePath handCodedPath, ProdSymbol prodSymbol, boolean isScopeSpanningSymbol) {

    String className = prodSymbol.getName();
    String symbolName = getSimpleTypeNameToGenerate(
            getSimpleName(className + SYMBOL),
            genHelper.getTargetPackage(), handCodedPath);
    String builderName = getSimpleTypeNameToGenerate(
            getSimpleName(className + SYMBOL + BUILDER),
            genHelper.getTargetPackage(), handCodedPath);
    String deserName = getSimpleTypeNameToGenerate(
        getSimpleName(className + SYMBOL + DESER),
        genHelper.getSerializationTargetPackage(), handCodedPath);
    String delegateName = getSimpleTypeNameToGenerate(
            getSimpleName("I" + className + SYMBOL + RESOLVING + DELEGATE),
            genHelper.getSerializationTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()),
            symbolName + ".java");
    final Path builderFilePath = get(getPathFromPackage(genHelper.getTargetPackage()),
            builderName + ".java");
    final Path serializationFilePath = get(getPathFromPackage(genHelper.getSerializationTargetPackage()),
            deserName +".java");
    final Path delegateFilePath = get(getPathFromPackage(genHelper.getTargetPackage()),
            delegateName +".java");

    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTSymbolRule> symbolRule = empty();
    List<String> imports = newArrayList();
    genHelper.getAllCds(genHelper.getCd()).stream()
            .forEach(s -> imports.add(s.getFullName().toLowerCase()));
    if (prodSymbol.getAstNode().isPresent() && prodSymbol.isSymbolDefinition()) {
      for (ASTSymbolRule sr : grammar.getSymbolRuleList()) {
        if (sr.getType().equals(prodSymbol.getName())) {
          symbolRule = of(sr);
          break;
        }
      }
      genEngine.generate("symboltable.Symbol", filePath, prodSymbol.getAstNode().get(), symbolName,
              prodSymbol, symbolRule, imports, isScopeSpanningSymbol);
      genEngine.generate("symboltable.SymbolBuilder", builderFilePath,
              prodSymbol.getAstNode().get(), builderName, className, symbolRule, imports, prodSymbol);
      genEngine.generate("symboltable.serialization.SymbolDeSer", serializationFilePath,
          prodSymbol.getAstNode().get(), genHelper.getGrammarSymbol().getName(), deserName, className, symbolRule);
      genEngine.generate("symboltable.SymbolDelegateInterface", delegateFilePath,
              prodSymbol.getAstNode().get(), delegateName, className);
    }

  }

}