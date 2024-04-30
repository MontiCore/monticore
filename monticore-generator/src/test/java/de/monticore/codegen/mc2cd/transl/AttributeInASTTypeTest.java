/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AttributeInASTTypeTest extends TranslationTestCase {

  private ASTCDClass astA;

  @Before
  public void setupAttributeInASTTypeTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTTypeGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
  }

  @Test
  public void testType() {
    astA.getCDAttributeList().stream()
        .map(ASTCDAttribute::getMCType)
        .map(Object::getClass)
        .forEach(type -> assertEquals(ASTMCPrimitiveType.class, type));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
