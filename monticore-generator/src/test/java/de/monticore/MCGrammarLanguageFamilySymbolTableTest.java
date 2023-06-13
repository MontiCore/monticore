/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCGrammarLanguageFamilySymbolTableTest {

  @Before
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
    GrammarFamilyMill.reset();
    GrammarFamilyMill.init();
  }
  
  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final IGrammar_WithConceptsGlobalScope globalScope = Grammar_WithConceptsMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get("src/test/resources")));
    globalScope.setFileExt("mc4");

    final Optional<MCGrammarSymbol> oldGrammar =
            globalScope.resolveMCGrammar("de.monticore.statechart.Statechart");
    assertTrue(oldGrammar.isPresent());


    final Optional<MCGrammarSymbol> newGrammar =
            globalScope.resolveMCGrammar("de.monticore.statechart.Statechart");
    assertTrue(newGrammar.isPresent());

    // 2 = Statechart grammar symbol and TestLexicals grammar symbol (super grammar of Statechart)
    assertEquals(1, globalScope.getSubScopes().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }


}
