/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExternalImplementationTest extends TranslationTestCase {

  private ASTCDClass astZ;

  @BeforeEach
  public void setupExternalImplementationTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/ExternalImplementationGrammar.mc4")).get();
    astZ = TestHelper.getCDClass(cdCompilationUnit, "ASTZ").get();
  }

  @Test
  public void testExternalImplementation() {
    ASTMCObjectType cdInterface = astZ.getInterfaceList().get(0);
    Assertions.assertTrue(cdInterface != null);
    String name = TransformationHelper.typeToString(cdInterface);
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.ASTZExt", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
