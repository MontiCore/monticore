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

import static de.monticore.grammar.cocos.GrammarInheritanceCycle.ERROR_CODE;
import static de.se_rwth.commons.logging.LogStub.enableFailQuick;
import static de.se_rwth.commons.logging.Log.getFindings;
import static java.lang.String.format;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static parser.MCGrammarParser.parse;

public class GrammarExtensionOnceTest extends CocoTest {

  private final String MESSAGE = GrammarExtensionOnce.ERROR_MSG_FORMAT;
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4150.A4150";

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
    checker.addCoCo(new GrammarExtensionOnce());
  }

  @Test
  public void testInvalid() {
    testInvalidGrammar(grammar, GrammarExtensionOnce.ERROR_CODE, MESSAGE, checker);
    getFindings().clear();
  }

  @Test
  public void testCorrect() {
    // Any grammar that extends another grammar
    testValidGrammar("cocos.valid.SubGrammarWithSymbol", checker);
  }
}
