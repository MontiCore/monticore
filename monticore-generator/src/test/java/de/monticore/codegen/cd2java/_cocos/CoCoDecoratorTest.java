package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CoCoDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit ast;

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "cocos", "CoCos");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    MethodDecorator methodDecorator = new MethodDecorator(glex);

    CoCoService coCoService = new CoCoService(ast);
    VisitorService visitorService = new VisitorService(ast);
    ASTService astService = new ASTService(ast);

    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, coCoService, visitorService);
    CoCoInterfaceDecorator coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, coCoService, astService);
    CoCoDecorator coCoDecorator = new CoCoDecorator(glex, coCoCheckerDecorator, coCoInterfaceDecorator);
    this.ast = coCoDecorator.decorate(ast);
  }

  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "cocos", "cocos", "_cocos");
    assertEquals(expectedPackage, ast.getPackageList());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : ast.getCDDefinition().getCDClassList()) {
      System.out.printf("==================== %s ====================\n", clazz.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
      System.out.println(sb.toString());
    }

    for (ASTCDInterface interfaze : ast.getCDDefinition().getCDInterfaceList()) {
      System.out.printf("==================== %s ====================\n", interfaze.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, interfaze, interfaze);
      System.out.println(sb.toString());
    }
  }
}
