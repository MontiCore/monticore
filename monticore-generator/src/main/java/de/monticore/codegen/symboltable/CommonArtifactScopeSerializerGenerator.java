package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonArtifactScopeSerializerGenerator implements ArtifactScopeSerializerGenerator {
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
      IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    final String className = getSimpleTypeNameToGenerate(
        getSimpleName(grammarSymbol.getFullName() + "ArtifactScope" + GeneratorHelper.SERIALIZER),
        genHelper.getTargetPackage(), handCodedPath);
    final String languageName = getSimpleTypeNameToGenerate(
        getSimpleName(grammarSymbol.getFullName()),
        genHelper.getTargetPackage(), handCodedPath);
    
    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()),
        className + ".java");
    
    genEngine.generateNoA("symboltable.serialization.ArtifactScopeSerializer", filePath, languageName,
        GeneratorHelper.SERIALIZER,
        grammarSymbol.getProds());
  } 
  
}
