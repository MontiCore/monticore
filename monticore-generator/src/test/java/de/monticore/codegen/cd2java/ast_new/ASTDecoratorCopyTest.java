package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ASTDecoratorCopyTest {

  @Test
  public void testCopy() {
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    ASTCDCompilationUnit cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);

    //make copy before decoration to compare after
    ASTCDCompilationUnit copy = cdCompilationUnit.deepClone();

    ASTDecorator astDecorator = new ASTDecorator(new GlobalExtensionManagement(), cdCompilationUnit);
    for(ASTCDClass astcdClass : cdCompilationUnit.getCDDefinition().getCDClassList()){
      astDecorator.decorate(astcdClass);
    }

    assertTrue(cdCompilationUnit.deepEquals(copy));
  }
}
