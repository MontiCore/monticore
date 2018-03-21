package mc.feature.interfaces;

import mc.feature.interfaces.optionalgeneration._ast.*;
import mc.feature.interfaces.optionalgeneration._parser.OptionalGenerationParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalInterfacesTest {

  @Test
  public void testMethodExistenceTest1() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest1> astTest1 = parser.parse_StringTest1("abc");
    assertFalse(parser.hasErrors());
    assertTrue(astTest1.isPresent());
    assertFalse(astTest1.get().isPresentName());
    Optional<ASTOpt1> astOpt1 = parser.parse_StringOpt1("abc Name");
    assertFalse(parser.hasErrors());
    assertTrue(astOpt1.isPresent());
    assertTrue(astOpt1.get().isPresentName());
    assertEquals("Name",astOpt1.get().getName());
  }

  @Test
  public void testMethodExistenceTest2() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest2> astTest2 = parser.parse_StringTest2("abc someName");
    assertFalse(parser.hasErrors());
    assertTrue(astTest2.isPresent());
    assertTrue(astTest2.get().isPresentName());
    assertEquals("someName", astTest2.get().getName());
  }

  @Test
  public void testMethodExistenceTest3() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest3> astTest3 = parser.parse_StringTest3("def -a");
    assertFalse(parser.hasErrors());
    assertTrue(astTest3.isPresent());
    assertEquals("-", astTest3.get().getWord().getMinus());
    assertTrue(astTest3.get().isPresentWord());
    Optional<ASTOpt2> astOpt2 = parser.parse_StringOpt2("def Name");
    assertFalse(parser.hasErrors());
    assertTrue(astOpt2.isPresent());
    assertTrue(astOpt2.get().isPresentName());
    assertEquals("Name",astOpt2.get().getName());
  }

  @Test
  public void testMethodExistenceTest4() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest4> astTest4 = parser.parse_StringTest4("def someName");
    assertFalse(parser.hasErrors());
    assertTrue(astTest4.isPresent());
    assertEquals("someName",astTest4.get().getName());
    assertTrue(astTest4.get().isPresentName());
  }

}
