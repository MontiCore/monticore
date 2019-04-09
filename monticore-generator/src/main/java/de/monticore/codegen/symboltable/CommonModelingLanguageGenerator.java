/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.existsHandwrittenClass;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

import java.nio.file.Path;
import java.util.Collection;

import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;

public class CommonModelingLanguageGenerator implements ModelingLanguageGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol, Collection<String> grammarRuleNames) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "Language"),
            genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    final boolean existsHW = existsHandwrittenClass(getSimpleName(grammarSymbol.getFullName() + "Language"),
            genHelper.getTargetPackage(), handCodedPath);
    
    genEngine.generate("symboltable.ModelingLanguage", filePath, grammarSymbol.getAstNode().get(),
            className, existsHW);
  }
}
