/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata7.Automata7Mill;
import automata7._ast.ASTAutomaton;
import automata7._parser.Automata7Parser;
import automata7._symboltable.IAutomata7ArtifactScope;
import automata7._symboltable.IAutomata7GlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class JavaAndAutTool {

  public static IAutomata7ArtifactScope createJavaAndAutSymTab(String model, MCPath symbolPath) {
    ASTAutomaton ast = parseAut(model);
    IAutomata7GlobalScope gs = Automata7Mill.globalScope();
    for (Path p : symbolPath.getEntries()) {
      gs.getSymbolPath().addEntry(p);
    }
    gs.addAdaptedStimulusSymbolResolver(new AutomataResolver(symbolPath));

    return Automata7Mill.scopesGenitorDelegator().createFromAST(ast);
  }

  public static ASTAutomaton parseAut(String model) {
    try {
      Automata7Parser parser = new Automata7Parser();
      Optional<ASTAutomaton> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
      }
      Log.error("0xEE84E Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE64E Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
}
