/* (c) https://github.com/MontiCore/monticore */

package mc.feature.interfaces;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.interfaces.methodinterface.MethodInterfaceMill;
import mc.feature.interfaces.methodinterface._ast.*;
import mc.feature.interfaces.methodinterface._parser.MethodInterfaceParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(MethodInterfaceMill.class)
public class MethodInterfaceTest {

  @Test
  public void testInterfaceDefaultA() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTInterfaceDefault> ast = parser.parse_StringInterfaceDefault("Hello3");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("test", ast.get().getTest());
  }

  @Test
  public void testInterfaceDefaultA1() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTInterfaceDefaultA> ast = parser.parse_StringInterfaceDefaultA("Hello");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("A", ast.get().getTest());
  }

  @Test
  public void testInterfaceDefaultA2() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTA> ast = parser.parse_StringA("Hello");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("A", ast.get().getTest());
  }

  @Test
  public void testInterfaceAbstract() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTInterfaceAbstract> ast = parser.parse_StringInterfaceAbstract("Hello2");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("B", ast.get().getTest2());
  }

  @Test
  public void testInterfaceAbstractB() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTB> ast = parser.parse_StringB("Hello2");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("B", ast.get().getTest2());
  }

  @Test
  public void testClassMethod() throws IOException {
    MethodInterfaceParser parser = MethodInterfaceMill.parser();
    Optional<ASTClassMethod> ast = parser.parse_StringClassMethod("Name C");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals("ABC", ast.get().getTest3());
  }
}
