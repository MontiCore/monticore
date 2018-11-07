/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import static de.monticore.grammar.symboltable.MCGrammarSymbol.KIND;
import static de.se_rwth.commons.logging.Log.enableFailQuick;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.logging.Log;

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
            globalScope.resolve("de.monticore.statechart.Statechart", KIND);
    assertTrue(oldGrammar.isPresent());


    final Optional<MCGrammarSymbol> newGrammar =
            globalScope.resolve("de.monticore.statechart.Statechart", KIND);
    assertTrue(newGrammar.isPresent());

    // 2 = Statechart grammar symbol and TestLexicals grammar symbol (super grammar of Statechart)
    assertEquals(2, globalScope.getSubScopes().size());
  }


}
