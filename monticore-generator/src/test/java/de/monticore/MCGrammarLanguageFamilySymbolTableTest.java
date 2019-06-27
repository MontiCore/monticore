/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static de.se_rwth.commons.logging.Log.enableFailQuick;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCGrammarLanguageFamilySymbolTableTest {

  @BeforeClass
  public static void disableFailQuick() {
    enableFailQuick(false);
  }

  @Test
  public void testSymbolTableOfGrammarStatechartDSL() {
    final ModelingLanguageFamily family = new MontiCoreGrammarLanguageFamily();
    final GlobalScope globalScope = new GlobalScope(new ModelPath(get("src/test/resources")), family);

    final Optional<MCGrammarSymbol> oldGrammar =
            globalScope.resolveMCGrammarSymbol("de.monticore.statechart.Statechart");
    assertTrue(oldGrammar.isPresent());


    final Optional<MCGrammarSymbol> newGrammar =
            globalScope.resolveMCGrammarSymbol("de.monticore.statechart.Statechart");
    assertTrue(newGrammar.isPresent());

    // 2 = Statechart grammar symbol and TestLexicals grammar symbol (super grammar of Statechart)
    assertEquals(2, globalScope.getSubScopes().size());
  }


}
