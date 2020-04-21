/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import hierautomata._ast.ASTStateMachine;
import hierautomata._parser.HierAutomataParser;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.GeneratorEngine;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class for the HierAutomata DSL tool.
 */
public class HierAutomataTool {
  
  /**
   * Use the single argument for specifying the single input automaton file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    // use normal logging (no DEBUG, TRACE)
    Log.init();
    
    // Retrieve the model name
    if (args.length != 1) {
      Log.error("0xEE747 Please specify only one single path to the input model.");
      return;
    }
    Log.info("HierAutomata DSL Tool", HierAutomataTool.class.getName());
    Log.info("------------------", HierAutomataTool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTStateMachine ast = parse(model);
    Log.info(model + " parsed successfully!", HierAutomataTool.class.getName());
    
    // --------------------------------------------------------
    // execute a generation process
    Log.info("Writing the parsed automaton into File:", HierAutomataTool.class.getName());
    
    GeneratorSetup s = new GeneratorSetup();
    s.setOutputDirectory(new File("gen"));
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
    // Fihoorst param defines depth of output hierarchy
    // (other params are irrelevant)
    
    // neue GeneratorEngine ist notwendig, weil globale Variable gesetzt
    // wurden (und nicht nochmal gesetzt werden können)
    s = new GeneratorSetup();
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("2pingPong.aut"), ast, 42);
    s = new GeneratorSetup();
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("3pingPong.aut"), ast, 2);
    s = new GeneratorSetup();
    ge = new GeneratorEngine(s);
    ge.generate("tpl2/StateMachine.ftl", Paths.get("4pingPong.aut"), ast, 1);
    
  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public static ASTStateMachine parse(String model) {
    try {
      HierAutomataParser parser = new HierAutomataParser();
      Optional<ASTStateMachine> optStateMachine = parser.parse(model);
      
      if (!parser.hasErrors() && optStateMachine.isPresent()) {
        return optStateMachine.get();
      }
      Log.error("0xEE847 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE647 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
}
