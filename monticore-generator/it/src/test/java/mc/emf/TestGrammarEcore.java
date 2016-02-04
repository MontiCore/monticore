/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.emf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.grammar.grammar_withconcepts._ast.ASTMCConcept;
import mc.grammar.grammar_withconcepts._ast.Grammar_WithConceptsPackage;
import mc.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class TestGrammarEcore {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMCGrammar() {
    try {
      
      AST2ModelFiles.get().serializeAST(Grammar_WithConceptsPackage.eINSTANCE);
      
      Optional<ASTMCConcept> grammar = new Grammar_WithConceptsParser()
          .parse("src/test/resources/mc/grammar/Automaton.mc4");
      assertTrue(grammar.isPresent());
      
      AST2ModelFiles.get().serializeASTInstance(grammar.get(), "Automaton");
      
    }
    catch (RecognitionException | IOException e) {
      fail(e.getMessage());
    }
  }
}
