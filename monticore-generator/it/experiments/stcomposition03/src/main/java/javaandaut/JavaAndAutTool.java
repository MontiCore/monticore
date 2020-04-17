/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata6._ast.ASTAutomaton;
import automata6._parser.Automata6Parser;
import automata6._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.util.Optional;

public class JavaAndAutTool {

  public static Automata6ArtifactScope createJavaAndAutSymTab(String model, ModelPath modelPath) {
    ASTAutomaton ast = parseAut(model);
    Automata6GlobalScope globalScope = Automata6SymTabMill.automata6GlobalScopeBuilder()
        .setModelPath(modelPath)
        .setAutomata6Language(new Automata6Language()) //will be removed soon
        .build();
    globalScope.addAdaptedStimulusSymbolResolvingDelegate(new AutomataResolvingDelegate(modelPath));

    //initialize symbol table creators
    Automata6SymbolTableCreator stc = Automata6SymTabMill
        .automata6SymbolTableCreatorBuilder()
        .addToScopeStack(globalScope)
        .build();

    return stc.createFromAST(ast);
  }

  public static ASTAutomaton parseAut(String model) {
    try { Automata6Parser parser = new Automata6Parser() ;
      Optional<ASTAutomaton> optResult = parser.parse(model);

      if (!parser.hasErrors() && optResult.isPresent()) {
        return optResult.get();
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
