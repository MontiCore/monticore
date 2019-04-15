package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.enums.EnumDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.factory.NodeFactoryService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ASTCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit ast;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    ASTService astService = new ASTService(ast);
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    VisitorService visitorService = new VisitorService(ast);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(ast);

    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex), new ASTService(ast), new DataDecoratorUtil());
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex);

    MillDecorator millDecorator = new MillDecorator(glex);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex);

    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        millDecorator, astConstantsDecorator, enumDecorator, astInterfaceDecorator);
    this.ast = astcdDecorator.decorate(ast);
  }

  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "ast", "ast", "_ast");
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
  }
}
