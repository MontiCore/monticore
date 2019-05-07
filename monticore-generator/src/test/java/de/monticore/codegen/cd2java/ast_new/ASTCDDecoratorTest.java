package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
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
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.CD4AnalysisModelLoader;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CD4AnalysisSymbolTableCreator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ASTCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "AST");
    this.originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/de/monticore/codegen"));
    CD4AnalysisLanguage cd4aLanguage = new CD4AnalysisLanguage();
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(cd4aLanguage.getResolvingFilters());

    GlobalScope symbolTable = new GlobalScope(modelPath, cd4aLanguage, resolvingConfiguration);
    CD4AnalysisModelLoader cd4aModelLoader = new CD4AnalysisModelLoader(cd4aLanguage);

    CD4AnalysisSymbolTableCreator symbolTableCreator = cd4aModelLoader.getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, symbolTable).get();


    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(decoratedCompilationUnit);

    DataDecorator dataDecorator = new DataDecorator(glex, new MethodDecorator(glex), astService, new DataDecoratorUtil());
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, new MethodDecorator(glex), symbolTableService);
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astSymbolDecorator, astScopeDecorator, astReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex);

    MillDecorator millDecorator = new MillDecorator(glex);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex);

    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), new MethodDecorator(glex));
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, symbolTableCreator, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        millDecorator, astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    this.decoratedCompilationUnit = astcdDecorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationCopy() {
    assertNotEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testPackageChanged() {
    String packageName = decoratedCompilationUnit.getPackageList().stream().reduce((a, b) -> a + "." + b).get();
    assertEquals("de.monticore.codegen.ast.ast._ast", packageName);
  }


  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "ast", "ast", "_ast");
    assertEquals(expectedPackage, decoratedCompilationUnit.getPackageList());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassList()) {
      System.out.printf("==================== %s ====================\n", clazz.getName());
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
      System.out.println(sb.toString());
    }
  }
}
