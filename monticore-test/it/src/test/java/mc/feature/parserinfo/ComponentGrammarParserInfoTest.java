/* (c) https://github.com/MontiCore/monticore */
package mc.feature.parserinfo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.parserinfo.parserinfocomponentgrammartest.ParserInfoComponentGrammarTestMill;
import mc.feature.parserinfo.parserinfocomponentgrammartest._parser.ParserInfoComponentGrammarTestParserInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestWithMCLanguage(ParserInfoComponentGrammarTestMill.class)
public class ComponentGrammarParserInfoTest {
  @Test
  public void testMethodsExistAndReturnFalse(){
    // Since this is a component grammar, no actual parser is generated => all methods need to return false
    // BUT: it can still be used to formulate general syntax highlighting or autocompletion rules in the Language Server with the static delegate pattern
    assertFalse(ParserInfoComponentGrammarTestParserInfo.stateDefinesName(0));

    assertFalse(ParserInfoComponentGrammarTestParserInfo.stateHasUsageNameName(0));
    assertFalse(ParserInfoComponentGrammarTestParserInfo.stateHasUsageNameIsSym(0));
    assertFalse(ParserInfoComponentGrammarTestParserInfo.stateHasUsageNameNoSym(0));

    assertFalse(ParserInfoComponentGrammarTestParserInfo.stateReferencesIsSymSymbol(0));
  }
}
