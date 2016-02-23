/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.emf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.GrammarPackage;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class GrammarSerDeserTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSerializeDesirializeASTMCGrammarInstance() {
    try {
      
      String path1 = "de/monticore/emf/Automaton.mc4";
      Optional<ASTMCGrammar> automatonGrammar = new Grammar_WithConceptsParser()
          .parse("src/test/resources/" + path1);
      assertTrue(automatonGrammar.isPresent());
      AST2ModelFiles.get().serializeASTInstance(automatonGrammar.get(), "Automaton");
      
      EObject deserAutomatonGrammar = AST2ModelFiles.get().deserializeASTInstance("ASTMCGrammar_Automaton",
          GrammarPackage.eINSTANCE);
      assertNotNull(deserAutomatonGrammar);
      assertTrue(deserAutomatonGrammar instanceof ASTMCGrammar);
      
      assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
      assertEquals("Automaton", ((ASTMCGrammar) deserAutomatonGrammar).getName());
      
      List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
          (ASTMCGrammar) deserAutomatonGrammar);
      assertTrue(diffs.isEmpty());
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
}
