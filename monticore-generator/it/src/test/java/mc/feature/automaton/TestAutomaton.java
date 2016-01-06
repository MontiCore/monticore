/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.automaton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.compare.diff.merge.service.MergeService;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.util.Diagnostician;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.automaton.automaton._ast.ASTAutomaton;
import mc.feature.automaton.automaton._ast.ASTTransition;
import mc.feature.automaton.automaton._ast.AutomatonNodeFactory;
import mc.feature.automaton.automaton._parser.AutomatonParser;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class TestAutomaton {
  
  /**
   * TODO: Write me!
   * 
   * @param args
   */
  public static void main(String[] args) {
    Slf4jLog.init();
    Log.enableFailQuick(false);
//    TestAutomatonResourceController.getInstance().serializeAstToECoreModelFile("models/automaton/");
//    ASTTransition transMy = AutomatonNodeFactory.createASTTransition("myfrom", "myactivate",
//        "myto");
//    TestAutomatonResourceController.getInstance().serializeASTClassInstance(transMy, "models/automaton/My");
//    
//    try {
//      Optional<ASTTransition> transA1 = new AutomatonParser()
//          .parseString_Transition("aFrom-aAct>aTo;");
//      if (transA1.isPresent()) {
//        System.err.println("Transition: " + transA1.get());
//        TestAutomatonResourceController.getInstance().serializeASTClassInstance(transA1.get(), "models/automaton/A1");
//      }
//      else {
//        System.err.println("Missed");
//      }
//      Optional<ASTTransition> transA2 = new AutomatonParser()
//          .parseString_Transition("bFrom-bAct>bTo;");
//      if (transA2.isPresent()) {
//        System.err.println("Transition: " + transA2.get());
//        TestAutomatonResourceController.getInstance().serializeASTClassInstance(transA2.get(), "models/automaton/A2");
//      }
//      else {
//        System.err.println("Missed");
//      }
//      
//      
   // Configure EMF Compare
//      EMFCompare comparator = EMFCompare.builder().build();
//      
//      // Compare the two models
//     DefaultComparisonScope defCompare = new DefaultComparisonScope(transA1.get(), transA2.get(), null);
//      
//     Comparison comparison = comparator.compare(defCompare);
//     for (Diff d : comparison.getDifferences()) {
//       // Prints the results
//       System.err.println("d.getKind(): "+d.getKind());
//       System.err.println("d.getMatch(): " + d.getMatch().toString());
//       System.err.println("State: " + d.getState());
//       
//        }
     
      
      /*
     .add("left", EObjectUtil.getLabel(getLeft()))
     .add("right", EObjectUtil.getLabel(getRight()))
     .add("origin", EObjectUtil.getLabel(getOrigin()))
     .add("#differences", getDifferences().size())
     .add("#submatches", getSubmatches().size()).toString();
      */
      
      
//    BasicDiagnostic diagn = new  Diagnostician().createDefaultDiagnostic(transA1.get());
//      System.err.println(" Diagn: " + diagn);
      
  // Matching model elements
//     MatchModel match = MatchService.doMatch(transA1.get(), transA2.get(), Collections.<String, Object> emptyMap());
//     // Computing differences
//     DiffModel diff = DiffService.doDiff(match, false);
//     // Merges all differences from model1 to model2
//     List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
//     MergeService.merge(differences, true);
     
//     for(DiffElement diffElement : diff.getDifferences(transA1.get())) {
//       System.err.println(" diffElement: " + diffElement.toString());
//     }
//     System.err.println("::: " + diff.getDifferences(transA1.get()));
//      
  // Prints the results
     try {
//       System.out.println("MatchModel :\n"); 
//       System.out.println(ModelUtils.serialize(match));
//       System.out.println("DiffModel :\n"); 
//       System.out.println(ModelUtils.serialize(diff));
//     } catch (IOException e) {
//       e.printStackTrace();
//     }

     // Serializes the result as "result.emfdiff" in the directory this class has been called from.
//     System.out.println("saving emfdiff as \"result.emfdiff\""); //$NON-NLS-1$
//     final ModelInputSnapshot snapshot = DiffFactory.eINSTANCE.createModelInputSnapshot();
//     snapshot.setDate(Calendar.getInstance().getTime());
//     snapshot.setMatch(match);
//     snapshot.setDiff(diff);
//     ModelUtils.save(snapshot, "result.emfdiff"); //$NON-NLS-1$
      
      
      Optional<ASTAutomaton> transB = new AutomatonParser().parse("src/test/resources/mc/automaton/Testautomat.aut");
      if (transB.isPresent()) {
        System.err.println("ASTAutomaton: " + transB.get());
        TestAutomatonResourceController.getInstance().serializeASTClassInstance(transB.get(), "models/automaton/B");
      }
      else {
        System.err.println("Missed");
      }
      
      Optional<ASTAutomaton> transC = new AutomatonParser().parse("src/test/resources/mc/automaton/Testautomat2.aut");
      if (transC.isPresent()) {
        System.err.println("ASTAutomaton: " + transC.get());
        TestAutomatonResourceController.getInstance().serializeASTClassInstance(transC.get(), "models/automaton/B");
      }
      else {
        System.err.println("Missed");
      }
      
      // Matching model elements
      MatchModel match = MatchService.doMatch(transB.get(), transC.get(), Collections.<String, Object> emptyMap());
      // Computing differences
      DiffModel diff = DiffService.doDiff(match, false);
      // Merges all differences from model1 to model2
      List<DiffElement>  differences = new ArrayList<DiffElement>(diff.getOwnedElements());
      //MergeService.merge(differences, true);
      
      for(DiffElement diffElement : diff.getDifferences(transB.get())) {
        System.err.println(" diffElement: " + diffElement.toString());
      }
      System.err.println("::: " + diff.getDifferences(transB.get()));
      
    }
    catch (RecognitionException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
}
