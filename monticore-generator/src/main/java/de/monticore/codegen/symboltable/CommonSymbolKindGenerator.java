/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getPackageName;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static java.nio.file.Paths.get;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

public class CommonSymbolKindGenerator implements SymbolKindGenerator {

  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, String className) {
    final String qualifiedClassName = getPackageName(genHelper.getTargetPackage(), "") + className + "Kind";

    if (existsHandwrittenClass(handCodedPath, qualifiedClassName)) {
      // Symbol kind classes are very simple and small. Hence, skip their generation
      // if handwritten class exists.
      return;
    }

    final Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + "Kind.java");
    genEngine.generateNoA("symboltable.SymbolKind", filePath, className);
  }
}
