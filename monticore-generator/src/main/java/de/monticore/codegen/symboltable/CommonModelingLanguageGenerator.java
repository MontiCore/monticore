/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;

import java.nio.file.Path;
import java.util.Collection;

import static de.monticore.codegen.GeneratorHelper.existsHandwrittenClass;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

public class CommonModelingLanguageGenerator implements ModelingLanguageGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol, Collection<String> grammarRuleNames) {
    String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "Language"),
            genHelper.getTargetPackage(), handCodedPath);

    String languageName = genHelper.getGrammarSymbol().getName();

    Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    final boolean existsHW = existsHandwrittenClass(getSimpleName(grammarSymbol.getFullName() + "Language"),
            genHelper.getTargetPackage(), handCodedPath);
    
    if(grammarSymbol.getStartProd().isPresent()) {
      if(!grammarSymbol.isComponent()&&existsHW) {
        filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
      }
    }
  }
}
