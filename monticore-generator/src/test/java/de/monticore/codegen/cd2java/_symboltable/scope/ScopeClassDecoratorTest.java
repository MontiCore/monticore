/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessAsSupplierTypes;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;


import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.jupiter.api.Assertions.*;

public class ScopeClassDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeClass;

  private ASTCDClass originalScopeRuleClass;

  private de.monticore.types.MCTypeFacade mcTypeFacade;

  private static final String SYM_TYPE_EXPRESSION = "de.monticore.types.check.SymTypeExpression";

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private GeneratorSetup generatorSetup;

  private static final String AUTOMATON_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol>";

  private static final String STATE_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol>";

  private static final String QUALIFIED_NAME_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol>";

  private static final String UNKNOWN_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.symboltable.SymbolWithScopeOfUnknownKind>";

  private static final String FOO_SYMBOL_MAP = "com.google.common.collect.LinkedListMultimap<String,de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol>";

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbol";

  private static final String STATE_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.StateSymbol";

  private static final String FOO_SYMBOL = "de.monticore.codegen.symboltable.automaton._symboltable.FooSymbol";

  private static final String UNKNOWN_SYMBOL = "de.monticore.symboltable.SymbolWithScopeOfUnknownKind";

  private static final String QUALIFIED_NAME_SYMBOL = "de.monticore.codegen.ast.lexicals._symboltable.QualifiedNameSymbol";

  private static final String AST_NODE_TYPE = "de.monticore.ast.ASTNode";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope";

  private static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  private static final String I_LEXICAS_SCOPE = "de.monticore.codegen.ast.lexicals._symboltable.ILexicalsScope";

  @BeforeEach
  public void setUp() {
    this.mcTypeFacade = mcTypeFacade.getInstance();
    ASTCDCompilationUnit astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalScopeRuleClass = getClassBy("AutomatonScope", decoratedScopeCompilationUnit);
    originalCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(astcdCompilationUnit));

    generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    CD4C.init(generatorSetup);

    ScopeClassDecorator decorator = new ScopeClassDecorator(this.glex, new SymbolTableService(astcdCompilationUnit),
        new VisitorService(decoratedSymbolCompilationUnit),
        new MethodDecorator(glex, new SymbolTableService(decoratedScopeCompilationUnit)));

    //creates normal Symbol
    this.scopeClass = decorator.decorate(decoratedScopeCompilationUnit, decoratedSymbolCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedSymbolCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScope", scopeClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(1, scopeClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals(I_AUTOMATON_SCOPE, scopeClass.getCDInterfaceUsage().getInterface(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClass() {
    assertTrue(scopeClass.isPresentCDExtendUsage());
    assertDeepEquals("de.monticore.FooScope", scopeClass.getCDExtendUsage().getSuperclass(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(4, scopeClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertTrue(cdConstructor.isEmptyCDParameters());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShadowingConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(1);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertBoolean(cdConstructor.getCDParameter(0).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(2);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShadowingAndEnclosingScopeConstructor() {
    ASTCDConstructor cdConstructor = scopeClass.getCDConstructorList().get(3);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScope", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(0).getName());

    assertBoolean(cdConstructor.getCDParameter(1).getMCType());
    assertEquals("shadowing", cdConstructor.getCDParameter(1).getName());

    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(24, scopeClass.getCDAttributeList().size());
  }

  @Test
  public void testAutomatonSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStateSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testQualifiedNameSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("qualifiedNameSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFooSymbolAlreadyResolvedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fooSymbolsAlreadyResolved", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStateSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stateSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(STATE_SYMBOL_MAP, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("automatonSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testQualifiedNameSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("qualifiedNameSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(QUALIFIED_NAME_SYMBOL_MAP, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFooSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fooSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(FOO_SYMBOL_MAP, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubScopesAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("subScopes", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertListOf(I_AUTOMATON_SCOPE, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpanningSymbolAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("spanningSymbol", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(I_SCOPE_SPANNING_SYMBOL, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShadowingAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("shadowing", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testExportsSymbolsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("exportingSymbols", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOrderedAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("ordered", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertBoolean(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(String.class, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAstNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("astNode", scopeClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertOptionalOf(AST_NODE_TYPE, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeRuleAttributes() {
    ASTCDAttribute fooAttribute = getAttributeBy("foo", scopeClass);
    assertDeepEquals(PROTECTED, fooAttribute.getModifier());
    assertListOf(String.class, fooAttribute.getMCType());
    assertFalse(AccessAsSupplierTypes.shouldHaveSupplier(fooAttribute));

    ASTCDAttribute blaAttribute = getAttributeBy("bla", scopeClass);
    assertDeepEquals(PROTECTED, blaAttribute.getModifier());
    assertOptionalOf(Integer.class, blaAttribute.getMCType());
    assertFalse(AccessAsSupplierTypes.shouldHaveSupplier(blaAttribute));

    ASTCDAttribute extraAtt = getAttributeBy("extraAttribute", scopeClass);
    assertDeepEquals(PROTECTED, extraAtt.getModifier());
    assertBoolean(extraAtt.getMCType());
    assertFalse(AccessAsSupplierTypes.shouldHaveSupplier(extraAtt));

    // symType, lSymType and oSymType are of the types listed in AccessAsSupplierTypes, so arewrapped int
    // internal Supplier type.
    ASTCDAttribute originalSymTypeAttribute = getAttributeBy("symType", originalScopeRuleClass);
    assertTrue(AccessAsSupplierTypes.shouldHaveSupplier(originalSymTypeAttribute));
    ASTCDAttribute symTypeAttribute = getAttributeBy("symType", scopeClass);
    assertDeepEquals(PROTECTED, symTypeAttribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(originalSymTypeAttribute.getMCType()),
        symTypeAttribute.getMCType());

    ASTCDAttribute originalLSymTypeAttribute = getAttributeBy("lSymType", originalScopeRuleClass);
    assertTrue(AccessAsSupplierTypes.shouldHaveSupplier(originalLSymTypeAttribute));
    ASTCDAttribute lSymTypeAttribute = getAttributeBy("lSymType", scopeClass);
    assertDeepEquals(PROTECTED, lSymTypeAttribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(originalLSymTypeAttribute.getMCType()),
        lSymTypeAttribute.getMCType());

    ASTCDAttribute originalOSymTypeAttribute = getAttributeBy("oSymType", originalScopeRuleClass);
    assertTrue(AccessAsSupplierTypes.shouldHaveSupplier(originalOSymTypeAttribute));
    ASTCDAttribute oSymTypeAttribute = getAttributeBy("oSymType", scopeClass);
    assertDeepEquals(PROTECTED, oSymTypeAttribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(originalOSymTypeAttribute.getMCType()),
        oSymTypeAttribute.getMCType());

    assertTrue(Log.getFindings().isEmpty());
  }


  // getters/setters that have to be Supplier-wrapped (see AccessAsSupplierTypes)
  // get additional fields, allowing to both set/get the Supplier itself and to set/get the value directly

  @Test
  public void testSymTypeScopeRuleMethods() {
    ASTMCType symTypeType = getAttributeBy("symType", originalScopeRuleClass).getMCType();

    ASTCDMethod getter = getMethodBy("getSymType", scopeClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertDeepEquals(symTypeType, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setSymType", scopeClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertTrue(setter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, setter.sizeCDParameters());
    assertDeepEquals(symTypeType, setter.getCDParameter(0).getMCType());
    assertEquals("symType", setter.getCDParameter(0).getName());

    ASTCDMethod supplierGetter = getMethodBy("getSymTypeSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(symTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setSymTypeSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertTrue(supplierSetter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(symTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("symType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLSymTypeScopeRuleMethods() {
    ASTMCType lSymTypeType = getAttributeBy("lSymType", originalScopeRuleClass).getMCType();

    ASTCDMethod getter = getMethodBy("getLSymTypeList", scopeClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertListOf(SYM_TYPE_EXPRESSION, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setLSymTypeList", scopeClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertTrue(setter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, setter.sizeCDParameters());
    assertListOf(SYM_TYPE_EXPRESSION, setter.getCDParameter(0).getMCType());
    assertEquals("lSymType", setter.getCDParameter(0).getName());

    ASTCDMethod supplierGetter = getMethodBy("getLSymTypeListSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(lSymTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setLSymTypeListSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertTrue(supplierSetter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(lSymTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("lSymType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOSymTypeScopeRuleMethods() {
    ASTCDAttribute originalOSymTypeAttribute = getAttributeBy("oSymType", originalScopeRuleClass);
    ASTMCType oSymTypeType = originalOSymTypeAttribute.getMCType();
    ASTMCType innerType = DecorationHelper.getInstance().getReferenceTypeOfOptional(oSymTypeType).getMCTypeOpt().get();

    ASTCDMethod getter = getMethodBy("getOSymType", scopeClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertDeepEquals(innerType, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod isPresent = getMethodBy("isPresentOSymType", scopeClass);
    assertDeepEquals(PUBLIC, isPresent.getModifier());
    assertBoolean(isPresent.getMCReturnType().getMCType());
    assertTrue(isPresent.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setOSymType", scopeClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertTrue(setter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, setter.sizeCDParameters());
    assertDeepEquals(innerType, setter.getCDParameter(0).getMCType());
    assertEquals("oSymType", setter.getCDParameter(0).getName());

    ASTCDMethod setAbsent = getMethodBy("setOSymTypeAbsent", scopeClass);
    assertDeepEquals(PUBLIC, setAbsent.getModifier());
    assertTrue(setAbsent.getMCReturnType().isPresentMCVoidType());
    assertTrue(setAbsent.isEmptyCDParameters());

    ASTCDMethod supplierGetter = getMethodBy("getOSymTypeSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(oSymTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setOSymTypeSupplier", scopeClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertTrue(supplierSetter.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(oSymTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("oSymType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    //   previously here:                                                                =104
    //   added because of supplied fields
    //   symType   (mandatory): get+getSupplier, set+setSupplier                         =  4
    //   lSymType  (list):      18 delegate + getList + getListSupplier                  = 20
    //                          14 delegate + setList + setListSupplier                  = 16
    //   oSymType  (optional):  get+isPresent+getSupplier, set+setAbsent+setSupplier     =  6
    assertEquals(104 + 4 + 20 + 16 + 6, scopeClass.getCDMethodList().size());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("remove", scopeClass);

    Map<String, ASTCDMethod> methods = new LinkedHashMap<>();
    methodList.forEach(l -> methods.put(
            CD4CodeMill.prettyPrint(l.getCDParameter(0).getMCType(), false), l)
    );

    assertEquals(5, methodList.size());

    ASTCDMethod automatonRemove = methods.get(AUTOMATON_SYMBOL);
    assertDeepEquals(PUBLIC, automatonRemove.getModifier());
    assertTrue(automatonRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonRemove.sizeCDParameters());
    assertEquals("symbol", automatonRemove.getCDParameter(0).getName());

    ASTCDMethod stateRemove = methods.get(STATE_SYMBOL);
    assertDeepEquals(PUBLIC, stateRemove.getModifier());
    assertTrue(stateRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateRemove.sizeCDParameters());
    assertEquals("symbol", stateRemove.getCDParameter(0).getName());

    ASTCDMethod qualifiedNameRemove = methods.get(QUALIFIED_NAME_SYMBOL);
    assertDeepEquals(PUBLIC, qualifiedNameRemove.getModifier());
    assertTrue(qualifiedNameRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, qualifiedNameRemove.sizeCDParameters());
    assertEquals("symbol", qualifiedNameRemove.getCDParameter(0).getName());

    ASTCDMethod unknownRemove = methods.get(UNKNOWN_SYMBOL);
    assertDeepEquals(PUBLIC, unknownRemove.getModifier());
    assertTrue(unknownRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, unknownRemove.sizeCDParameters());
    assertEquals("symbol", unknownRemove.getCDParameter(0).getName());

    ASTCDMethod fooRemove = methods.get(FOO_SYMBOL);
    assertDeepEquals(PUBLIC, fooRemove.getModifier());
    assertTrue(fooRemove.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, fooRemove.sizeCDParameters());
    assertEquals("symbol", fooRemove.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddSymbolMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("add", scopeClass);
    Map<String, ASTCDMethod> methods = new LinkedHashMap<>();
    methodList.forEach(l -> methods.put(
        CD4CodeMill.prettyPrint(l.getCDParameter(0).getMCType(), false), l)
    );

    assertEquals(5, methodList.size());

    ASTCDMethod automatonAdd = methods.get(AUTOMATON_SYMBOL);
    assertDeepEquals(PUBLIC, automatonAdd.getModifier());
    assertTrue(automatonAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, automatonAdd.sizeCDParameters());
    assertEquals("symbol", automatonAdd.getCDParameter(0).getName());

    ASTCDMethod stateAdd = methods.get(STATE_SYMBOL);
    assertDeepEquals(PUBLIC, stateAdd.getModifier());
    assertTrue(stateAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, stateAdd.sizeCDParameters());
    assertEquals("symbol", stateAdd.getCDParameter(0).getName());

    ASTCDMethod qualifiedNameAdd = methods.get(QUALIFIED_NAME_SYMBOL);
    assertDeepEquals(PUBLIC, qualifiedNameAdd.getModifier());
    assertTrue(qualifiedNameAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, qualifiedNameAdd.sizeCDParameters());
    assertEquals("symbol", qualifiedNameAdd.getCDParameter(0).getName());

    ASTCDMethod unknownAdd = methods.get(UNKNOWN_SYMBOL);
    assertDeepEquals(PUBLIC, unknownAdd.getModifier());
    assertTrue(unknownAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, unknownAdd.sizeCDParameters());
    assertEquals("symbol", unknownAdd.getCDParameter(0).getName());

    ASTCDMethod fooAdd = methods.get(FOO_SYMBOL);
    assertDeepEquals(PUBLIC, fooAdd.getModifier());
    assertTrue(fooAdd.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, fooAdd.sizeCDParameters());
    assertEquals("symbol", fooAdd.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetAutomatonSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getAutomatonSymbols", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_MAP, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSymbolWithSubKindsMethod() {
    List<Predicate<ASTCDMethod>> predicates = Arrays.asList(
            m -> m.getName().endsWith("WithSubKinds")
    );
    List<ASTCDMethod> methodList = getMethodsBy(scopeClass.getCDMethodList(), predicates);
    Map<String, ASTCDMethod> methods = new HashMap<>();
    methodList.forEach(l -> methods.put(
            CD4CodeMill.prettyPrint(l.getMCReturnType().getMCType(), false), l)
    );

    assertEquals(5, methodList.size());

    ASTCDMethod automatonAdd = methods.get(AUTOMATON_SYMBOL_MAP);
    assertDeepEquals(PUBLIC, automatonAdd.getModifier());
    assertTrue(automatonAdd.isEmptyCDParameters());
    assertEquals("getAutomatonSymbolsWithSubKinds", automatonAdd.getName());

    ASTCDMethod stateAdd = methods.get(STATE_SYMBOL_MAP);
    assertDeepEquals(PUBLIC, stateAdd.getModifier());
    assertTrue(stateAdd.isEmptyCDParameters());
    assertEquals("getStateSymbolsWithSubKinds", stateAdd.getName());

    ASTCDMethod qualifiedNameAdd = methods.get(QUALIFIED_NAME_SYMBOL_MAP);
    assertDeepEquals(PUBLIC, qualifiedNameAdd.getModifier());
    assertTrue(qualifiedNameAdd.isEmptyCDParameters());
    assertEquals("getQualifiedNameSymbolsWithSubKinds", qualifiedNameAdd.getName());

    ASTCDMethod unknownAdd = methods.get(UNKNOWN_SYMBOL_MAP);
    assertDeepEquals(PUBLIC, unknownAdd.getModifier());
    assertTrue(unknownAdd.isEmptyCDParameters());
    assertEquals("getUnknownSymbolsWithSubKinds", unknownAdd.getName());

    ASTCDMethod fooAdd = methods.get(FOO_SYMBOL_MAP);
    assertDeepEquals(PUBLIC, fooAdd.getModifier());
    assertTrue(fooAdd.isEmptyCDParameters());
    assertEquals("getFooSymbolsWithSubKinds", fooAdd.getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetStateSymbolsMethod() {
    ASTCDMethod method = getMethodBy("getStateSymbols", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(STATE_SYMBOL_MAP, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isStateSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("isAutomatonSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetAutomatonSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setAutomatonSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("automatonSymbolsAlreadyResolved", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetStateSymbolsAlreadyResolvedMethod() {
    ASTCDMethod method = getMethodBy("setStateSymbolsAlreadyResolved", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("stateSymbolsAlreadyResolved", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("getSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_SCOPE_SPANNING_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testisPresentSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetSpanningSymbolMethod() {
    ASTCDMethod method = getMethodBy("setSpanningSymbol", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(I_SCOPE_SPANNING_SYMBOL, method.getCDParameter(0).getMCType());
    assertEquals("spanningSymbol", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetSpanningSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setSpanningSymbolAbsent", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddSubScopeAutomatonMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("addSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveSubScopeAutomatonMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("removeSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testSetSubScopesAutomatonMethod() {
    ASTCDMethod method = getMethodBy("setSubScopes", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(I_AUTOMATON_SCOPE, method.getCDParameter(0).getMCType());
    assertEquals("subScopes", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetSubScopessMethod() {
    ASTCDMethod method = getMethodBy("getSubScopes", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(I_AUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddSubScopeLexicalsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("addSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveSubScopeLexicalsMethod() {
    List<ASTCDMethod> methodList = getMethodsBy("removeSubScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("subScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetEnclosingScopeAutomatonMethod() {

    List<ASTCDMethod> methodList = getMethodsBy("setEnclosingScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_AUTOMATON_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetEnclosingScopeLexicalsMethod() {

    List<ASTCDMethod> methodList = getMethodsBy("setEnclosingScope", scopeClass);
    assertEquals(2, methodList.size());
    Optional<ASTCDMethod> method = methodList.stream()
        .filter(m -> m.sizeCDParameters() == 1)
        .filter(m -> m.getCDParameter(0).getMCType().deepEquals(mcTypeFacade.createQualifiedType(I_LEXICAS_SCOPE)))
        .findFirst();
    assertTrue(method.isPresent());

    assertDeepEquals(PUBLIC, method.get().getModifier());
    assertTrue(method.get().getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.get().sizeCDParameters());
    assertDeepEquals(I_LEXICAS_SCOPE, method.get().getCDParameter(0).getMCType());
    assertEquals("newEnclosingScope", method.get().getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("isExtraAttribute", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetExtraAttributeMethod() {
    ASTCDMethod method = getMethodBy("setExtraAttribute", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("extraAttribute", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFooListMethod() {
    ASTCDMethod method = getMethodBy("getFooList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertListOf(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetFooListMethod() {
    ASTCDMethod method = getMethodBy("setFooList", scopeClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertListOf(String.class, method.getCDParameter(0).getMCType());
    assertEquals("foo", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeRuleMethod() {
    ASTCDMethod method = getMethodBy("toString", scopeClass);

    assertTrue(method.getModifier().isPublic());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, scopeClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAcceptMethods() {
    List<ASTCDMethod> methods = getMethodsBy("accept", scopeClass);

    assertEquals(2, methods.size());

    methods.forEach(method -> {
      assertDeepEquals(PUBLIC, method.getModifier());
      assertVoid(method.getMCReturnType().getMCVoidType());
      assertEquals(1, method.getCDParameterList().size());
      assertEquals("visitor", method.getCDParameter(0).getName());
      assertTrue(method.getCDParameter(0).getMCType().printType().endsWith("Traverser"));
    });
  }

}
