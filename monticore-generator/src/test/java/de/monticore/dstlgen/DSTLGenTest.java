/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen;

import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyGlobalScope;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by
 *
 */
public class DSTLGenTest {

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGenerateDSTLGrammar() throws Exception {
    Log.clearFindings();
    Log.enableFailQuick(false); // otherwise gradle fails to acknowledge fails

    IGrammarFamilyGlobalScope scope = GrammarFamilyMill.globalScope();
    System.err.println(scope.getSymbolPath());
    scope.clear();
    BasicSymbolsMill.initializePrimitives();
    scope.setSymbolPath(new MCPath(Paths.get("src/test/resources")));
    scope.setFileExt("mc4");

    // test grammar symbol
    final MCGrammarSymbol grammarSymbol = scope.resolveMCGrammar("mc.testcases.statechart.Statechart").orElse(null);
    assertNotNull(grammarSymbol);
    assertNotNull(grammarSymbol.getAstGrammar());
    DSTLGenScript cli = new DSTLGenScript();
//    cli.initGenerator(grammarSymbol.getAstGrammar().get(),new File("target/generated-test-sources/testgen"));
//    cli.generateDSTL(grammarSymbol.getAstGrammar().get(), Optional.empty());

    GrammarFamilyGlobalScope scope2 = new GrammarFamilyGlobalScope(new MCPath(Paths.get("target/generated-test-sources/testgen"), Paths.get("src/test/resources" )), "mc4");

    // test grammar symbol
    final MCGrammarSymbol tfGrammarSymbol = scope2.resolveMCGrammar("mc.testcases.statechart.tr.StatechartTR").orElse(null);
    assertNotNull(tfGrammarSymbol);
    assertNotNull(tfGrammarSymbol.getAstGrammar());

    GrammarCoCos coCos = new GrammarCoCos();

    Grammar_WithConceptsCoCoChecker checker = coCos.getCoCoChecker();
    checker.checkAll(tfGrammarSymbol.getAstGrammar().get());

    assertEquals("The log contains errors: " + Log.getFindings(), 0, Log.getErrorCount());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
