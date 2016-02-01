/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.compare.EmfDiffTest;
import de.monticore.grammar.concepts.antlr.antlr._ast.AntlrResourceController;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.GrammarResourceController;
import de.monticore.grammar.grammar_withconcepts._ast.Grammar_WithConceptsResourceController;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.java.javadsl._ast.JavaDSLResourceController;
import de.monticore.lexicals.lexicals._ast.LexicalsResourceController;
import de.monticore.literals.literals._ast.LiteralsResourceController;
import de.monticore.types.types._ast.TypesResourceController;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class GrammaDiffsTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Test
  public  void test() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
     try {
     
      LexicalsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      LiteralsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      TypesResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      JavaDSLResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      AntlrResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      GrammarResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      Grammar_WithConceptsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      
      
      String path1 = "de/monticore/emf/Automaton.mc4";
      String path2 = "de/monticore/emf/Automaton2.mc4";
      Optional<ASTMCGrammar> transB = new Grammar_WithConceptsParser().parse("src/test/resources/" + path1);
      if (transB.isPresent()) {
        MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/Automaton_mc4");
      }
      else {
        System.err.println("Missed");
      }
      
      Optional<ASTMCGrammar> transC = new Grammar_WithConceptsParser().parse("src/test/resources/" + path2);
      if (transB.isPresent()) {
        MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/Automaton2_mc4");
      }
      else {
        System.err.println("Missed");
      }
      
//      // Matching model elements
//      MatchModel match = MatchService.doMatch(transC.get(), transB.get(), Collections.<String, Object> emptyMap());
//      // Computing differences
//      DiffModel diff = DiffService.doDiff(match, false);
//      // Merges all differences from model1 to model2
//      List<DiffElement>  differences = new ArrayList<DiffElement>(diff.getOwnedElements());
//      System.err.println("Compare: " + path2 + "  vs. " + path1);
//      for (DiffElement e : differences) {
//        System.err.println("\nChanges: " );
//        for (EObject contents : e.eContents()) {
//          printDiffs((DiffElement)contents, new StringBuffer(" "));
//        }
//      }
//      //MergeService.merge(differences, true);
//      System.err.println("\nAll changes : ");
//      for(DiffElement diffElement : diff.getDifferences(transC.get())) {
//        System.err.println(" : " + diffElement.toString());
//      }
//      
      EmfDiffTest.printDiff(transC.get(), transB.get());
    }
    catch (RecognitionException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
