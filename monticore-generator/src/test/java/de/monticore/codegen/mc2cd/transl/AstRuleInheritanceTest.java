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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;

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

  @BeforeEach
  public void setUp() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();

    Optional<ASTCDClass> astaOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTA");
    Optional<ASTCDInterface> astbOpt = TestHelper.getCDInterface(cdCompilationUnit, "ASTB");
    Optional<ASTCDClass> astcOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTC");
    Optional<ASTCDClass> astdOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTD");
    Optional<ASTCDInterface> asteOpt = TestHelper.getCDInterface(cdCompilationUnit, "ASTE");
    Optional<ASTCDClass> astfOpt = TestHelper.getCDClass(cdCompilationUnit, "ASTF");

    Assertions.assertTrue(astaOpt.isPresent());
    Assertions.assertTrue(astbOpt.isPresent());
    Assertions.assertTrue(astcOpt.isPresent());
    Assertions.assertTrue(astdOpt.isPresent());
    Assertions.assertTrue(asteOpt.isPresent());
    Assertions.assertTrue(astfOpt.isPresent());

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
    Assertions.assertTrue(astA.isPresentCDExtendUsage());
    String name = typeToString(astA.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("ASTExternalProd", name);

    Assertions.assertTrue(astC.isPresentCDExtendUsage());
    name = typeToString(astC.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("mc2cdtransformation.AstRuleInheritance.ASTA", name);

    Assertions.assertTrue(astD.isPresentCDExtendUsage());
    name = typeToString(astD.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.ASTSuperProd", name);

    Assertions.assertTrue(astF.isPresentCDExtendUsage());
    name = typeToString(astF.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("java.util.Observable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstSuperclass() {
    Assertions.assertTrue(astA.getModifier().isPresentStereotype());
    // one stereotype for the startProd flag and one for the checked external type
    Assertions.assertEquals(2, astA.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals(astA.getModifier().getStereotype().getValuesList().get(0).getName(), MC2CDStereotypes.EXTERNAL_TYPE.toString());
    Assertions.assertFalse(astA.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    Assertions.assertEquals(astA.getModifier().getStereotype().getValuesList().get(0).getValue(), "ASTExternalProd");

    Assertions.assertTrue(astF.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astF.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals(astF.getModifier().getStereotype().getValuesList().get(0).getName(), MC2CDStereotypes.EXTERNAL_TYPE.toString());
    Assertions.assertFalse(astF.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    Assertions.assertEquals(astF.getModifier().getStereotype().getValuesList().get(0).getValue(), "java.util.Observable");

    Assertions.assertTrue(astD.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, astD.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals(astD.getModifier().getStereotype().getValuesList().get(0).getName(), MC2CDStereotypes.EXTERNAL_TYPE.toString());
    Assertions.assertFalse(astD.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    Assertions.assertEquals(astD.getModifier().getStereotype().getValuesList().get(0).getValue(), "java.io.Serializable");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * tests the stereotye 'externalType' which is added to the types,
   * which can not be machted to defined AST-prods
   */
  @Test
  public void testStereotypesForAstInterfaces() {
    Assertions.assertTrue(astE.getModifier().isPresentStereotype());
    Assertions.assertEquals(2, astE.getModifier().getStereotype().getValuesList().size());
    Assertions.assertEquals(astE.getModifier().getStereotype().getValuesList().get(0).getName(), MC2CDStereotypes.EXTERNAL_TYPE.toString());
    Assertions.assertFalse(astE.getModifier().getStereotype().getValuesList().get(0).getValue().isEmpty());
    Assertions.assertEquals(astE.getModifier().getStereotype().getValuesList().get(0).getValue(), "ASTExternalInterface");
    Assertions.assertEquals(astE.getModifier().getStereotype().getValuesList().get(1).getName(), MC2CDStereotypes.EXTERNAL_TYPE.toString());
    Assertions.assertFalse(astE.getModifier().getStereotype().getValuesList().get(1).getValue().isEmpty());
    Assertions.assertEquals(astE.getModifier().getStereotype().getValuesList().get(1).getValue(), "java.io.Serializable");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * checks for prod D, that the astextends results in a super class
   * and astimplements in super interfaces
   */
  @Test
  public void testASTExtendsAndImplements() {
    List<ASTMCObjectType> superInterfaces = astD.getInterfaceList();
    Assertions.assertEquals(3, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("mc2cdtransformation.AstRuleInheritance.ASTB", name);
    name = typeToString(superInterfaces.get(1));
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    Assertions.assertEquals("java.io.Serializable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * checks that the astextends keyword for interface prods (here E)
   * adds all of these to super interfaces
   */
  @Test
  public void testAstInterfaces() {
    List<ASTMCObjectType> superInterfaces = astE.getInterfaceList();
    Assertions.assertEquals(4, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("mc2cdtransformation.AstRuleInheritance.ASTB", name);
    name = typeToString(superInterfaces.get(1));
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    Assertions.assertEquals("ASTExternalInterface", name);
    name = typeToString(superInterfaces.get(3));
    Assertions.assertEquals("java.io.Serializable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
