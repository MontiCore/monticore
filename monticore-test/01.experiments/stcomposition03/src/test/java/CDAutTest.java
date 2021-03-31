/* (c) https://github.com/MontiCore/monticore */

import automata7._symboltable.StimulusSymbol;
import basicjava._ast.ASTCompilationUnit;
import basicjava._symboltable.MethodSymbol;
import cdandaut.CDClass2StimulusAdapter;
import cdautomata.CDAutomataMill;
import cdautomata._ast.ASTCDAutomaton;
import cdautomata._parser.CDAutomataParser;
import cdautomata._symboltable.ICDAutomataArtifactScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CDAutTest {

  CDAutomataParser parser = new CDAutomataParser();

  @BeforeClass
  public static void setUp(){
//    LogStub.init();         // replace log by a sideffect free variant
    CDAutomataMill.init();
  }

  @Test
  public void testResolveAdapted() throws IOException {
    String model = "src/test/resources/example/Foo.cdaut";
    ASTCDAutomaton ast = parser.parse(model).get();
    ICDAutomataArtifactScope as = CDAutomataMill.scopesGenitorDelegator().createFromAST(ast);
    as.setName("Foo");
    Optional<StimulusSymbol> symbol = as
        .resolveStimulus("Foo.Bar.Bla"); //in example model, this is a CD class
    assertTrue(symbol.isPresent());
    assertEquals("Bla", symbol.get().getName());
    assertTrue(symbol.get() instanceof CDClass2StimulusAdapter); //assure that an adapter was found
  }
  
}
