/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests astextends and astimplements functionality at astrules
 */
public class AstRuleInheritanceTest {

  private ASTCDClass astA;

  private ASTCDInterface astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDInterface astE;

  private ASTCDClass astF;

  @BeforeClass
  public static void init() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();

    Optional<ASTCDClass> astaOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTA");
    Optional<ASTCDInterface> astbOpt = TestHelper.getCDInterface(cdCompilationUnit, "ASTB");
    Optional<ASTCDClass> astcOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTC");
    Optional<ASTCDClass> astdOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTD");
    Optional<ASTCDInterface> asteOpt = TestHelper.getCDInterface(cdCompilationUnit, "ASTE");
    Optional<ASTCDClass> astfOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTF");

    assertTrue(astaOpt.isPresent());
    assertTrue(astbOpt.isPresent());
    assertTrue(astcOpt.isPresent());
    assertTrue(astdOpt.isPresent());
    assertTrue(asteOpt.isPresent());
    assertTrue(astfOpt.isPresent());

    astA = astaOpt.get();
    astB = astbOpt.get();
    astC = astcOpt.get();
    astD = astdOpt.get();
    astE = asteOpt.get();
    astF = astfOpt.get();
  }

  /**
   * checks that for astextends on a class prod the given prod is saved as a super class
   */
  @Test
  public void testAstSuperClass() {
    assertTrue(astA.isPresentSuperclass());
    String name = typeToString(astA.getSuperclass());
    assertEquals("ASTExternalProd", name);

    assertTrue(astC.isPresentSuperclass());
    name = typeToString(astC.getSuperclass());
    assertEquals("mc2cdtransformation.AstRuleInheritance.ASTA", name);

    assertTrue(astD.isPresentSuperclass());
    name = typeToString(astD.getSuperclass());
    assertEquals("mc2cdtransformation.super.Supergrammar.ASTSuperProd", name);

    assertTrue(astF.isPresentSuperclass());
    name = typeToString(astF.getSuperclass());
    assertEquals("java.util.Observable", name);
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstSuperclass() {
    assertTrue(astA.isPresentModifier());
    assertTrue(astA.getModifier().isPresentStereotype());
    // one stereotype for the startProd flag and one for the checked external type
    assertEquals(2, astA.getModifier().getStereotype().getValueList().size());
    assertEquals(astA.getModifier().getStereotype().getValueList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astA.getModifier().getStereotype().getValueList().get(0).isPresentValue());
    assertEquals(astA.getModifier().getStereotype().getValueList().get(0).getValue(), "ASTExternalProd");

    assertTrue(astF.isPresentModifier());
    assertTrue(astF.getModifier().isPresentStereotype());
    assertEquals(1, astF.getModifier().getStereotype().getValueList().size());
    assertEquals(astF.getModifier().getStereotype().getValueList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astF.getModifier().getStereotype().getValueList().get(0).isPresentValue());
    assertEquals(astF.getModifier().getStereotype().getValueList().get(0).getValue(), "java.util.Observable");

    assertTrue(astD.isPresentModifier());
    assertTrue(astD.getModifier().isPresentStereotype());
    assertEquals(1, astD.getModifier().getStereotype().getValueList().size());
    assertEquals(astD.getModifier().getStereotype().getValueList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astD.getModifier().getStereotype().getValueList().get(0).isPresentValue());
    assertEquals(astD.getModifier().getStereotype().getValueList().get(0).getValue(), "java.io.Serializable");
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstInterfaces() {
    assertTrue(astE.isPresentModifier());
    assertTrue(astE.getModifier().isPresentStereotype());
    assertEquals(2, astE.getModifier().getStereotype().getValueList().size());
    assertEquals(astE.getModifier().getStereotype().getValueList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astE.getModifier().getStereotype().getValueList().get(0).isPresentValue());
    assertEquals(astE.getModifier().getStereotype().getValueList().get(0).getValue(), "ASTExternalInterface");
    assertEquals(astE.getModifier().getStereotype().getValueList().get(1).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astE.getModifier().getStereotype().getValueList().get(1).isPresentValue());
    assertEquals(astE.getModifier().getStereotype().getValueList().get(1).getValue(), "java.io.Serializable");
  }

  /**
   * checks for prod D, that the astextends results in a super class
   * and astimplements in super interfaces
   */
  @Test
  public void testASTExtendsAndImplements() {
    List<ASTMCObjectType> superInterfaces = astD.getInterfaceList();
    assertEquals(3, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.AstRuleInheritance.ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("java.io.Serializable", name);
  }

  /**
   * checks that the astextends keyword for interface prods (here E)
   * adds all of these to super interfaces
   */
  @Test
  public void testAstInterfaces() {
    List<ASTMCObjectType> superInterfaces = astE.getInterfaceList();
    assertEquals(4, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.AstRuleInheritance.ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("ASTExternalInterface", name);
    name = typeToString(superInterfaces.get(3));
    assertEquals("java.io.Serializable", name);
  }

}
