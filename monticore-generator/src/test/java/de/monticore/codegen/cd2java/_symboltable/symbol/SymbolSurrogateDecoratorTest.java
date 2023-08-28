/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymbolSurrogateDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassFoo;

  private MCTypeFacade mcTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.IAutomatonSymbolCDScope";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automatonsymbolcd._symboltable.AutomatonSymbol";

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolSurrogateDecorator decorator = new SymbolSurrogateDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedCompilationUnit)),
        new MandatoryMutatorSymbolSurrogateDecorator(glex));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("Automaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);
    //creates fooSymbolRef
    ASTCDClass fooClass = getClassBy("Foo", decoratedCompilationUnit);
    this.symbolClassFoo = decorator.decorate(fooClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolSurrogate", symbolClassAutomaton.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals(AUTOMATON_SYMBOL, symbolClassAutomaton.getCDExtendUsage().getSuperclass(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symbolClassAutomaton.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symbolClassAutomaton.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolSurrogate", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("name", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(1, symbolClassAutomaton.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLoadedSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("delegate", symbolClassAutomaton);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(AUTOMATON_SYMBOL, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(9, symbolClassAutomaton.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("name", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testLoadSymbolMethod() {
    ASTCDMethod method = getMethodBy("lazyLoadDelegate", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFullNameMethod() {
    ASTCDMethod method = getMethodBy("getFullName", symbolClassAutomaton);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, symbolClassAutomaton, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGeneratedCodeFoo() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, symbolClassFoo, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
