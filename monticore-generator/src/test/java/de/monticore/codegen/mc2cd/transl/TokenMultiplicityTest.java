/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokenMultiplicityTest extends TranslationTestCase {

  private ASTCDClass testListClass;

  @BeforeEach
  public void setupTokenMultiplicityTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LexerFormat.mc4"));
    testListClass = TestHelper.getCDClass(cdCompilationUnit.get(), "ASTTestList").get();
  }

  @Test
  public void testTokenStar() {
    List<ASTCDAttribute> attributes = testListClass.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    Assertions.assertEquals("java.util.List", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
