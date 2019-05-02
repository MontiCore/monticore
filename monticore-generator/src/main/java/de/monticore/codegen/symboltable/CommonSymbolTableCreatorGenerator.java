/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import com.google.common.collect.Sets;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

public class CommonSymbolTableCreatorGenerator implements SymbolTableCreatorGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreator"),
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
  
    Set<MCProdSymbol> allSymbols = Sets.newHashSet();
    allSymbols.addAll(genHelper.getAllSymbolDefiningRules());
    allSymbols.addAll(genHelper.getAllSymbolDefiningRulesInSuperGrammar());
    List<CDSymbol> directSuperCds = genHelper.getDirectSuperCds(genHelper.getCd());
    genEngine.generate("symboltable.SymbolTableCreator", filePath, grammarSymbol.getAstNode().get(), className, directSuperCds, allSymbols
        , handCodedPath);
  }
}
