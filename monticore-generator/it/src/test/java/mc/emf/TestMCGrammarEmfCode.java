/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.emf;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.grammar.concepts.antlr.antlr._ast.AntlrResourceController;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.GrammarPackage;
import de.monticore.grammar.grammar._ast.GrammarResourceController;
import de.monticore.grammar.grammar_withconcepts._ast.Grammar_WithConceptsPackage;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.java.javadsl._ast.JavaDSLResourceController;
import de.monticore.lexicals.lexicals._ast.LexicalsResourceController;
import de.monticore.types.types._ast.TypesResourceController;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class TestMCGrammarEmfCode {
  
  
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
  public  void test() {
  //  TestAutomatonResourceController.getInstance().serializeAstToECoreModelFile("models/");
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
      
     
      LexicalsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
//      LiteralsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      TypesResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      JavaDSLResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      AntlrResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
      GrammarResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
//      Grammar_WithConceptsResourceController.getInstance().serializeAstToECoreModelFile("grammars_emf/");
//      
   //   ResourceController.getInstance().serializeAstToECoreModelFile(LiteralsPackage.eINSTANCE, "Literals3", "grammars_emf_new/");
      AST2ModelFiles.get().serializeAST(GrammarPackage.eINSTANCE);
      AST2ModelFiles.get().serializeAST(Grammar_WithConceptsPackage.eINSTANCE);
     // ResourceController.getInstance().serializeAstToECoreModelFile(TypesPackage.eINSTANCE, "Types3", "grammars_emf/");
      
      
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
      AstEmfDiffUtility.printAstDiffs(transC.get(), transB.get());
    }
    catch (RecognitionException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
