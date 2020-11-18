/* (c) https://github.com/MontiCore/monticore */

import basicjava._ast.ASTCompilationUnit;
import basicjava._symboltable.MethodSymbol;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import javaaut.JavaAutMill;
import javaaut._parser.JavaAutParser;
import javaaut._symboltable.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SymTabTest {

  @BeforeClass
  public static void setUp(){
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
  }

  @Test
  public void testPingPong() {
    IJavaAutArtifactScope as = createSymTab("src/test/resources/example/PingPong.javaaut");
    Optional<MethodSymbol> symbol = as
        .resolveMethod("PingPong.PingPong.simulate.Game"); //in example model, this is an automaton
    assertTrue(symbol.isPresent());
    assertEquals("Game", symbol.get().getName());
    assertTrue(symbol.get() instanceof Automaton2MethodAdapter); //assure that an adapter was found
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static IJavaAutArtifactScope createSymTab(String model) {
    ASTCompilationUnit ast = parse(model);
    JavaAutMill.javaAutGlobalScope().setModelFileExtension("javaaut");
    return JavaAutMill.javaAutSymbolTableCreatorDelegator().createFromAST(ast);
  }

  public static ASTCompilationUnit parse(String model) {
    try {
      JavaAutParser parser = new JavaAutParser();
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84C Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64C Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
}
