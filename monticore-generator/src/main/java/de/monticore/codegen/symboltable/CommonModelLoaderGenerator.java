/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;

import java.nio.file.Path;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

public class CommonModelLoaderGenerator implements ModelLoaderGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "ModelLoader"),
            genHelper.getTargetPackage(), handCodedPath);

    String languageName = genHelper.getGrammarSymbol().getName();

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

    if(grammarSymbol.getStartProd().isPresent()) {
      genEngine.generate("symboltable.ModelLoader", filePath, grammarSymbol.getAstNode().get(),
          className);
      if(!grammarSymbol.isComponent()){
        className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName())+"ModelLoaderBuilder",
            genHelper.getTargetPackage(),handCodedPath);
        filePath = get(getPathFromPackage(genHelper.getTargetPackage()),className+".java");
        genEngine.generate("symboltable.ModelLoaderBuilder", filePath, grammarSymbol.getAstNode().get(),className, languageName);

      }
    }
  }
}
