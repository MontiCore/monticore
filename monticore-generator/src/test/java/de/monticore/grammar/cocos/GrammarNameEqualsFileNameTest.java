/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by
 *
 * @author KH
 */
public class GrammarNameEqualsFileNameTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testInvalidFilename(){
    Log.getFindings().clear();
    MCGrammarParser.parse(Paths.get("src/test/resources/cocos/invalid/A4003/A4003.mc4"));

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4003 The grammar name A4002 must be identical to the file name"
          + " A4003 of the grammar (without its file extension).", f.getMsg());
    }
  }

  @Test
  public void testInvalidPackage(){
    Log.getFindings().clear();
    MCGrammarParser.parse(Paths.get("src/test/resources/cocos/invalid/A4004/A4004.mc4"));

    assertFalse(Log.getFindings().isEmpty());
    for(Finding f : Log.getFindings()){
      assertEquals("0"+"xA4004 The package declaration cocos.invalid.A4003 of the grammar must not"
          + " differ from the package of the grammar file.", f.getMsg());
    }
  }
}
