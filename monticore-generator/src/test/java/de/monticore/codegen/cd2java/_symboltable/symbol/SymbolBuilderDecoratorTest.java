/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertListOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.jupiter.api.Assertions.*;

public class SymbolBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass cdClass;

  private ASTCDClass builderClass;

  private MCTypeFacade mcTypeFacade;

  private static final String SYM_TYPE_EXPRESSION = "de.monticore.types.check.SymTypeExpression";

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.symboltable.cdforbuilder.symbol_builder._symboltable.ISymbol_BuilderScope";

  private static final String A_NODE_TYPE_OPT = "Optional<de.monticore.codegen.symboltable.cdforbuilder.symbol_builder._ast.ASTA>";

  private static final String A_NODE_TYPE = "de.monticore.codegen.symboltable.cdforbuilder.symbol_builder._ast.ASTA";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_STEREOTYPE_REF = "de.monticore.symboltable.stereotypes.IStereotypeReference";

  private static final String VALUE = "de.monticore.values.MCValue";

  @BeforeEach
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "Symbol_Builder");
    cdClass = getClassBy("A", ast);
    this.glex.setGlobalValue("service", new AbstractService(ast));

    AccessorDecorator methodDecorator = new AccessorDecorator(glex, new SymbolTableService(ast));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new SymbolTableService(ast));
    SymbolBuilderDecorator astNodeBuilderDecorator = new SymbolBuilderDecorator(glex, new SymbolTableService(ast),
            builderDecorator);
    this.builderClass = astNodeBuilderDecorator.decorate(cdClass);
  }

  @Test
  public void testClassName() {
    assertEquals("ASymbolBuilder", builderClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterfacesEmpty() {
    assertFalse(builderClass.isPresentCDInterfaceUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(builderClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, builderClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = builderClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("ASymbolBuilder", cdConstructor.getName());
    assertTrue(cdConstructor.isEmptyCDParameters());
    assertFalse(cdConstructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributes() {
    assertEquals(11, builderClass.getCDAttributeList().size());

    assertTrue(Log.getFindings().isEmpty());
  }

  // getters/setters that have to be Supplier-wrapped (see AccessAsSupplierTypes)
  // get additional fields. allowing to both set/get the Supplier itself and to set/get the value directly

  @Test
  public void testSymTypeAttributeAndMethods() {
    ASTCDAttribute originalAttribute = getAttributeBy("symType", cdClass);
    ASTMCType symTypeType = originalAttribute.getMCType();

    ASTCDAttribute attribute = getAttributeBy("symType", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(symTypeType), attribute.getMCType());

    ASTCDMethod getter = getMethodBy("getSymType", builderClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertDeepEquals(symTypeType, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setSymType", builderClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertDeepEquals(builderClass.getName(), setter.getMCReturnType().getMCType());
    assertEquals(1, setter.sizeCDParameters());
    assertDeepEquals(symTypeType, setter.getCDParameter(0).getMCType());
    assertEquals("symType", setter.getCDParameter(0).getName());

    ASTCDMethod supplierGetter = getMethodBy("getSymTypeSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(symTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setSymTypeSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertDeepEquals(builderClass.getName(), supplierSetter.getMCReturnType().getMCType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(symTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("symType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLSymTypeAttributeAndMethods() {
    ASTCDAttribute originalAttribute = getAttributeBy("lSymType", cdClass);
    ASTMCType lSymTypeType = originalAttribute.getMCType();

    ASTCDAttribute attribute = getAttributeBy("lSymType", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(lSymTypeType), attribute.getMCType());

    ASTCDMethod getter = getMethodBy("getLSymTypeList", builderClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertListOf(SYM_TYPE_EXPRESSION, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setLSymTypeList", builderClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertDeepEquals(builderClass.getName(), setter.getMCReturnType().getMCType());
    assertEquals(1, setter.sizeCDParameters());
    assertListOf(SYM_TYPE_EXPRESSION, setter.getCDParameter(0).getMCType());
    assertEquals("lSymType", setter.getCDParameter(0).getName());

    ASTCDMethod supplierGetter = getMethodBy("getLSymTypeListSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(lSymTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setLSymTypeListSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertDeepEquals(builderClass.getName(), supplierSetter.getMCReturnType().getMCType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(lSymTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("lSymType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOSymTypeAttributeAndMethods() {
    ASTCDAttribute originalAttribute = getAttributeBy("oSymType", cdClass);
    ASTMCType oSymTypeType = originalAttribute.getMCType();
    ASTMCType innerType = DecorationHelper.getInstance().getReferenceTypeOfOptional(oSymTypeType).getMCTypeOpt().get();

    ASTCDAttribute attribute = getAttributeBy("oSymType", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createInternalSupplierTypeOf(oSymTypeType), attribute.getMCType());

    ASTCDMethod getter = getMethodBy("getOSymType", builderClass);
    assertDeepEquals(PUBLIC, getter.getModifier());
    assertDeepEquals(innerType, getter.getMCReturnType().getMCType());
    assertTrue(getter.isEmptyCDParameters());

    ASTCDMethod isPresent = getMethodBy("isPresentOSymType", builderClass);
    assertDeepEquals(PUBLIC, isPresent.getModifier());
    assertBoolean(isPresent.getMCReturnType().getMCType());
    assertTrue(isPresent.isEmptyCDParameters());

    ASTCDMethod setter = getMethodBy("setOSymType", builderClass);
    assertDeepEquals(PUBLIC, setter.getModifier());
    assertDeepEquals(builderClass.getName(), setter.getMCReturnType().getMCType());
    assertEquals(1, setter.sizeCDParameters());
    assertDeepEquals(innerType, setter.getCDParameter(0).getMCType());
    assertEquals("oSymType", setter.getCDParameter(0).getName());

    ASTCDMethod setAbsent = getMethodBy("setOSymTypeAbsent", builderClass);
    assertDeepEquals(PUBLIC, setAbsent.getModifier());
    assertDeepEquals(builderClass.getName(), setAbsent.getMCReturnType().getMCType());
    assertTrue(setAbsent.isEmptyCDParameters());

    ASTCDMethod supplierGetter = getMethodBy("getOSymTypeSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierGetter.getModifier());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(oSymTypeType), supplierGetter.getMCReturnType().getMCType());
    assertTrue(supplierGetter.isEmptyCDParameters());

    ASTCDMethod supplierSetter = getMethodBy("setOSymTypeSupplier", builderClass);
    assertDeepEquals(PUBLIC, supplierSetter.getModifier());
    assertDeepEquals(builderClass.getName(), supplierSetter.getMCReturnType().getMCType());
    assertEquals(1, supplierSetter.sizeCDParameters());
    assertDeepEquals(DecorationHelper.getInstance().createStdSupplierTypeOf(oSymTypeType), supplierSetter.getCDParameter(0).getMCType());
    assertEquals("oSymType", supplierSetter.getCDParameter(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFullNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fullName", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("astNode", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE_OPT), astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAccessModifierAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("accessModifier", builderClass);
    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE), astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStereoinfoAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("stereoinfo", builderClass);

    assertDeepEquals(CDModifier.PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(
      mcTypeFacade.createMapTypeOf(
        mcTypeFacade.createQualifiedType(I_STEREOTYPE_REF),
        mcTypeFacade.createOptionalTypeOf(VALUE)
      ),
      astcdAttribute.getMCType()
    );

    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMethods() {
    //   previously here: 20
    //   added because of supplied fields
    //   symType   (mandatory): get+getSupplier, set+setSupplier                         =  4
    //   lSymType  (list):      18 delegate + getList + getListSupplier                  = 20
    //                          14 delegate + setList + setListSupplier                  = 16
    //   oSymType  (optional):  get+isPresent+getSupplier, set+setAbsent+setSupplier     =  6
    assertEquals(20 + 4 + 20 + 16 + 6, builderClass.getCDMethodList().size());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBuildMethod() {
    ASTCDMethod method = getMethodBy("build", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbol"), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFullNameMethod() {
    ASTCDMethod method = getMethodBy("getFullName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetAstNodeMethod() {
    ASTCDMethod method = getMethodBy("getAstNode", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testisPresentAstNodeMethod() {
    ASTCDMethod method = getMethodBy("isPresentAstNode", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetAccessModifierNameMethod() {
    ASTCDMethod method = getMethodBy("getAccessModifier", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetFullNameMethod() {
    ASTCDMethod method = getMethodBy("setFullName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("fullName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetAstNodeMethod() {
    ASTCDMethod method = getMethodBy("setAstNode", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(A_NODE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("astNode", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetAstNodeAbsentMethod() {
    ASTCDMethod method = getMethodBy("setAstNodeAbsent", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetAccessModifierMethod() {
    ASTCDMethod method = getMethodBy("setAccessModifier", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(mcTypeFacade.createQualifiedType("ASymbolBuilder"), method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(mcTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("accessModifier", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetStereoinfoMethod() {
    ASTCDMethod method = getMethodBy("getStereoinfo", builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(
      mcTypeFacade.createMapTypeOf(
        mcTypeFacade.createQualifiedType(I_STEREOTYPE_REF),
        mcTypeFacade.createOptionalTypeOf(VALUE)
      ),
      method.getMCReturnType().getMCType()
    );

    assertEquals(0, method.sizeCDParameters());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetStereoinfoMethod() {
    ASTCDMethod method = getMethodBy("setStereoinfo", builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(
      mcTypeFacade.createQualifiedType("ASymbolBuilder"),
      method.getMCReturnType().getMCType()
    );

    assertEquals(1, method.sizeCDParameters());
    assertEquals("stereoinfo", method.getCDParameter(0).getName());
    assertDeepEquals(
      mcTypeFacade.createMapTypeOf(
        mcTypeFacade.createQualifiedType(I_STEREOTYPE_REF),
        mcTypeFacade.createOptionalTypeOf(VALUE)
      ),
      method.getCDParameter(0).getMCType()
    );

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddStereoinfoWithoutValueMethod() {
    ASTCDMethod method = getMethodBy("addStereoinfo", 1, builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(
      mcTypeFacade.createQualifiedType("ASymbolBuilder"),
      method.getMCReturnType().getMCType()
    );

    assertEquals("stereotype", method.getCDParameter(0).getName());
    assertDeepEquals(
      mcTypeFacade.createQualifiedType(I_STEREOTYPE_REF),
      method.getCDParameter(0).getMCType()
    );

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddStereoinfoWithValueMethod() {
    ASTCDMethod method = getMethodBy("addStereoinfo", 2, builderClass);

    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(
      mcTypeFacade.createQualifiedType("ASymbolBuilder"),
      method.getMCReturnType().getMCType()
    );

    assertEquals(2, method.sizeCDParameters());
    assertEquals("stereotype", method.getCDParameter(0).getName());
    assertEquals("value", method.getCDParameter(1).getName());
    System.out.println(method.getCDParameter(0).getMCType().printType());
    assertDeepEquals(
      mcTypeFacade.createQualifiedType(I_STEREOTYPE_REF),
      method.getCDParameter(0).getMCType()
    );
    assertDeepEquals(
      mcTypeFacade.createQualifiedType(VALUE),
      method.getCDParameter(1).getMCType()
    );

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, builderClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
