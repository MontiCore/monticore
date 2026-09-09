/* (c) https://github.com/MontiCore/monticore */

package mc.feature.keyrule;

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
   * "add" is a nokeyword and a constant of the enum production,
   * thus it must be parsable in both positions.
   */
  @Test
  public void testNoKeywordAsEnumConstant() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("(keyword add) (OperationKind add) (Name Hello)");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());
    assertEquals(ASTOperationKind.ADD, opt.get().getKind());
    assertEquals("Hello", opt.get().getId());
  }

  /**
   * "add" is a nokeyword, thus it must still be parsable as a Name.
   */
  @Test
  public void testNoKeywordAsName() throws IOException {
    NoKeywordEnumParser parser = NoKeywordEnumMill.parser();
    Optional<ASTA> opt = parser.parse_StringA("(keyword add) (OperationKind delete) (Name add)");
    assertFalse(parser.hasErrors());
    assertTrue(opt.isPresent());
    assertEquals(ASTOperationKind.DELETE, opt.get().getKind());
    assertEquals("add", opt.get().getId());
  }
}
