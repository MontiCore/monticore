/* (c) https://github.com/MontiCore/monticore */
package mc.feature.parserinfo;

import mc.feature.parserinfo.parserinfocomponentgrammartest._parser.ParserInfoComponentGrammarTestParserInfo;

import static org.junit.Assert.assertFalse;

public class ComponentGrammarParserInfoTest {

  public void init(){
    ParserInfoComponentGrammarTestParserInfo.init();
  }

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
