/* (c) Monticore license: https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import hierautomaton._ast.ASTStateMachine;
import hierautomaton._parser.HierAutomatonParser;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.GeneratorEngine;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class for the HierAutomaton DSL tool.
 */
public class HierAutomatonTool {
  
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
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("HierAutomaton DSL Tool", HierAutomatonTool.class.getName());
    Log.info("------------------", HierAutomatonTool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTStateMachine ast = parse(model);
    Log.info(model + " parsed successfully!", HierAutomatonTool.class.getName());
    
    // --------------------------------------------------------
    // execute a generation process
    Log.info("Writing the parsed automaton into File:", HierAutomatonTool.class.getName());
    
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
      HierAutomatonParser parser = new HierAutomatonParser();
      Optional<ASTStateMachine> optStateMachine = parser.parse(model);
      
      if (!parser.hasErrors() && optStateMachine.isPresent()) {
        return optStateMachine.get();
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
