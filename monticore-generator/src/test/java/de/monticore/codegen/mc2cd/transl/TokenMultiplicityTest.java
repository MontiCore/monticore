/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;

public class TokenMultiplicityTest {

  private final ASTCDClass testListClass;

  public TokenMultiplicityTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LexerFormat.mc4"));
    testListClass = TestHelper.getCDClass(cdCompilationUnit.get(), "ASTTestList").get();
  }

  @Test
  public void testTokenStar() {
    List<ASTCDAttribute> attributes = testListClass.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("java.util.List", name);
  }
}
