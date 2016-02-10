package de.monticore.emf.util.compare;
/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;

import de.monticore.emf.ASTEObjectImplNode;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstEmfDiffUtility {
  
  public static List<DiffElement> getAstDiffs(ASTEObjectImplNode node1, ASTEObjectImplNode node2)
      throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    return diff.getOwnedElements();
  }
  
  public static void printAstDiffsHierarchical(ASTEObjectImplNode node1, ASTEObjectImplNode node2)
      throws InterruptedException {
      
    // Configure EMF Compare
    // EMFCompare comparator = EMFCompare.builder().build();
    //
    // // Compare the two models
    // DefaultComparisonScope defCompare = new
    // DefaultComparisonScope(transA1.get(), transA2.get(), null);
    //
    // Comparison comparison = comparator.compare(defCompare);
    // for (Diff d : comparison.getDifferences()) {
    // // Prints the results
    // System.err.println("d.getKind(): "+d.getKind());
    // System.err.println("d.getMatch(): " + d.getMatch().toString());
    // System.err.println("State: " + d.getState());
    //
    // }
    
    /* .add("left", EObjectUtil.getLabel(getLeft())) .add("right",
     * EObjectUtil.getLabel(getRight())) .add("origin",
     * EObjectUtil.getLabel(getOrigin())) .add("#differences",
     * getDifferences().size()) .add("#submatches",
     * getSubmatches().size()).toString(); */
    
    // BasicDiagnostic diagn = new
    // Diagnostician().createDefaultDiagnostic(transA1.get());
    // System.err.println(" Diagn: " + diagn);
    
    // Matching model elements
    // MatchModel match = MatchService.doMatch(transA1.get(), transA2.get(),
    // Collections.<String, Object> emptyMap());
    // // Computing differences
    // DiffModel diff = DiffService.doDiff(match, false);
    // // Merges all differences from model1 to model2
    // List<DiffElement> differences = new
    // ArrayList<DiffElement>(diff.getOwnedElements());
    // MergeService.merge(differences, true);
    
    // for(DiffElement diffElement : diff.getDifferences(transA1.get())) {
    // System.err.println(" diffElement: " + diffElement.toString());
    // }
    // System.err.println("::: " + diff.getDifferences(transA1.get()));
    //
    // Prints the results
    // System.out.println("MatchModel :\n");
    // System.out.println(ModelUtils.serialize(match));
    // System.out.println("DiffModel :\n");
    // System.out.println(ModelUtils.serialize(diff));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    
    // Serializes the result as "result.emfdiff" in the directory this class has
    // been called from.
    // System.out.println("saving emfdiff as \"result.emfdiff\""); //$NON-NLS-1$
    // final ModelInputSnapshot snapshot =
    // DiffFactory.eINSTANCE.createModelInputSnapshot();
    // snapshot.setDate(Calendar.getInstance().getTime());
    // snapshot.setMatch(match);
    // snapshot.setDiff(diff);
    // ModelUtils.save(snapshot, "result.emfdiff"); //$NON-NLS-1$
    
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    System.err.println("Compare: " + node1 + "\n vs. " + node2);
    for (DiffElement e : diff.getOwnedElements()) {
      System.err.println("\nChanges: ");
      int spaces = 1;
      for (EObject contents : e.eContents()) {
        printDiff((DiffElement) contents, spaces);
      }
    }
  }
  
  public static void printAllAstDiffs(ASTEObjectImplNode node1, ASTEObjectImplNode node2)
      throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    System.err.println("\nAll changes : ");
    for (DiffElement diffElement : diff.getDifferences(node2)) {
      System.err.println(" : " + diffElement.toString());
    }
  }
  
  public static EList<DiffElement> getAllAstDiffs(ASTEObjectImplNode node1,
      ASTEObjectImplNode node2) throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    return diff.getDifferences(node2);
  }
  
  public static void printDiff(DiffElement diff, int ls) {
    System.err.println(String.format("%" + ls + "s", " ") + " - " + diff + " :");
    Iterator<DiffElement> it = diff.getSubDiffElements().iterator();
    while (it.hasNext()) {
      DiffElement dw = it.next();
      if (dw.getSubDiffElements().size() != 0) {
        printDiff(dw, ls + 2);
      }
      else {
        System.err.println(String.format("%" + (ls + 2) + "s", " ") + " - " + dw);
      }
    }
  }
  
}
