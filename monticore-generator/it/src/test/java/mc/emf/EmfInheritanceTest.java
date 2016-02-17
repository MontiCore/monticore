/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.emf;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

//import de.monticore.emf.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;

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
  
  @Test
  public void test2() {
    try {
      Optional<ASTAutomaton> transB = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat.aut");
          
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
      if (transB.isPresent() && transC.isPresent()) {
        AST2ModelFiles.get().serializeASTInstance(transB.get(),
            "B");
        AST2ModelFiles.get().serializeASTInstance(transC.get(),
            "C");
  
        AstEmfDiffUtility.printAstDiffsHierarchical(transB.get(), transC.get());
      
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
