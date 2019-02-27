//package de.monticore.codegen.cd2java.builder;
//
//import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
//import de.monticore.codegen.cd2java.factories.CDTypeFactory;
//import de.monticore.generating.templateengine.GlobalExtensionManagement;
//import de.monticore.types.TypesPrinter;
//import de.monticore.types.types._ast.ASTType;
//import de.monticore.umlcd4a.cd4analysis._ast.*;
//import de.se_rwth.commons.logging.LogStub;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//import java.util.Optional;
//
//import static de.monticore.codegen.cd2java.builder.BuilderDecoratorConstants.BUILDER_SUFFIX;
//import static junit.framework.TestCase.assertTrue;
//import static org.junit.Assert.assertEquals;
//
//public class BuilderOptionalMethodDecoratorStrategyTest {
//
//  private static final String BUILDER_CLASS_NAME = "A" + BUILDER_SUFFIX;
//
//  private static final String PUBLIC = "public";
//
//  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();
//
//  private List<ASTCDMethod> methods;
//
//  @Before
//  public void setup() {
//    LogStub.init();
//    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttributeByDefinition("protected Optional<String> s;");
//    ASTType builderType = CDTypeFactory.getInstance().createTypeByDefinition(BUILDER_CLASS_NAME);
//    BuilderOptionalMethodDecoratorStrategy decoratorStrategy = new BuilderOptionalMethodDecoratorStrategy(glex, builderType);
//    this.methods = decoratorStrategy.decorate(attribute);
//  }
//
//  @Test
//  public void testMethods() {
//    assertEquals(6, methods.size());
//  }
//
//  @Test
//  public void testGetMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "getS".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//    assertTrue(getMethod.get().getCDParameterList().isEmpty());
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals("String", getMethod.get().printReturnType());
//  }
//
//  @Test
//  public void testGetOptMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "getSOpt".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//    assertTrue(getMethod.get().getCDParameterList().isEmpty());
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals("Optional<String>", getMethod.get().printReturnType());
//  }
//
//  @Test
//  public void testIsPresentMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "isPresentS".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//    assertTrue(getMethod.get().getCDParameterList().isEmpty());
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals("boolean", getMethod.get().printReturnType());
//  }
//
//  @Test
//  public void testSetMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "setS".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//
//    assertEquals(1, getMethod.get().getCDParameterList().size());
//    ASTCDParameter parameter = getMethod.get().getCDParameter(0);
//    assertEquals("String", TypesPrinter.printType(parameter.getType()));
//    assertEquals("s", parameter.getName());
//
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals(BUILDER_CLASS_NAME, getMethod.get().printReturnType());
//  }
//
//  @Test
//  public void testSetOptMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "setSOpt".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//
//    assertEquals(1, getMethod.get().getCDParameterList().size());
//    ASTCDParameter parameter = getMethod.get().getCDParameter(0);
//    assertEquals("Optional<String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("s", parameter.getName());
//
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals(BUILDER_CLASS_NAME, getMethod.get().printReturnType());
//  }
//
//  @Test
//  public void testSetAbsentMethod() {
//    Optional<ASTCDMethod> getMethod = methods.stream().filter(m -> "setAbsentS".equals(m.getName())).findFirst();
//    assertTrue(getMethod.isPresent());
//    assertTrue(getMethod.get().getCDParameterList().isEmpty());
//    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
//    assertEquals(BUILDER_CLASS_NAME, getMethod.get().printReturnType());
//  }
//}
