/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.*;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast.mill.MillDecorator;
import de.monticore.codegen.cd2java._ast.mill.MillForSuperDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;


public class ASTCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    LogStub.enableFailQuick(false);
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    this.decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "AST");
    this.originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), new ASTService(decoratedCompilationUnit));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);

    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex, nodeFactoryService);

    MillDecorator millDecorator = new MillDecorator(glex, astService, visitorService);

    MillForSuperDecorator millForSuperDecorator = new MillForSuperDecorator(glex, astService);

    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);

    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        millDecorator, millForSuperDecorator, astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
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
  public void testNewModifierObjects(){
    // test that the modifier of AST classes have a different object ID
    ASTCDClass originalClass = getClassBy("A", originalCompilationUnit);
    ASTCDClass decoratedClass = getClassBy("A", decoratedCompilationUnit);

    assertTrue(originalClass.isPresentModifier());
    assertTrue(decoratedClass.isPresentModifier());

    assertNotEquals(originalClass.getModifier().toString(), decoratedClass.getModifier().toString());
    assertNotEquals(originalClass.getModifier(), decoratedClass.getModifier());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
      // test parsing
      ParserConfiguration configuration = new ParserConfiguration();
      JavaParser parser = new JavaParser(configuration);
      ParseResult parseResult = parser.parse(sb.toString());
      assertTrue(parseResult.isSuccessful());
    }
  }
}
