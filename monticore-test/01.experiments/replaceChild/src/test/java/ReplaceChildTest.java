/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;
import replacechildtest.ReplaceChildTestMill;
import replacechildtest._ast.ASTA;
import replacechildtest._ast.ASTB;
import replacechildtest._ast.ASTC;
import replacechildtest._ast.ASTD;
import replacechildtest._parser.ReplaceChildTestParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ReplaceChildTestMill.class)
public class ReplaceChildTest {

  @Test
  public void testReplaceB() throws IOException {
    String code = "B C D B";
    ReplaceChildTestParser parser = ReplaceChildTestMill.parser();
    ASTA a = parser.parse_StringA(code).get();
    ASTB originalB = a.getB();
    ASTB originalEnd = a.getEnd();

    ASTB replacementB = ReplaceChildTestMill.bBuilder().build();
    ASTB replacementEnd = ReplaceChildTestMill.bBuilder().build();
    a.replaceChild(originalB, replacementB);

    assertSame(replacementB, a.getB());
    assertSame(originalEnd, a.getEnd());

    a.replaceChild(originalEnd, replacementEnd);

    assertSame(replacementB, a.getB());
    assertSame(replacementEnd, a.getEnd());
  }

  @Test
  public void testReplaceC() throws IOException {
    String code = "B C D B";
    ReplaceChildTestParser parser = ReplaceChildTestMill.parser();
    ASTA a = parser.parse_StringA(code).get();
    ASTC originalC = a.getC();
    ASTC replacementC = ReplaceChildTestMill.cBuilder().build();

    a.replaceChild(originalC, replacementC);

    assertTrue(a.isPresentC());
    assertSame(replacementC, a.getC());
  }

  @Test
  public void testReplaceD() throws IOException {
    String code = "B C D D B";
    ReplaceChildTestParser parser = ReplaceChildTestMill.parser();
    ASTA a = parser.parse_StringA(code).get();
    ASTD originalD0 = a.getD(0);
    ASTD originalD1 = a.getD(1);

    ASTD replacementD1 = ReplaceChildTestMill.dBuilder().build();
    a.replaceChild(originalD1, replacementD1);

    assertEquals(2, a.sizeDs());
    assertSame(originalD0, a.getD(0));
    assertSame(replacementD1, a.getD(1));
  }

  @Test
  public void testReplaceMultipleChildren() throws IOException {
    String code = "B C D D B";
    ReplaceChildTestParser parser = ReplaceChildTestMill.parser();
    ASTA a = parser.parse_StringA(code).get();
    ASTD replacementD = ReplaceChildTestMill.dBuilder().build();
    a.replaceChild(a.getD(0), replacementD);
    a.replaceChild(a.getD(1), replacementD);

    assertSame(replacementD, a.getD(0));
    assertSame(replacementD, a.getD(1));

    ASTD replacementD2 = ReplaceChildTestMill.dBuilder().build();
    a.replaceChild(a.getD(0), replacementD2);

    assertSame(replacementD2, a.getD(0));
    assertSame(replacementD2, a.getD(1));
  }

  @Test
  public void testReplaceNonExistentChild() throws IOException {
    String code = "B C D B";
    ReplaceChildTestParser parser = ReplaceChildTestMill.parser();
    ASTA a = parser.parse_StringA(code).get();
    ASTB originalB = a.getB();
    ASTB originalEnd = a.getEnd();

    ASTB replacementB = ReplaceChildTestMill.bBuilder().build();
    a.replaceChild(replacementB, replacementB);

    assertSame(originalB, a.getB());
    assertSame(originalEnd, a.getEnd());
  }

}
