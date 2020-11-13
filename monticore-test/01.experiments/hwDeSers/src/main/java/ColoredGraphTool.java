/* (c) https://github.com/MontiCore/monticore */

import coloredgraph.ColoredGraphMill;
import coloredgraph._ast.ASTGraph;
import coloredgraph._parser.ColoredGraphParser;
import coloredgraph._symboltable.ColoredGraphScopeDeSer;
import coloredgraph._symboltable.ColoredGraphSymbolTableCreatorDelegator;
import coloredgraph._symboltable.IColoredGraphArtifactScope;
import coloredgraph._symboltable.IColoredGraphGlobalScope;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.util.Optional;

/**
 * Small tool for storing symbol tables of colored graphs
 * <p>
 * and also includes a main function
 */
public class ColoredGraphTool {

  /**
   * Use the single argument for specifying the single input colored graph file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xAE749 Please specify only one single path to the input model.");
      return;
    }
    Log.info("ColoredGraph DSL Tool", "ColoredGraphTool");
    Log.info("------------------", "ColoredGraphTool");
    String model = args[0];

    // parse the model and create the AST representation
    final ASTGraph ast = parse(model);
    Log.info(model + " parsed successfully!", "ColoredGraphTool");

    // instantiate symbol table:
    ColoredGraphMill.coloredGraphGlobalScope().setModelFileExtension("cg");
    final ColoredGraphSymbolTableCreatorDelegator stc = ColoredGraphMill
        .coloredGraphSymbolTableCreatorDelegator();
    final IColoredGraphArtifactScope symTab = stc.createFromAST(ast);

    Log.info("------------------", "ColoredGraphTool");

    // store symbol table
    new ColoredGraphScopeDeSer().store(symTab, "target/" + model + "sym");

  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTGraph parse(String model) {
    try {
      ColoredGraphParser parser = new ColoredGraphParser();
      Optional<ASTGraph> optAST = parser.parse(model);

      if (!parser.hasErrors() && optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("0xAE849 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xAE649 Failed to parse " + model, e);
    }
    return null;
  }

}
