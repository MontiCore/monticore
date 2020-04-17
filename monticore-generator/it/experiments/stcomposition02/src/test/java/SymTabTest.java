/* (c) https://github.com/MontiCore/monticore */

import basicjava._ast.ASTCompilationUnit;
import basicjava._symboltable.MethodSymbol;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import javaaut._parser.JavaAutParser;
import javaaut._symboltable.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SymTabTest {

  @Test
  public void testPingPong() {
    JavaAutArtifactScope as = createSymTab("src/test/resources/example/PingPong.javaaut");
    Optional<MethodSymbol> symbol = as
        .resolveMethod("PingPong.simulate.Game"); //in example model, this is an automaton
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
  public static JavaAutArtifactScope createSymTab(String model) {
    ASTCompilationUnit ast = parse(model);
    JavaAutGlobalScope globalScope = JavaAutSymTabMill.javaAutGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setJavaAutLanguage(new JavaAutLanguage()) //will be removed soon
        .build();

    //initialize symbol table creators
    JavaAutSymbolTableCreatorDelegator stc = JavaAutSymTabMill
        .javaAutSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    return stc.createFromAST(ast);
  }

  public static ASTCompilationUnit parse(String model) {
    try {
      JavaAutParser parser = new JavaAutParser();
      Optional<ASTCompilationUnit> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
}
