/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExternalImplementationTest {

  private ASTCDClass astZ;

  public ExternalImplementationTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/ExternalImplementationGrammar.mc4")).get();
    astZ = TestHelper.getCDClass(cdCompilationUnit, "ASTZ").get();
  }

  @Test
  public void testExternalImplementation() {
    ASTReferenceType cdInterface = astZ.getInterfaceList().get(0);
    assertTrue(cdInterface != null);
    String name = TransformationHelper.typeToString(cdInterface);
    assertEquals("mc2cdtransformation.Supergrammar.ASTZExt", name);
  }
}
