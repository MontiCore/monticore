/* (c) https://github.com/MontiCore/monticore */
package mc.feature.interfaces;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.interfaces.listgeneration._ast.*;
import mc.feature.interfaces.listgeneration._parser.ListGenerationParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListInterfaceTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testMethodExistenceTokenPlus() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTTokenPlus> ast = parser.parse_StringTokenPlus("+ Name, name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(2, ast.get().getNameList().size());
    Assertions.assertFalse(ast.get().isEmptyNames());
    Assertions.assertEquals(0, ast.get().indexOfName("Name"));
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceTokenStar() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTTokenStar> ast = parser.parse_StringTokenStar("something * Name name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(2, ast.get().getNameList().size());
    Assertions.assertFalse(ast.get().isEmptyNames());
    Assertions.assertEquals(0, ast.get().indexOfName("Name"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceListPlus() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTListPlus> ast = parser.parse_StringListPlus("something Abc Dec");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(2, ast.get().getTestList().size());
    Assertions.assertFalse(ast.get().isEmptyTest());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodExistenceListStar() throws IOException{
    ListGenerationParser parser = new ListGenerationParser();
    Optional<ASTListStar> ast = parser.parse_StringListStar("Abc Dec Abc word");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(3, ast.get().getTestList().size());
    Assertions.assertFalse(ast.get().isEmptyTest());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
