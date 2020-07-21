/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata7.Automata7Mill;
import automata7._ast.ASTAutomaton;
import automata7._parser.Automata7Parser;
import automata7._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.util.Optional;

public class JavaAndAutTool {

  public static Automata7ArtifactScope createJavaAndAutSymTab(String model, ModelPath modelPath) {
    ASTAutomaton ast = parseAut(model);
    Automata7GlobalScope globalScope = Automata7Mill
        .automata7GlobalScopeBuilder()
        .setModelPath(modelPath)
        .setModelFileExtension("aut")
        .build();
    globalScope.addAdaptedStimulusSymbolResolvingDelegate(new AutomataResolvingDelegate(modelPath));

    //initialize symbol table creators
    Automata7SymbolTableCreator stc = Automata7Mill
        .automata7SymbolTableCreatorBuilder()
        .addToScopeStack(globalScope)
        .build();

    return stc.createFromAST(ast);
  }

  public static ASTAutomaton parseAut(String model) {
    try { Automata7Parser parser = new Automata7Parser() ;
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
