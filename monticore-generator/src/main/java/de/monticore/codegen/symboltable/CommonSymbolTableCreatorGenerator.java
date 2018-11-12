/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

public class CommonSymbolTableCreatorGenerator implements SymbolTableCreatorGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreator"),
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    List<CDSymbol> directSuperCds = genHelper.getDirectSuperCds(genHelper.getCd());
    genEngine.generate("symboltable.SymbolTableCreator", filePath, grammarSymbol.getAstNode().get(), className, directSuperCds,
            grammarSymbol.getProds(), handCodedPath);
  }
}
