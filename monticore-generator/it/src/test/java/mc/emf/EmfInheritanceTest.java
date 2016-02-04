/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.emf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import de.monticore.emf.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;
import de.monticore.emf.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;
//import de.monticore.emf.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class EmfInheritanceTest {
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * TODO: Write me!
   * 
   * @param args
   */
  @Test
  public void test() {
    
 //   ResourceController.getInstance().serializeAstToECoreModelFile(ActionAutomatonPackage.eINSTANCE);
    
    // String path1 = "de/monticore/emf/Automaton.mc4";
    // String path2 = "de/monticore/emf/Automaton2.mc4";
    // Optional<ASTMCGrammar> transB = new
    // Grammar_WithConceptsParser().parse("src/test/resources/" + path1);
    // if (transB.isPresent()) {
    // MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(),
    // "models/Automaton_mc4");
    // }
    // else {
    // System.err.println("Missed");
    // }
    //
    // Optional<ASTMCGrammar> transC = new
    // Grammar_WithConceptsParser().parse("src/test/resources/" + path2);
    // if (transB.isPresent()) {
    // MCGrammarResourceController.getInstance().serializeASTClassInstance(transB.get(),
    // "models/Automaton2_mc4");
    // }
    // else {
    // System.err.println("Missed");
    // }
    
  }
  
  @Test
  public void test2() {
    try {
      Optional<ASTAutomaton> transB = new FlatAutomatonParser()
          .parse("src/test/resources/mc/automaton/Testautomat.aut");
      if (transB.isPresent()) {
        System.err.println("ASTAutomaton: " + transB.get());
        AST2ModelFiles.get().serializeASTInstance(transB.get(),
            "B");
      }
      else {
        System.err.println("Missed");
      }
      
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/automaton/Testautomat2.aut");
      if (transC.isPresent()) {
        System.err.println("ASTAutomaton: " + transC.get());
        AST2ModelFiles.get().serializeASTInstance(transC.get(),
            "C");
      }
      else {
        System.err.println("Missed");
      }
      
      // Matching model elements
      MatchModel match = MatchService.doMatch(transB.get(), transC.get(),
          Collections.<String, Object> emptyMap());
      // Computing differences
      DiffModel diff = DiffService.doDiff(match, false);
      // Merges all differences from model1 to model2
      List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
      // MergeService.merge(differences, true);
      
      for (DiffElement diffElement : diff.getDifferences(transB.get())) {
        System.err.println(" diffElement: " + diffElement.toString());
      }
      System.err.println("::: " + diff.getDifferences(transB.get()));
      
    }
    catch (RecognitionException | IOException e)
    
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (
    
    InterruptedException e)
    
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
