/* (c) https://github.com/MontiCore/monticore */

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hierautomata._ast.ASTStateMachine;
import hierautomata._parser.HierAutomataParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Main class for the HierAutomaton DSL tool.
 */
public class HierAutomataTest {
  
  @Before
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void toolTest() {
    
    // parse the model and create the AST representation
    ASTStateMachine ast = parse("src/test/resources/example/HierarchyPingPong.aut");
    Log.info("src/test/resources/example/HierarchyPingPong.aut" + " parsed successfully!", "HierAutomataTest");
    
    // --------------------------------------------------------
    // execute a generation process
    Log.info("Writing the parsed automaton into File:", "HierAutomataTest");
    
    GeneratorSetup s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/out1"));
    s.setCommentStart("/*-- ");
    s.setCommentEnd(" --*/");
    s.setTracing(true);
    GeneratorEngine ge = new GeneratorEngine(s);
    
    ge.generate("tpl/DemoStateMachine1.ftl", Paths.get("demo1"), ast);
    
    // --------------------------------------------------------
    // second generation process:
    s.setCommentStart("/* ");
    s.setCommentEnd(" */");
    ge = new GeneratorEngine(s);
    
    ge.generate("tpl/StateMachine.ftl", Paths.get("pingPong.aut"), ast);
    
    // Generation with additional parameters
    // First param defines depth of output hierarchy
    // (other params are irrelevant)
    
    // neue GeneratorEngine ist notwendig, weil globale Variable gesetzt
    // wurden (und nicht nochmal gesetzt werden können)
    s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/out2"));
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("2pingPong.aut"), ast, 42);
    s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/out2"));
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("3pingPong.aut"), ast, 2);
    s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/out2"));
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("4pingPong.aut"), ast, 1);

    s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/out3"));
    ge = new GeneratorEngine(s);
    ge.generate("tpl3/StateMachine.ftl", Paths.get("pingPong.aut"), ast);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public  ASTStateMachine parse(String model) {
    Optional<ASTStateMachine> optStateMachine = Optional.empty();
    try {
      HierAutomataParser parser = new HierAutomataParser();
      optStateMachine = parser.parse(model);
      
      if (parser.hasErrors()) {
        Log.error("0xEE848 Model could not be parsed.");
      }
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE648 Failed to parse " + model, e);
    }
    return optStateMachine.get();
  }
}
