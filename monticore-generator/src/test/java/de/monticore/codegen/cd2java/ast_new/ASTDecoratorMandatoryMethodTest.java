package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ASTDecoratorMandatoryMethodTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  private List<ASTCDMethod> methods;

  private GlobalExtensionManagement glex;

  private static final String PUBLIC = "public";

  private static final String VOID = "void";

  private static final String STRING = "String";

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/ast/ASTTest.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator typeDecorator = new TypeCD2JavaDecorator();
    cdCompilationUnit = typeDecorator.decorate(cdCompilationUnit);

    ASTDecorator factoryDecorator = new ASTDecorator(glex, cdCompilationUnit);
    this.methods = factoryDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(0)).getCDMethodList();
  }

  @Test
  public void testClass() {
    assertEquals("ASTMand", cdCompilationUnit.getCDDefinition().getCDClass(0).getName());
  }

  @Test
  public void testMethods() {
    assertEquals(13, methods.size());
  }

  @Test
  public void testGetMethod() {
    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "getName".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(STRING, getMethod.get().printReturnType());
  }

  @Test
  public void testSetMethod() {
    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "setName".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());

    assertEquals(1, getMethod.get().getCDParameterList().size());
    ASTCDParameter parameter = getMethod.get().getCDParameter(0);
    assertEquals(STRING, TypesPrinter.printType(parameter.getType()));
    assertEquals("name", parameter.getName());

    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(VOID, getMethod.get().printReturnType());
  }
}
