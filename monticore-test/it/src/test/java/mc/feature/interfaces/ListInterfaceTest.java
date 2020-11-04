/* (c) https://github.com/MontiCore/monticore */
package mc.feature.interfaces;

import mc.feature.interfaces.listgeneration._ast.*;
import mc.feature.interfaces.listgeneration._parser.ListGenerationParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListInterfaceTest {

  @Test
  public void testMethodExistenceTokenPlus() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTTokenPlus> ast = parser.parse_StringTokenPlus("+ Name, name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2,ast.get().getNameList().size());
    assertFalse(ast.get().isEmptyNames());
    assertEquals(0,ast.get().indexOfName("Name"));
  }

  @Test
  public void testMethodExistenceTokenStar() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTTokenStar> ast = parser.parse_StringTokenStar("something * Name name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2,ast.get().getNameList().size());
    assertFalse(ast.get().isEmptyNames());
    assertEquals(0,ast.get().indexOfName("Name"));
  }

  @Test
  public void testMethodExistenceListPlus() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTListPlus> ast = parser.parse_StringListPlus("something Abc Dec");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2,ast.get().getTestList().size());
    assertFalse(ast.get().isEmptyTest());
  }

  @Test
  public void testMethodExistenceListStar() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTListStar> ast = parser.parse_StringListStar("Abc Dec Abc word");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(3,ast.get().getTestList().size());
    assertFalse(ast.get().isEmptyTest());
  }
}
