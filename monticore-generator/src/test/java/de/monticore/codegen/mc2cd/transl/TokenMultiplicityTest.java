/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * @author Sebastian Oberhoff
 */
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
    String name = typeToString(attributes.get(0).getType());
    assertEquals("java.util.List", name);
  }
}
