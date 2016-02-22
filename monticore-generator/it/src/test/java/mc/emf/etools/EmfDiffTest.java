/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.emf.etools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class EmfDiffTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testDiffAutomaton() {
    try {
      Optional<ASTAutomaton> transB = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat.aut");
          
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
      if (transB.isPresent() && transC.isPresent()) {
       
        // Matching model elements
        List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(transB.get(), transC.get());
        
        AstEmfDiffUtility.printAstDiffsHierarchical(transB.get(), transC.get());

        assertEquals(9, diffs.size());
        assertEquals("Attribute Name in Testautomat has changed from Testautomat2 to Testautomat",
            diffs.get(0).toString());
        
        assertEquals("The order of the values of reference States have been changed",
            diffs.get(1).toString());
        
        assertEquals("Attribute R__final in a has changed from false to true",
            diffs.get(2).toString());
            
        assertEquals("Attribute Initial in a has changed from true to false", diffs.get(3).toString());
        
        assertEquals("Attribute Initial in c has changed from false to true", diffs.get(4).toString());
    
        assertEquals("Attribute R__final in d has changed from true to false", diffs.get(5).toString());

        assertEquals("Attribute Activate in b has changed from x to y", diffs.get(6).toString());

        assertEquals("ASTTransition From: d Activate: y To: d has been added", diffs.get(7).toString());
        
        assertEquals("ASTTransition From: c Activate: y To: d has been removed", diffs.get(8).toString());
       
      }
      else {
        fail("Parse errors");
      }
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
}
