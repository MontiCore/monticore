/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
public class AbstractProdTest extends TranslationTestCase {

  private ASTCDClass astA;

  private ASTCDClass astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDClass astE;

  private ASTCDClass astF;

  @BeforeEach
  public void setupAbstractProdTest() {
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
    Assertions.assertTrue(astA.getModifier().isAbstract());
    Assertions.assertTrue(astB.getModifier().isAbstract());
    Assertions.assertTrue(astC.getModifier().isAbstract());
    Assertions.assertTrue(astD.getModifier().isAbstract());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testExtends() {
    Assertions.assertTrue(astA.isPresentCDExtendUsage());
    String name = typeToString(astA.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("mc2cdtransformation.AbstractProd.ASTextendedProd", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract B implements Y" results in ASTB having
   * ASTY as a superinterface
   */
  @Test
  public void testImplements() {
    List<ASTMCObjectType> superInterfaces = astB.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("mc2cdtransformation.AbstractProd.ASTimplementedProd", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract C astextends X" results in ASTC having
   * X as a superclass
   */
  @Test
  public void testAstextends() {
    Assertions.assertTrue(astC.isPresentCDExtendUsage());
    String name = typeToString(astC.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("AstExtendedType", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract D astimplements Y" results in ASTD
   * having Y as a superinterface
   */
  @Test
  public void testAstimplements() {
    List<ASTMCObjectType> superInterfaces = astD.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("AstImplementedType", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract C astextends x.y.Z" results in ASTC
   * having x.y.Z as a superclass
   */
  @Test
  public void testAstextendsQualified() {
    Assertions.assertTrue(astE.isPresentCDExtendUsage());
    String name = typeToString(astE.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("java.util.Observable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD
   * having x.y.Z as a superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTMCObjectType> superInterfaces = astF.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("java.io.Serializable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
