/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.keyrule.nokeywordenum.NoKeywordEnumMill;
import mc.feature.keyrule.nokeywordenum._ast.ASTA;
import mc.feature.keyrule.nokeywordenum._ast.ASTOperationKind;
import mc.feature.keyrule.nokeywordenum._parser.NoKeywordEnumParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(NoKeywordEnumMill.class)
public class NoKeywordEnumTest {

  /**
   * "add" is a nokeyword, thus it must still be parsable as a Name.
   */
  @Test
  public void testNoKeywordAsName() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("add add;");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());

    ASTA ast = opt.get();
    assertEquals("add", ast.getName(0));
  }

  /**
   * "add" is a constant of the enum production and must be parsable there as well.
   */
  @Test
  public void testNoKeywordAsEnumConstant() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("modifier add Hello;");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());

    ASTA ast = opt.get();
    assertEquals(1, ast.sizeOperationKinds());
    assertEquals(ASTOperationKind.ADD, ast.getOperationKind(0));
    assertEquals("Hello", ast.getName(0));

    opt = parser.parse_StringA("modifier delete Hello;");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());

    ast = opt.get();
    assertEquals(1, ast.sizeOperationKinds());
    assertEquals(ASTOperationKind.DELETE, ast.getOperationKind(0));
    assertEquals("Hello", ast.getName(0));
  }

  /**
   * Both alternatives must be usable within one model.
   */
  @Test
  public void testCombined() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("add Hello; modifier add World; modifier delete Foo;");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());

    ASTA ast = opt.get();
    // both alternatives contribute to the same Name list
    assertEquals(3, ast.sizeNames());
    assertEquals("Hello", ast.getName(0));
    assertEquals("World", ast.getName(1));
    assertEquals("Foo", ast.getName(2));

    assertEquals(2, ast.sizeOperationKinds());
    assertEquals(ASTOperationKind.ADD, ast.getOperationKind(0));
    assertEquals(ASTOperationKind.DELETE, ast.getOperationKind(1));
  }

  /**
   * Sanity check: unknown input still yields an error.
   */
  @Test
  public void testInvalid() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("modifier unknown Hello;");
    assertTrue(parser.hasErrors());
    assertFalse(opt.isPresent());
    MCAssertions.assertHasFindingStartingWith(
        "no viable alternative at input 'unknown', expecting 'add' or 'delete'");
    MCAssertions.assertHasFindingStartingWith("extraneous input 'Hello' expecting ';'");
  }
}
