/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.*;

/**
 * tests astextends and astimplements functionality at astrules
 */
public class AstRuleInheritanceTest extends TranslationTestCase {

  private ASTCDClass astA;

  private ASTCDInterface astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDInterface astE;

  private ASTCDClass astF;

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
    assertTrue(astA.isPresentCDExtendUsage());
    String name = typeToString(astA.getCDExtendUsage().getSuperclass(0));
    assertEquals("ASTExternalProd", name);

    assertTrue(astC.isPresentCDExtendUsage());
    name = typeToString(astC.getCDExtendUsage().getSuperclass(0));
    assertEquals("mc2cdtransformation.AstRuleInheritance.ASTA", name);

    assertTrue(astD.isPresentCDExtendUsage());
    name = typeToString(astD.getCDExtendUsage().getSuperclass(0));
    assertEquals("mc2cdtransformation.Supergrammar.ASTSuperProd", name);

    assertTrue(astF.isPresentCDExtendUsage());
    name = typeToString(astF.getCDExtendUsage().getSuperclass(0));
    assertEquals("java.util.Observable", name);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstSuperclass() {
    assertTrue(astA.getModifier().isPresentStereotype());
    // one stereotype for the startProd flag and one for the checked external type
    assertEquals(2, astA.getModifier().getStereotype().getValuesList().size());
    assertEquals(astA.getModifier().getStereotype().getValuesList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertFalse(astA.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    assertEquals(astA.getModifier().getStereotype().getValuesList().get(0).getValue(), "ASTExternalProd");

    assertTrue(astF.getModifier().isPresentStereotype());
    assertEquals(1, astF.getModifier().getStereotype().getValuesList().size());
    assertEquals(astF.getModifier().getStereotype().getValuesList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertFalse(astF.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    assertEquals(astF.getModifier().getStereotype().getValuesList().get(0).getValue(), "java.util.Observable");

    assertTrue(astD.getModifier().isPresentStereotype());
    assertEquals(1, astD.getModifier().getStereotype().getValuesList().size());
    assertEquals(astD.getModifier().getStereotype().getValuesList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertFalse(astD.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    assertEquals(astD.getModifier().getStereotype().getValuesList().get(0).getValue(), "java.io.Serializable");
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstInterfaces() {
    assertTrue(astE.getModifier().isPresentStereotype());
    assertEquals(2, astE.getModifier().getStereotype().getValuesList().size());
    assertEquals(astE.getModifier().getStereotype().getValuesList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertFalse(astE.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    assertEquals(astE.getModifier().getStereotype().getValuesList().get(0).getValue(), "ASTExternalInterface");
    assertEquals(astE.getModifier().getStereotype().getValuesList().get(1).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertFalse(astE.getModifier().getStereotype().getValuesList().get(1).getValue().isEmpty());
    assertEquals(astE.getModifier().getStereotype().getValuesList().get(1).getValue(), "java.io.Serializable");
  
    assertTrue(Log.getFindings().isEmpty());
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
    assertEquals("mc2cdtransformation.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("java.io.Serializable", name);
  
    assertTrue(Log.getFindings().isEmpty());
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
    assertEquals("mc2cdtransformation.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("ASTExternalInterface", name);
    name = typeToString(superInterfaces.get(3));
    assertEquals("java.io.Serializable", name);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
