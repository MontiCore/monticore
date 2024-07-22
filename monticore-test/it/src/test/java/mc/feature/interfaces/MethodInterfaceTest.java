/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.interfaces.methodinterface._ast.*;
import mc.feature.interfaces.methodinterface._parser.MethodInterfaceParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodInterfaceTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testInterfaceDefaultA() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTInterfaceDefault> ast = parser.parse_StringInterfaceDefault("Hello3");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("test", ast.get().getTest());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceDefaultA1() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTInterfaceDefaultA> ast = parser.parse_StringInterfaceDefaultA("Hello");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("A", ast.get().getTest());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceDefaultA2() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTA> ast = parser.parse_StringA("Hello");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("A", ast.get().getTest());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceAbstract() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTInterfaceAbstract> ast = parser.parse_StringInterfaceAbstract("Hello2");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("B", ast.get().getTest2());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceAbstractB() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTB> ast = parser.parse_StringB("Hello2");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("B", ast.get().getTest2());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassMethod() throws IOException {
    MethodInterfaceParser parser = new MethodInterfaceParser();
    Optional<ASTClassMethod> ast = parser.parse_StringClassMethod("Name C");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ABC", ast.get().getTest3());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
