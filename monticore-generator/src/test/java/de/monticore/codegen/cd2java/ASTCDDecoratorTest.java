package de.monticore.codegen.cd2java;

import de.monticore.codegen.cd2java.ast_new.*;
import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.mill.MillDecorator;
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

    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex));
    ASTDecorator astDecorator = new ASTDecorator(glex, ast);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, ast, new MethodDecorator(glex));
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, ast, new MethodDecorator(glex));
    ASTReferencedSymbolDecorator astReferencedSymbolDecorator = new ASTReferencedSymbolDecorator(glex, new ReferencedSymbolAccessorDecorator(glex));
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new MethodDecorator(glex));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex);

    MillDecorator millDecorator = new MillDecorator(glex);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(fullDecorator, astBuilderDecorator, nodeFactoryDecorator, millDecorator);
    this.ast = astcdDecorator.decorate(ast);
  }

  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "ast", "_ast");
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
