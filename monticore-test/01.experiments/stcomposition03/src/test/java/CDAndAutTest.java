/* (c) https://github.com/MontiCore/monticore */

import automata7.Automata7Mill;
import automata7._ast.ASTAutomaton;
import automata7._parser.Automata7Parser;
import automata7._symboltable.IAutomata7ArtifactScope;
import automata7._symboltable.StimulusSymbol;
import basiccd.BasicCDMill;
import basiccd._ast.ASTClassDiagram;
import basiccd._parser.BasicCDParser;
import cdandaut.CDClass2StimulusAdapter;
import cdandaut.CDClass2StimulusResolver;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.LogStub;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CDAndAutTest {

  BasicCDParser cdParser = new BasicCDParser();

  Automata7Parser autParser = new Automata7Parser();

  @BeforeAll
  public static void setUpLogger() {
    BasicCDMill.reset();
    Automata7Mill.reset();
    BasicCDMill.init();
    Automata7Mill.init();
  }
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testPingPong() throws IOException {
    // read symtab of cd model into cd global scope
    String cdModel = "src/test/resources/example/Bar.cd";
    ASTClassDiagram cdAST = cdParser.parse(cdModel).get();
    BasicCDMill.scopesGenitorDelegator().createFromAST(cdAST);

    // read symtab of automata model into aut global scope
    String autModel = "src/test/resources/example/PingPong.aut";
    ASTAutomaton autAST = autParser.parse(autModel).get();
    IAutomata7ArtifactScope autAS = Automata7Mill.scopesGenitorDelegator().createFromAST(autAST);
    autAS.setName(autAST.getName());

    // configure aut global scope with resolver
    Automata7Mill.globalScope()
        .addAdaptedStimulusSymbolResolver(new CDClass2StimulusResolver());

    // resolve for Stimulus  "Bar.Bla", which in fact only exists as state in the aut
    Optional<StimulusSymbol> s = Automata7Mill.globalScope()
        .resolveStimulus("Bar.Bla");

    assertTrue(s.isPresent());
    assertEquals("Bla", s.get().getName());
    assertInstanceOf(CDClass2StimulusAdapter.class, s.get()); //assure that an adapter was found
    MCAssertions.assertNoFindings();
  }
}
