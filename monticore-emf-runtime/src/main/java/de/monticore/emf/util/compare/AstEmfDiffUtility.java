/* (c) https://github.com/MontiCore/monticore */

package de.monticore.emf.util.compare;
/*
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

import de.monticore.emf._ast.ASTENode;

/**
 * Compare utility for two AST instances
 */
public class AstEmfDiffUtility {
  
  public static List<DiffElement> getAstDiffs(ASTENode node1, ASTENode node2)
      throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    return diff.getOwnedElements();
  }
  
  public static void printAstDiffsHierarchical(ASTENode node1, ASTENode node2)
      throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    System.out.println("Compare: " + node1 + "\n vs. " + node2);
    for (DiffElement e : diff.getOwnedElements()) {
      System.out.println("\nChanges: ");
      int spaces = 1;
      for (EObject contents : e.eContents()) {
        printDiff((DiffElement) contents, spaces);
      }
    }
  }
  
  public static void printAllAstDiffs(ASTENode node1, ASTENode node2)
      throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    System.out.println("\nAll changes : ");
    for (DiffElement diffElement : diff.getDifferences(node2)) {
      System.out.println(" : " + diffElement.toString());
    }
  }
  
  public static EList<DiffElement> getAllAstDiffs(ASTENode node1,
      ASTENode node2) throws InterruptedException {
    // Matching model elements
    MatchModel match = MatchService.doMatch(node1, node2, Collections.<String, Object> emptyMap());
    // Computing differences
    DiffModel diff = DiffService.doDiff(match, false);
    // Merges all differences from model1 to model2
    return diff.getDifferences(node2);
  }
  
  public static void printDiff(DiffElement diff, int ls) {
    System.out.println(String.format("%" + ls + "s", " ") + " - " + diff + " :");
    Iterator<DiffElement> it = diff.getSubDiffElements().iterator();
    while (it.hasNext()) {
      DiffElement dw = it.next();
      if (!dw.getSubDiffElements().isEmpty()) {
        printDiff(dw, ls + 2);
      }
      else {
        System.out.println(String.format("%" + (ls + 2) + "s", " ") + " - " + dw);
      }
    }
  }
  
  private AstEmfDiffUtility() {}
  
}
