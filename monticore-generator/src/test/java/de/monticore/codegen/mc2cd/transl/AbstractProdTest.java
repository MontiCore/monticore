/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the proper transformation of ASTAbstractProds to corresponding
 * ASTCDClasses
 *
 */
public class AbstractProdTest {

  private ASTCDClass astA;

  private ASTCDClass astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDClass astE;

  private ASTCDClass astF;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  public AbstractProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();

    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    astD = TestHelper.getCDClass(cdCompilationUnit, "ASTD").get();
    astE = TestHelper.getCDClass(cdCompilationUnit, "ASTE").get();
    astF = TestHelper.getCDClass(cdCompilationUnit, "ASTF").get();
  }

  @Test
  public void testAbstract() {
    assertTrue(astA.isPresentModifier());
    assertTrue(astA.getModifier().isAbstract());
    assertTrue(astB.isPresentModifier());
    assertTrue(astB.getModifier().isAbstract());
    assertTrue(astC.isPresentModifier());
    assertTrue(astC.getModifier().isAbstract());
    assertTrue(astD.isPresentModifier());
    assertTrue(astD.getModifier().isAbstract());
  }

  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testExtends() {
    java.util.Optional<ASTMCObjectType> superClasses = astA.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("mc2cdtransformation.AbstractProd.ASTextendedProd", name);
  }

  /**
   * Checks that the production "abstract B implements Y" results in ASTB having
   * ASTY as a superinterface
   */
  @Test
  public void testImplements() {
    List<ASTMCObjectType> superInterfaces = astB.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.AbstractProd.ASTimplementedProd", name);
  }

  /**
   * Checks that the production "abstract C astextends X" results in ASTC having
   * X as a superclass
   */
  @Test
  public void testAstextends() {
    java.util.Optional<ASTMCObjectType> superClasses = astC.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("AstExtendedType", name);
  }

  /**
   * Checks that the production "abstract D astimplements Y" results in ASTD
   * having Y as a superinterface
   */
  @Test
  public void testAstimplements() {
    List<ASTMCObjectType> superInterfaces = astD.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("AstImplementedType", name);
  }

  /**
   * Checks that the production "abstract C astextends x.y.Z" results in ASTC
   * having x.y.Z as a superclass
   */
  @Test
  public void testAstextendsQualified() {
    java.util.Optional<ASTMCObjectType> superClasses = astE.getSuperclassOpt();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("java.util.Observable", name);
  }

  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD
   * having x.y.Z as a superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTMCObjectType> superInterfaces = astF.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("java.io.Serializable", name);
  }
}
