package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeCD2JavaCopyTest {

  @Test
  public void testCopy() {
    //test checks that the original CompilationUnitInput has changed

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
    ASTCDCompilationUnit unit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    unit.setEnclosingScope(globalScope);

    ASTCDCompilationUnit copy = unit.deepClone();
    //make types java compatible
    TypeCD2JavaDecorator decorator = new TypeCD2JavaDecorator();
    ASTCDCompilationUnit cdCompilationUnit = decorator.decorate(unit);

    //test that the returned actually is different
    assertTrue(cdCompilationUnit.deepEquals(unit));
    assertFalse(cdCompilationUnit.deepEquals(copy));
  }
}
