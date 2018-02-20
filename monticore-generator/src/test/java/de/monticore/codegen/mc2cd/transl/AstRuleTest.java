/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;

/**
 * Test for the proper transformation of ASTAbstractProds to corresponding
 * ASTCDClasses
 * 
 * @author Sebastian Oberhoff
 */
@Ignore
// TODO: refactor - this test is a mess
public class AstRuleTest {
  
  private ASTCDClass astA;
  
  private ASTCDInterface astB;

  private ASTCDClass astC;
  
  private ASTCDClass astD;
  
  private ASTCDInterface astE;
  
  private ASTCDClass astF;
  
  public AstRuleTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRule.mc4")).get();

    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDInterface(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    astD = TestHelper.getCDClass(cdCompilationUnit, "ASTD").get();
    astE = TestHelper.getCDInterface(cdCompilationUnit, "ASTE").get();
    astF = TestHelper.getCDClass(cdCompilationUnit, "ASTF").get();
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testAstSuperClass() {
    java.util.Optional<ASTReferenceType> superClasses = astA.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("ASTExternalProd", name);
    
    superClasses = astC.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("ASTA", name);
    
    superClasses = astD.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("mc2cdtransformation.super._ast.ASTSuperProd", name);
    
    superClasses = astF.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("java.util.Observable", name);
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testStereotypesForAstSuperclass() {
    assertTrue(astA.isPresentModifier());
    assertTrue(astA.getModifier().isPresentStereotype());
    assertEquals(1, astA.getModifier().getStereotype().getValueList().size());
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
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testStereotypesForAstInterfaces() {
    assertTrue(astD.isPresentModifier());
    assertTrue(astD.getModifier().isPresentStereotype());
    assertEquals(1, astD.getModifier().getStereotype().getValueList().size());
    assertEquals(astD.getModifier().getStereotype().getValueList().get(0).getName(),
        MC2CDStereotypes.EXTERNAL_TYPE.toString());
    assertTrue(astD.getModifier().getStereotype().getValueList().get(0).isPresentValue());
    assertEquals(astD.getModifier().getStereotype().getValueList().get(0).getValue(), "java.io.Serializable");
    
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
   * Checks that the production "abstract B implements Y" results in ASTB having
   * ASTY as a superinterface
   */
  @Test
  public void testAstInterfaces() {
    List<ASTReferenceType> superInterfaces = astD.getInterfaceList();
    assertEquals(3, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super._ast.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("java.io.Serializable", name);
    
    superInterfaces = astE.getInterfaceList();
    assertEquals(4, superInterfaces.size());
    name = typeToString(superInterfaces.get(0));
    assertEquals("ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super._ast.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("ASTExternalInterface", name);
    name = typeToString(superInterfaces.get(3));
    assertEquals("java.io.Serializable", name);
  }
  
}
