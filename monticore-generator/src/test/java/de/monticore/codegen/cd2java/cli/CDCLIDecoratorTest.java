/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDCLIDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedCD;
 
  private ASTCDCompilationUnit originalCD;

  private ASTCDCompilationUnit clonedCD;
  
 
  @Before
  public void setup() {
    originalCD = parse("de", "monticore", "codegen", "ast", "Automaton");
    clonedCD = originalCD.deepClone();
    decoratedCD = createEmptyCompilationUnit(originalCD);
    this.glex.setGlobalValue("service", new AbstractService(originalCD));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    SymbolTableService symbolTableService = new SymbolTableService(originalCD);
    ParserService parserService = new ParserService(originalCD);
    CLIDecorator cliDecorator = new CLIDecorator(glex, parserService,symbolTableService);
    CDCLIDecorator cdcliDecorator = new CDCLIDecorator(glex ,cliDecorator,parserService);
    cdcliDecorator.decorate(originalCD, decoratedCD);
  }
  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(clonedCD,originalCD);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testCDName() {
    assertEquals("Automaton", decoratedCD.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testClassCount() {
    assertEquals(1, decoratedCD.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testClassNames() {
    ASTCDClass automatonCli = getClassBy("AutomatonTool", decoratedCD);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testNoInterface() {
    assertTrue( decoratedCD.getCDDefinition().getCDInterfacesList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnum() {
    assertTrue(decoratedCD.getCDDefinition().getCDEnumsList().isEmpty());

    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testPackage() {
    assertTrue (decoratedCD.getCDDefinition().getPackageWithName("de.monticore.codegen.ast.automaton").isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testImports() {
    assertEquals(0, decoratedCD.getMCImportStatementList().size());

    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    for (ASTCDClass clazz : decoratedCD.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, clazz, packageDir);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      assertTrue(parseResult.isSuccessful());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }


}
