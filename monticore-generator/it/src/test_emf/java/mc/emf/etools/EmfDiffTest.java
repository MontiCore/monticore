/* (c)  https://github.com/MontiCore/monticore */
package mc.emf.etools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.junit.Test;

import de.monticore.emf.util.compare.AstEmfDiffUtility;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;

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

        assertEquals(12, diffs.size());
        assertEquals("Attribute Name in Testautomat has changed from Testautomat2 to Testautomat",
            diffs.get(0).toString());
        
        assertEquals("The order of the values of reference States have been changed",
            diffs.get(1).toString());
        
        assertEquals("Attribute Initial in a has changed from true to false", diffs.get(2).toString());
        
        assertEquals("Attribute R__final in a has changed from false to true",
            diffs.get(3).toString());
        
        assertEquals("Attribute Initial in c has changed from false to true", diffs.get(4).toString());
    
        assertEquals("Attribute R__final in d has changed from true to false", diffs.get(5).toString());

        assertEquals("Attribute From in x has changed from a to c", diffs.get(6).toString());

        assertEquals("Attribute To in x has changed from c to d", diffs.get(7).toString());
        
        assertEquals("Attribute From in y has changed from c to b", diffs.get(8).toString());

        assertEquals("Attribute To in y has changed from d to a", diffs.get(9).toString());
        
        assertEquals("ASTTransition Activate: y From: d To: d has been added", diffs.get(10).toString());
        
        assertEquals("ASTTransition Activate: x From: b To: a has been removed", diffs.get(11).toString());
       
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
