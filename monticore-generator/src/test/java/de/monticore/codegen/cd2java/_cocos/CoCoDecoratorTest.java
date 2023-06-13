/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedAst;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "cocos", "CoCos");
    decoratedAst = createEmptyCompilationUnit(ast);
    this.glex.setGlobalValue("service", new AbstractService(ast));

    CoCoService coCoService = new CoCoService(ast);
    VisitorService visitorService = new VisitorService(ast);
    ASTService astService = new ASTService(ast);
    MCPath targetPath = Mockito.mock(MCPath.class);

    MethodDecorator methodDecorator = new MethodDecorator(glex, coCoService);

    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, coCoService, visitorService);
    CoCoInterfaceDecorator coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, coCoService, astService);
    CoCoDecorator coCoDecorator = new CoCoDecorator(glex, targetPath, coCoCheckerDecorator, coCoInterfaceDecorator);
    coCoDecorator.decorate(ast, decoratedAst);
  }

  @Test
  public void testPackage() {
    assertTrue (decoratedAst.getCDDefinition().getPackageWithName("de.monticore.codegen.cocos.cocos._cocos").isPresent());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);

    for (ASTCDClass clazz : decoratedAst.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, clazz, packageDir);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      assertTrue(parseResult.isSuccessful());
    }
    for (ASTCDInterface astcdInterface : decoratedAst.getCDDefinition().getCDInterfacesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, astcdInterface, packageDir);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      assertTrue(parseResult.isSuccessful());
    }
    
    assertTrue(Log.getFindings().isEmpty());
  }
}
