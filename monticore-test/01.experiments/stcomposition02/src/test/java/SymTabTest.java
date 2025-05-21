/* (c) https://github.com/MontiCore/monticore */

import basicjava._ast.ASTCompilationUnit;
import basicjava._symboltable.MethodSymbol;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import javaaut.JavaAutMill;
import javaaut._parser.JavaAutParser;
import javaaut._symboltable.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(JavaAutMill.class)
public class SymTabTest {


  @Test
  public void testPingPong() {
    IJavaAutArtifactScope as = createSymTab("src/test/resources/example/PingPong.javaaut");
    as.setName("PingPong");
    Optional<MethodSymbol> symbol = as
        .resolveMethod("PingPong.simulate.Game"); //in example model, this is an automaton
    assertTrue(symbol.isPresent());
    assertEquals("Game", symbol.get().getName());
    assertInstanceOf(Automaton2MethodAdapter.class, symbol.get()); //assure that an adapter was found
    MCAssertions.assertNoFindings();
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static IJavaAutArtifactScope createSymTab(String model) {
    ASTCompilationUnit ast = parse(model);
    JavaAutMill.globalScope().setFileExt("javaaut");
    return JavaAutMill.scopesGenitorDelegator().createFromAST(ast);
  }

  public static ASTCompilationUnit parse(String model) {
    try {
      JavaAutParser parser = JavaAutMill.parser();
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84C Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64C Failed to parse " + model, e);
    }
    return null;
  }
}
