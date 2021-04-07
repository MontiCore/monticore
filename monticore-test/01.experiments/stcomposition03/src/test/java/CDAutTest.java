/* (c) https://github.com/MontiCore/monticore */

import automata7._symboltable.StimulusSymbol;
import cdandaut.CDClass2StimulusAdapter;
import cdautomata.CDAutomataMill;
import cdautomata._ast.ASTCDAutomaton;
import cdautomata._parser.CDAutomataParser;
import cdautomata._symboltable.ICDAutomataArtifactScope;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CDAutTest {

  CDAutomataParser parser = new CDAutomataParser();

  @BeforeClass
  public static void setUp() {
    //    LogStub.init();         // replace log by a sideffect free variant
    CDAutomataMill.init();
  }

  @Test
  public void testResolveAdapted() throws IOException {
    //initialize
    String model = "src/test/resources/example/Foo.cdaut";
    ASTCDAutomaton ast = parser.parse(model).get();
    ICDAutomataArtifactScope as = CDAutomataMill.scopesGenitorDelegator().createFromAST(ast);
    as.setName("Foo");

    // resolve for adapted symbol
    Optional<StimulusSymbol> symbol = as
        .resolveStimulus("Foo.Bar.Bla"); //in example model, this is a CD class
    assertTrue(symbol.isPresent());
    assertEquals("Bla", symbol.get().getName());
    assertTrue(symbol.get() instanceof CDClass2StimulusAdapter); //assure that an adapter was found

    // resolve for same symbol a second time
    Optional<StimulusSymbol> symbol2 = as
        .resolveStimulus("Foo.Bar.Bla"); //in example model, this is a CD class
    assertTrue(symbol2.isPresent());
    assertEquals("Bla", symbol2.get().getName());
    assertTrue(symbol2.get() instanceof CDClass2StimulusAdapter); //assure that an adapter was found

    //assure that the same object of the adapter was found in both calls
    assertEquals(symbol.get(), symbol2.get());
  }

}
