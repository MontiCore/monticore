/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.cli;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._cocos.CoCoCheckerDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLIDecoratorTest extends DecoratorTestCase {

  private ASTCDClass cliClass;

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    CLIDecorator cliDecorator = new CLIDecorator(glex, new AbstractService(ast));
    this.cliClass = cliDecorator.decorate(Lists.newArrayList(ast));
  }

  @Test
  public void testCliName() {
    assertEquals("AutomatonCli", cliClass.getName());
  }

}
