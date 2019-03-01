package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static org.junit.Assert.*;

public class ASTWithSymbolDecoratorTest {

  private CDTypeFactory cdTypeFacade;

  private CDParameterFactory cdParameterFacade;

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass astcdClass;

  private GlobalExtensionManagement glex;

  private final static String PUBLIC = "public";

  private final static String PROTECTED = "protected";

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();

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
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator typeDecorator = new TypeCD2JavaDecorator();
    typeDecorator.decorate(cdCompilationUnit);

    glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTDecorator factoryDecorator = new ASTDecorator(glex, cdCompilationUnit);
    this.astcdClass = factoryDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(0));
  }

  @Test
  public void testClass() {
    assertEquals("ASTAutomaton", astcdClass.getName());
  }


  @Test
  public void testClassModifier() {
    assertEquals(PUBLIC, astcdClass.printModifier().trim());
  }

  @Test
  public void testAttributes() {
    assertFalse(astcdClass.isEmptyCDAttributes());
    assertEquals(5, astcdClass.sizeCDAttributes());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute attribute : astcdClass.getCDAttributeList()) {
      assertEquals(PROTECTED,attribute.printModifier().trim());
    }
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("automatonSymbol", astcdClass);
    assertEquals(PROTECTED, symbolAttribute.printModifier().trim());

    assertEquals("Optional<automaton._symboltable.AutomatonSymbol>",symbolAttribute.printType());
  }

  @Test
  public void testScopeAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("automatonScope", astcdClass);

    assertEquals(PROTECTED, symbolAttribute.printModifier().trim());
    assertEquals("Optional<automaton._symboltable.AutomatonScope>",symbolAttribute.printType());
  }

  @Test
  public void testConstructors() {
    assertFalse(astcdClass.isEmptyCDConstructors());
    assertEquals(2, astcdClass.sizeCDConstructors());
  }


  @Test
  public void testMethods() {
    assertFalse(astcdClass.isEmptyCDMethods());
    assertEquals(93, astcdClass.sizeCDMethods());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astcdClass, astcdClass);
    System.out.println(sb.toString());
  }
}
