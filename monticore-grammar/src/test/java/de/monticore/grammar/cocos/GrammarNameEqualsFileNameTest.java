/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GrammarNameEqualsFileNameTest {

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
  }

  @Test
  public void testInvalidFilename() throws IOException {
    Log.getFindings().clear();
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    parser.parse("src/test/resources/de/monticore/grammar/cocos/invalid/A4003/A4003.mc4");

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4003 The grammar name A4002 must be identical to the file name"
          + " A4003 of the grammar (without its file extension).", f.getMsg());
    }
  }

  @Test
  public void testInvalidPackage() throws IOException {
    Log.getFindings().clear();
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    parser.parse("src/test/resources/de/monticore/grammar/cocos/invalid/A4004/A4004.mc4");

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4004 The package declaration de.monticore.grammar.cocos.invalid.A4003 of the grammar must not"
          + " differ from the package of the grammar file.", f.getMsg());
    }
  }
}
