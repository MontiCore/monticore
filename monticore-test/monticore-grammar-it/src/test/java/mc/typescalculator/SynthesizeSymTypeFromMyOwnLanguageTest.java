/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._parser.MyOwnLanguageParser;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SynthesizeSymTypeFromMyOwnLanguageTest {

  protected MyOwnLanguageParser parser = new MyOwnLanguageParser();
  protected TypeCalculator tc = new TypeCalculator(new SynthesizeSymTypeFromMyOwnLanguage(),null);

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    MyOwnLanguageMill.reset();
    MyOwnLanguageMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testMCCollectionTypes() throws IOException {
    Optional<ASTMCType> type = parser.parse_StringMCType("List<int>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(MyOwnLanguageMill.globalScope());
    assertEquals("List<int>",tc.symTypeFromAST(type.get()).printFullName());
  }

  @Test
  public void testUnitTypes() throws IOException {
    Optional<ASTMinuteType> type = parser.parse_StringMinuteType("min");
    assertTrue(type.isPresent());
    assertEquals("min",tc.symTypeFromAST(type.get()).print());
  }


}
