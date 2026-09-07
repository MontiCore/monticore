/* (c) https://github.com/MontiCore/monticore */
package mc.feature.interfaces;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.interfaces.listgeneration.ListGenerationMill;
import mc.feature.interfaces.listgeneration._ast.ASTListPlus;
import mc.feature.interfaces.listgeneration._ast.ASTListStar;
import mc.feature.interfaces.listgeneration._ast.ASTTokenPlus;
import mc.feature.interfaces.listgeneration._ast.ASTTokenStar;
import mc.feature.interfaces.listgeneration._parser.ListGenerationParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(ListGenerationMill.class)
public class ListInterfaceTest {

  @Test
  public void testMethodExistenceTokenPlus() throws IOException{
    ListGenerationParser parser = ListGenerationMill.parser();
    Optional<ASTTokenPlus> ast = parser.parse_StringTokenPlus("+ Name, name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2, ast.get().getNameList().size());
    assertFalse(ast.get().isEmptyNames());
    assertEquals(0, ast.get().indexOfName("Name"));
  }

  @Test
  public void testMethodExistenceTokenStar() throws IOException{
    ListGenerationParser parser = ListGenerationMill.parser();
    Optional<ASTTokenStar> ast = parser.parse_StringTokenStar("something * Name name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2, ast.get().getNameList().size());
    assertFalse(ast.get().isEmptyNames());
    assertEquals(0, ast.get().indexOfName("Name"));
  }

  @Test
  public void testMethodExistenceListPlus() throws IOException{
    ListGenerationParser parser = ListGenerationMill.parser();
    Optional<ASTListPlus> ast = parser.parse_StringListPlus("something Abc Dec");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(2, ast.get().getTestList().size());
    assertFalse(ast.get().isEmptyTest());
  }

  @Test
  public void testMethodExistenceListStar() throws IOException{
    ListGenerationParser parser = ListGenerationMill.parser();
    Optional<ASTListStar> ast = parser.parse_StringListStar("Abc Dec Abc word");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(3, ast.get().getTestList().size());
    assertFalse(ast.get().isEmptyTest());
  }
}
