package de.monticore.codegen.cd2java.mill;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorCopyTest {

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

    ASTCDCompilationUnit copy = cdCompilationUnit.deepClone();
    MillDecorator millDecorator = new MillDecorator(new GlobalExtensionManagement());
    millDecorator.decorate(cdCompilationUnit);
    //test if not changed the original Definition
    assertDeepEquals(cdCompilationUnit, copy);
  }

  @Test
  public void testCopyWithInheritance() {
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/factory/BGrammar.mc4").getAbsolutePath()));
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

    MillDecorator millDecorator = new MillDecorator(new GlobalExtensionManagement());
    millDecorator.decorate(cdCompilationUnit);

    assertDeepEquals(cdCompilationUnit, copy);
  }

}
