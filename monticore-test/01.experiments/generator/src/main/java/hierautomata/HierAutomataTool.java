package hierautomata;/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import hierautomata._ast.ASTStateMachine;
import hierautomata._parser.HierAutomataParser;
import de.se_rwth.commons.logging.Log;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.GeneratorEngine;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class for the HierAutomata DSL tool.
 */
public class HierAutomataTool extends HierAutomataToolTOP {

  /**
   * Use the single argument for specifying the single input automaton file.
   *
   * @param args
   */
  public static void main(String[] args){
    HierAutomataTool tool = new HierAutomataTool();
    tool.run(args);
  }

  public void run(String[] args) {
    // use normal logging (no DEBUG, TRACE)
    init();
    Log.ensureInitalization();

    Options options = initOptions();

    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }
      //version: when --version
      else if (cmd.hasOption("v")) {
        printVersion();
        //do not continue when help is printed
        return;
      }

      Log.info("HierAutomata DSL Tool", "HierAutomataTool");
      Log.info("------------------", "HierAutomataTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        ASTStateMachine ast = parse(model);
        Log.info(model + " parsed successfully!", "HierAutomataTool");

        // --------------------------------------------------------
        // execute a generation process
        Log.info("Writing the parsed automaton into File:", "HierAutomataTool");

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
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE747 Could not process HierAutomataTool parameters: " + e.getMessage());
    }
  }
}
