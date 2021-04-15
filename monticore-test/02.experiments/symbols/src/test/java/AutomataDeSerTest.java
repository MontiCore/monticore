/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.AutomataDeSer;
import automata._symboltable.AutomataSymbols2Json;
import automata._symboltable.IAutomataArtifactScope;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AutomataDeSerTest {

  // setup the language infrastructure
  AutomataParser parser = new AutomataParser() ;
  AutomataSymbols2Json s2j = new AutomataSymbols2Json() ;

  @Before
  public void init(){
    AutomataMill.init();
//    LogStub.init();
  }

  @Test
  public void storePingPong() throws IOException {
    //parse, create symtab
    ASTAutomaton ast = parser.parse( "src/test/resources/example/PingPong.aut" ).get();
    IAutomataArtifactScope as = AutomataMill.scopesGenitorDelegator().createFromAST(ast);

    //enabled formatting for stored symtabs
    JsonPrinter.enableIndentation();

    //store symtab and load it again
    s2j.store(as, "target/PingPong.autsym");
    IAutomataArtifactScope loaded = s2j.load("target/PingPong.autsym");
    assertEquals(0, Log.getErrorCount());
    assertNotNull(loaded);
  }
}
