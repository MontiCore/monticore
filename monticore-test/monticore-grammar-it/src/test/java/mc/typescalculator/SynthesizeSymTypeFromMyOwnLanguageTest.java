/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._parser.MyOwnLanguageParser;
import mc.typescalculator.myownlanguage._symboltable.IMyOwnLanguageGlobalScope;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SynthesizeSymTypeFromMyOwnLanguageTest {

  protected MyOwnLanguageParser parser = new MyOwnLanguageParser();
  protected TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromMyOwnLanguage(),null);
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup() {
    MyOwnLanguageMill.reset();
    MyOwnLanguageMill.init();
    BasicSymbolsMill.initializePrimitives();
    initilizeGenerics();
  }

  protected void initilizeGenerics() {
    IMyOwnLanguageGlobalScope gs = MyOwnLanguageMill.globalScope();
    gs.add(buildGeneric("Map", "K", "V"));
    gs.add(buildGeneric("List", "T"));
    gs.add(buildGeneric("Set", "T"));
    gs.add(buildGeneric("Optional", "T"));
  }

  protected static TypeSymbol buildGeneric(String rawName, String... typeParamNames) {
    // Raw type without type parameters
    OOTypeSymbol genericType = MyOwnLanguageMill.oOTypeSymbolBuilder()
      .setSpannedScope(MyOwnLanguageMill.scope())
      .setName(rawName)
      .setFullName(rawName)
      .build();

    // Add type parameters
    Arrays.stream(typeParamNames)
      .map(SynthesizeSymTypeFromMyOwnLanguageTest::buildTypeParam)
      .forEach(genericType::addTypeVarSymbol);

    return genericType;
  }

  protected static TypeVarSymbol buildTypeParam(String typeParamName) {
    return MyOwnLanguageMill.typeVarSymbolBuilder()
      .setName(typeParamName)
      .setFullName(typeParamName)
      .setSpannedScope(MyOwnLanguageMill.scope())
      .build();
  }

  @Test
  public void testMCCollectionTypes() throws IOException {
    Optional<ASTMCType> type = parser.parse_StringMCType("List<int>");
    Assertions.assertTrue(type.isPresent());
    type.get().setEnclosingScope(MyOwnLanguageMill.globalScope());
    ((ASTMCListType)(type.get())).getMCTypeArgument().getMCTypeOpt().get().setEnclosingScope(MyOwnLanguageMill.globalScope());
    Assertions.assertEquals("List<int>", tc.symTypeFromAST(type.get()).printFullName());
  }

  @Test
  public void testUnitTypes() throws IOException {
    Optional<ASTMinuteType> type = parser.parse_StringMinuteType("min");
    Assertions.assertTrue(type.isPresent());
    // pretend to use the scope genitor
    type.get().setEnclosingScope(MyOwnLanguageMill.globalScope());
    Assertions.assertEquals("min", tc.symTypeFromAST(type.get()).print());
  }


}
