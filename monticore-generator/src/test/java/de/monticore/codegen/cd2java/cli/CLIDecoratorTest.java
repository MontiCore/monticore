/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CLIDecoratorTest extends DecoratorTestCase {
  private ASTCDClass cliClass;

  private static final String AST_AUTOMATON = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonArtifactScope";

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    CliDecorator cliDecorator = new CliDecorator(glex,  new SymbolTableService(ast));
    this.cliClass = cliDecorator.decorate(ast).get();
  }

  @Test
  public void testcreateMainMethod() {
    ASTCDMethod method = getMethodBy("main", cliClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.getCDParameterList().size());
    assertDeepEquals("String[]", method.getCDParameter(0).getMCType());

    assertEquals("args", method.getCDParameter(0).getName());
  }
}
