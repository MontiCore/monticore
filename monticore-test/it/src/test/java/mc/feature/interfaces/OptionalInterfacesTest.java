/* (c) https://github.com/MontiCore/monticore */
package mc.feature.interfaces;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.interfaces.optionalgeneration._ast.*;
import mc.feature.interfaces.optionalgeneration._parser.OptionalGenerationParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalInterfacesTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testMethodExistenceTest1() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTOpt1> astOpt1 = parser.parse_StringOpt1("abc Name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astOpt1.isPresent());
    Assertions.assertTrue(astOpt1.get().isPresentName());
    Assertions.assertEquals("Name", astOpt1.get().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceTest2() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest2> astTest2 = parser.parse_StringTest2("abc someName");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astTest2.isPresent());
    Assertions.assertTrue(astTest2.get().isPresentName());
    Assertions.assertEquals("someName", astTest2.get().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceTest3() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTOpt2> astOpt2 = parser.parse_StringOpt2("def Name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astOpt2.isPresent());
    Assertions.assertTrue(astOpt2.get().isPresentName());
    Assertions.assertEquals("Name", astOpt2.get().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceTest4() throws IOException{
    OptionalGenerationParser parser = new OptionalGenerationParser();
    Optional<ASTTest4> astTest4 = parser.parse_StringTest4("def someName");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astTest4.isPresent());
    Assertions.assertEquals("someName", astTest4.get().getName());
    Assertions.assertTrue(astTest4.get().isPresentName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
