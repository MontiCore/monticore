/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
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
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CDAuxiliaryDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");

    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    ParserService parserService = new ParserService(originalCompilationUnit);
    MillForSuperDecorator millForSuperDecorator = new MillForSuperDecorator(glex, symbolTableService, visitorService, parserService);

    CDAuxiliaryDecorator cdAuxiliaryDecorator = new CDAuxiliaryDecorator(glex, millForSuperDecorator);
    decoratedCompilationUnit = getASTCD();
    cdAuxiliaryDecorator.decorate(originalCompilationUnit, decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getASTCD() {
    ASTService astService = new ASTService(originalCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);
    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(glex, astService, visitorService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), new ASTService(originalCompilationUnit));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);
    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);
    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);
    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator,
        astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    return astcdDecorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testPackageName() {
    Assert.assertTrue (decoratedCompilationUnit.getCDDefinition().getPackageWithName("de.monticore.codegen.symboltable.automaton._auxiliary").isPresent());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefinitionName() {
    assertEquals("Automaton", decoratedCompilationUnit.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSize() {
    Optional<ASTCDPackage> p = decoratedCompilationUnit.getCDDefinition().getCDPackagesList().stream()
            .filter(pp -> "de.monticore.codegen.symboltable.automaton._auxiliary".equals(pp.getName())).findAny();
    assertTrue (p.isPresent());
    assertEquals(1, p.get().getCDElementList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMillForSuperClass() {
    ASTCDClass automatonMill = getClassBy("LexicalsMillForAutomaton", decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
