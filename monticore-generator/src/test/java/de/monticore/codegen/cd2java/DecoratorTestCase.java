/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.BeforeClass;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;

public abstract class DecoratorTestCase {

  private static final String MODEL_PATH = "src/test/resources/";

  @BeforeClass
  public static void setUpDecoratorTestCase() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    ICD4CodeGlobalScope globalScope = CD4CodeMill.globalScope();
    globalScope.clear();
    globalScope.setFileExt("cd");
    globalScope.setModelPath(new ModelPath(Paths.get(MODEL_PATH)));
  }

  public ASTCDCompilationUnit parse(String... names) {
    String qualifiedName = String.join("/", names);

    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTCDCompilationUnit> ast = null;
    try {
      ast = parser.parse(MODEL_PATH + qualifiedName + ".cd");
    } catch (IOException e) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    if (!ast.isPresent()) {
      fail(String.format("Failed to load model '%s'", qualifiedName));
    }
    
    // TODO: activate transformation and symbol table completer, when cd4a is ready
//    new CD4CodeAfterParseTrafo().transform(ast.get());
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast.get());
    CD4CodeSymbolTableCompleter creator = new CD4CodeSymbolTableCompleter(ast.get());
//    ast.get().accept(creator.getTraverser());
    return ast.get();
  }
}
