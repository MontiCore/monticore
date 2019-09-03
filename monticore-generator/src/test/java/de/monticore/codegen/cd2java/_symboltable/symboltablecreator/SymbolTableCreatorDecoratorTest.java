package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class SymbolTableCreatorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symTabCreatorClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private CDTypeFacade cdTypeFacade;

  private static final String AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.AutomatonScope";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  public static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  public static final String QUALIFIED_NAMES_CALCULATOR = "de.monticore.symboltable.names.QualifiedNamesCalculator";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  public static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  public static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    String dequeType = "Deque<? extends " + I_AUTOMATON_SCOPE + ">";

    CDPrettyPrinterDelegator cdPrettyPrinterDelegator = new CDPrettyPrinterDelegator();
    String prettyprint = cdPrettyPrinterDelegator.prettyprint(cdTypeFacade.createTypeByDefinition(dequeType));
    SymbolTableCreatorDecorator decorator = new SymbolTableCreatorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit), new MethodDecorator(glex));

    //creates normal Symbol
    Optional<ASTCDClass> optSymTabCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optSymTabCreator.isPresent());
    this.symTabCreatorClass = optSymTabCreator.get();
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSymbolTableCreator", symTabCreatorClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, symTabCreatorClass.sizeInterfaces());
  }

  @Test
  public void testSuperInterface() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor", symTabCreatorClass.getInterface(0));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symTabCreatorClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(2, symTabCreatorClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symTabCreatorClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolTableCreator", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());


    assertTrue(cdConstructor.isEmptyExceptions());
  }


  @Test
  public void testConstructorWithEnclosingScope() {
    ASTCDConstructor cdConstructor = symTabCreatorClass.getCDConstructor(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolTableCreator", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());

    assertDeepEquals("Deque<? extends " + I_AUTOMATON_SCOPE + ">", cdConstructor.getCDParameter(0).getMCType());
    assertEquals("scopeStack", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(3, symTabCreatorClass.sizeCDAttributes());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", symTabCreatorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<? extends de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  }

  @Test
  public void testRealThisAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("realThis", symTabCreatorClass);
    assertDeepEquals(PRIVATE, astcdAttribute.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor", astcdAttribute.getMCType());
  }

  @Test
  public void testFirstCreatedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("firstCreatedScope", symTabCreatorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(9, symTabCreatorClass.getCDMethodList().size());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals("de.monticore.codegen.ast.automaton._ast.ASTAutomaton", method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  }


  @Test
  public void testGetFirstCreatedScopeMethod() {
    ASTCDMethod method = getMethodBy("getFirstCreatedScope", symTabCreatorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symTabCreatorClass, symTabCreatorClass);
    StaticJavaParser.parse(sb.toString());
  }
}
