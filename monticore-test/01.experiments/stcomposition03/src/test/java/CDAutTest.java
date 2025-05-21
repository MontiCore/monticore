/* (c) https://github.com/MontiCore/monticore */

import automata7._symboltable.StimulusSymbol;
import cdandaut.CDClass2StimulusAdapter;
import cdautomata.CDAutomataMill;
import cdautomata._ast.ASTCDAutomaton;
import cdautomata._symboltable.ICDAutomataArtifactScope;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(CDAutomataMill.class)
public class CDAutTest {

  @Test
  public void testResolveAdapted() throws IOException {
    //initialize
    String model = "src/test/resources/example/Foo.cdaut";
    ASTCDAutomaton ast = CDAutomataMill.parser().parse(model).get();
    ICDAutomataArtifactScope as = CDAutomataMill.scopesGenitorDelegator().createFromAST(ast);
    as.setName("Foo");

    // resolve for adapted symbol
    Optional<StimulusSymbol> symbol = as
        .resolveStimulus("Foo.Bar.Bla"); //in example model, this is a CD class
    assertTrue(symbol.isPresent());
    assertEquals("Bla", symbol.get().getName());
    assertInstanceOf(CDClass2StimulusAdapter.class, symbol.get()); //assure that an adapter was found

    // resolve for same symbol a second time
    Optional<StimulusSymbol> symbol2 = as
        .resolveStimulus("Foo.Bar.Bla"); //in example model, this is a CD class
    assertTrue(symbol2.isPresent());
    assertEquals("Bla", symbol2.get().getName());
    assertInstanceOf(CDClass2StimulusAdapter.class, symbol2.get()); //assure that an adapter was found

    //assure that the same object of the adapter was found in both calls
    assertEquals(symbol.get(), symbol2.get());
    MCAssertions.assertNoFindings();
  }

}
