/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class CommonSymbolReferenceGenerator implements SymbolReferenceGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, ProdSymbol prodSymbol, boolean isScopeSpanningSymbol) {
    final String ruleName = prodSymbol.getSymbolDefinitionKind().isPresent() ? prodSymbol.getSymbolDefinitionKind().get() : prodSymbol.getName();
    String className = getSimpleTypeNameToGenerate(getSimpleName(ruleName + "SymbolReference"),
        genHelper.getTargetPackage(), handCodedPath);
    Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTSymbolRule> symbolRule = empty();
    List<String> imports = newArrayList();
    genHelper.getAllCds(genHelper.getCd()).stream()
        .forEach(s -> imports.add(s.getFullName().toLowerCase()));
    if (prodSymbol.getAstNode().isPresent() && prodSymbol.getSymbolDefinitionKind().isPresent()) {
      for (ASTSymbolRule sr : grammar.getSymbolRuleList()) {
        if (sr.getType().equals(prodSymbol.getSymbolDefinitionKind().get())) {
          symbolRule = of(sr);
          break;
        }
      }
    }

    if (prodSymbol.getAstNode().isPresent()) {
      genEngine.generate("symboltable.SymbolReference", filePath, prodSymbol.getAstNode().get(),
          className, ruleName, prodSymbol, symbolRule);

      String symbolReferenceName = className.endsWith("TOP")? className.replace("TOP","") : className;

      String languageName = genHelper.getGrammarSymbol().getName();

      className = getSimpleTypeNameToGenerate(getSimpleName(ruleName + "SymbolReferenceBuilder"),
          genHelper.getTargetPackage(), handCodedPath);
      filePath = get(getPathFromPackage(genHelper.getTargetPackage()),className+".java");

      genEngine.generate("symboltable.SymbolReferenceBuilder", filePath, prodSymbol.getAstNode().get(),
          className,languageName,symbolReferenceName);
    }

  }
}
