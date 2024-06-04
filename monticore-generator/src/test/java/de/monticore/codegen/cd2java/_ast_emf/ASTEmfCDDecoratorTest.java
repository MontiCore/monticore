/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.ASTFullEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.DataEmfDecorator;
import de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageImplDecorator;
import de.monticore.codegen.cd2java._ast_emf.emf_package.PackageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast_emf.enums.EmfEnumDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ASTEmfCDDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    this.decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "AST");
    this.originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new EmfService(decoratedCompilationUnit));

    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    EmfService emfService = new EmfService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    EmfMutatorDecorator emfMutatorDecorator = new EmfMutatorDecorator(glex, astService);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(glex, methodDecorator, astService, new DataDecoratorUtil(), emfMutatorDecorator);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTEmfDecorator astEmfDecorator = new ASTEmfDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService, emfService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullEmfDecorator fullEmfDecorator = new ASTFullEmfDecorator(dataEmfDecorator, astEmfDecorator, astClassReferencedSymbolDecorator);

    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(glex, astService, visitorService);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);


    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);

    EmfEnumDecorator emfEnumDecorator = new EmfEnumDecorator(glex, new AccessorDecorator(glex, astService), astService);

    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);

    PackageImplDecorator packageImplDecorator = new PackageImplDecorator(glex, new MandatoryAccessorDecorator(glex), emfService);
    PackageInterfaceDecorator packageInterfaceDecorator = new PackageInterfaceDecorator(glex, emfService);

    ASTEmfCDDecorator astcdDecorator = new ASTEmfCDDecorator(glex, fullEmfDecorator, astLanguageInterfaceDecorator, astBuilderDecorator,
          astConstantsDecorator, emfEnumDecorator, fullASTInterfaceDecorator, packageImplDecorator, packageInterfaceDecorator);
    this.decoratedCompilationUnit = astcdDecorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationCopy() {
    assertNotEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertTrue (decoratedCompilationUnit.getCDDefinition().getPackageWithName("de.monticore.codegen.ast.ast._ast").isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassesList()) {
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
