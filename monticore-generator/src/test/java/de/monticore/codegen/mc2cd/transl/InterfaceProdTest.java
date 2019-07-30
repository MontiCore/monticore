/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;

/**
 * Test for the proper transformation of ASTInterfaceProds to corresponding ASTCDInterfaces
 * 
 */
public class InterfaceProdTest {
  
  private ASTCDInterface astA;
  
  private ASTCDInterface astB;
  
  private ASTCDInterface astC;
  
  public InterfaceProdTest() {
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
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.InterfaceProd.ASTextendedProd", name);
  }
  
  /**
   * Checks that the production "interface A astextends X" results in ASTA having X as a
   * superinterface
   */
  @Test
  public void testAstextends() {
    List<ASTMCObjectType> superInterfaces = astB.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("AstExtendedType", name);
  }
  
  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD having x.y.Z as a
   * superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTMCObjectType> superInterfaces = astC.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("java.io.Serializable", name);
  }
  
}
