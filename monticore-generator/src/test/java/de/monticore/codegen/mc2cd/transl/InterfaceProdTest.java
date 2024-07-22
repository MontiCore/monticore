/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
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
 * Test for the proper transformation of ASTInterfaceProds to corresponding ASTCDInterfaces
 * 
 */
public class InterfaceProdTest extends TranslationTestCase {
  
  private ASTCDInterface astA;
  
  private ASTCDInterface astB;
  
  private ASTCDInterface astC;

  @BeforeEach
  public void setupInterfaceProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InterfaceProd.mc4")).get();
    astA = TestHelper.getCDInterface(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDInterface(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDInterface(cdCompilationUnit, "ASTC").get();
  }
  
  /**
   * Checks that the production "interface A extends X" results in ASTA having ASTX as a
   * superinterface
   */
  @Test
  public void testExtends() {
    List<ASTMCObjectType> superInterfaces = astA.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("mc2cdtransformation.InterfaceProd.ASTextendedProd", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Checks that the production "interface A astextends X" results in ASTA having X as a
   * superinterface
   */
  @Test
  public void testAstextends() {
    List<ASTMCObjectType> superInterfaces = astB.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("AstExtendedType", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD having x.y.Z as a
   * superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTMCObjectType> superInterfaces = astC.getInterfaceList();
    Assertions.assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    Assertions.assertEquals("java.io.Serializable", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
