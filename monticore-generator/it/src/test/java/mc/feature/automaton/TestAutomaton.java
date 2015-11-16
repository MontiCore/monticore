/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.automaton;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.automaton.automaton._ast.ASTTransition;
import mc.feature.automaton.automaton._ast.AutomatonNodeFactory;
import mc.feature.automaton.automaton._parser.AutomatonParserFactory;
import mc.feature.automaton.automaton._parser.TransitionMCParser;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class TestAutomaton {
  
  /**
   * TODO: Write me!
   * @param args
   */
  public static void main(String[] args) {
    Slf4jLog.init();
    Log.enableFailQuick(false);
    TestAutomatonResourceController.getInstance().serializeAstToECoreModelFile();
    ASTTransition transMy = AutomatonNodeFactory.createASTTransition("myfrom", "myactivate", "myto");
    TestAutomatonResourceController.getInstance().serializeASTClassInstance(transMy, "My");
  
    TransitionMCParser parser = AutomatonParserFactory.createTransitionMCParser();
    try {
      Optional<ASTTransition> transA = parser.parse(new StringReader("aFrom-aAct>aTo;"));
      if (transA.isPresent()) {
        System.err.println("Transition: " + transA.get());
        TestAutomatonResourceController.getInstance().serializeASTClassInstance(transA.get(), "A");
      } else {
        System.err.println("Missed");
      }
    }
    catch (RecognitionException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
