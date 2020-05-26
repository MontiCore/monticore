/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import mc.typescalculator.myownlanguage._parser.MyOwnLanguageParser;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SynthesizeSymTypeFromMyOwnLanguageTest {

  protected MyOwnLanguageParser parser = new MyOwnLanguageParser();
  protected TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMyOwnLanguage(),null);

  @Test
  public void testMCCollectionTypes() throws IOException {
    Optional<ASTMCType> type = parser.parse_StringMCType("List<int>");
    assertTrue(type.isPresent());
    assertEquals("List<int>",tc.symTypeFromAST(type.get()).print());
  }

  @Test
  public void testUnitTypes() throws IOException {
    Optional<ASTMinuteType> type = parser.parse_StringMinuteType("min");
    assertTrue(type.isPresent());
    assertEquals("min",tc.symTypeFromAST(type.get()).print());
  }


}
