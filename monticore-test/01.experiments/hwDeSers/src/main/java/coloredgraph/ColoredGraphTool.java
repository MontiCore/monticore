package coloredgraph;/* (c) https://github.com/MontiCore/monticore */

import coloredgraph._ast.ASTGraph;
import coloredgraph._symboltable.*;
import coloredgraph._visitor.ColoredGraphTraverser;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Small tool for storing symbol tables of colored graphs
 * <p>
 * and also includes a main function
 */
public class ColoredGraphTool extends ColoredGraphToolTOP {

  /**
   * Use the single argument for specifying the single input colored graph file.
   *
   * @param args
   */
  public static void main(String[] args) {
    ColoredGraphTool tool = new ColoredGraphTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args){
    init();
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

      Log.info("ColoredGraph DSL Tool", "ColoredGraphTool");
      Log.info("------------------", "ColoredGraphTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        // parse the model and create the AST representation
        ASTGraph ast = parse(model);
        Log.info(model + " parsed successfully!", "ColoredGraphTool");

        // instantiate symbol table:
        ColoredGraphMill.globalScope().setFileExt("cg");
        IColoredGraphArtifactScope symTab = createSymbolTable(ast);
        completeSymbolTable(ast, symTab);

        Log.info("------------------", "ColoredGraphTool");

        storeSymbols(symTab, "target/" + model + "sym");
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE749 Could not process HierAutomataTool parameters: " + e.getMessage());
    }
  }

  public void completeSymbolTable(ASTGraph ast, IColoredGraphArtifactScope symTab){
    ColoredGraphSTCompleteTypes stCompleteTypes = new ColoredGraphSTCompleteTypes();
    ColoredGraphTraverser traverser = ColoredGraphMill.traverser();
    traverser.add4ColoredGraph(stCompleteTypes);
    ast.accept(traverser);
    symTab.setNumberOfColors(stCompleteTypes.getAllColors().size());
  }

}
