/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonSymbolGenerator implements SymbolGenerator {
  
  public static final String SYMBOL_SUFFIX = "Symbol";
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol prodSymbol) {
    generateSymbol(genEngine, genHelper, handCodedPath, prodSymbol);
  }
  
  protected void generateSymbol(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCProdSymbol prodSymbol) {
    
    String className = prodSymbol.getSymbolDefinitionKind().isPresent()
        ? prodSymbol.getSymbolDefinitionKind().get()
        : prodSymbol.getName();
    String symbolName = getSimpleTypeNameToGenerate(
        getSimpleName(className + GeneratorHelper.SYMBOL),
        genHelper.getTargetPackage(), handCodedPath);
    String builderName = getSimpleTypeNameToGenerate(
        getSimpleName(className + GeneratorHelper.SYMBOL + GeneratorHelper.BUILDER),
        genHelper.getTargetPackage(), handCodedPath);
    String serializerName = getSimpleTypeNameToGenerate(
        getSimpleName(className + GeneratorHelper.SYMBOL + GeneratorHelper.SERIALIZER),
        genHelper.getTargetPackage(), handCodedPath);
    
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        symbolName + ".java");
    final Path builderFilePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        builderName + ".java");
    final Path serializerFilePath = Paths
        .get(Names.getPathFromPackage(genHelper.getTargetPackage()), serializerName + ".java");
    
    ASTMCGrammar grammar = genHelper.getGrammarSymbol().getAstGrammar().get();
    Optional<ASTSymbolRule> symbolRule = Optional.empty();
    List<String> imports = Lists.newArrayList();
    genHelper.getAllCds(genHelper.getCd()).stream()
        .forEach(s -> imports.add(s.getFullName().toLowerCase()));
    if (prodSymbol.getAstNode().isPresent() && prodSymbol.getSymbolDefinitionKind().isPresent()) {
      for (ASTSymbolRule sr : grammar.getSymbolRuleList()) {
        if (sr.getType().equals(prodSymbol.getSymbolDefinitionKind().get())) {
          symbolRule = Optional.of(sr);
          break;
        }
      }
      genEngine.generate("symboltable.Symbol", filePath, prodSymbol.getAstNode().get(), symbolName,
          prodSymbol, symbolRule, imports);
      genEngine.generate("symboltable.SymbolBuilder", builderFilePath,
          prodSymbol.getAstNode().get(), builderName, className);
      genEngine.generate("symboltable.serialization.SymbolSerialization", serializerFilePath,
          prodSymbol.getAstNode().get(), className);
    }
    
  }
  
}
