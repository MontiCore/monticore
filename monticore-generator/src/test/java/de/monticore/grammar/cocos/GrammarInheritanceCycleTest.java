/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by
 *
 * @author KH
 */
public class GrammarInheritanceCycleTest extends CocoTest{

  private final String MESSAGE = " The grammar A4023%s introduces an inheritance cycle.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4023.A4023";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new GrammarInheritanceCycle());
  }

  @Test
  public void testInvalid() {
    Optional<ASTMCGrammar> grammar = MCGrammarParser
        .parse(Paths.get("src/test/resources/cocos/invalid/A4023/A4023.mc4"));
    
    Log.getFindings().clear();
    checker.handle(grammar.get());
    
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    for (Finding f : Log.getFindings()) {
      assertEquals(String.format(GrammarInheritanceCycle.ERROR_CODE + MESSAGE, ""), f.getMsg());
    }
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

  @Test
  public void testCorrectB(){
    testValidGrammar("cocos.valid.enum.Enum", checker);
  }

}
