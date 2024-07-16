/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbols2json;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.symbols2json.sub.SubMill;
import mc.feature.symbols2json.sub._symboltable.ISubScope;
import mc.feature.symbols2json.sub._symboltable.SubSymbols2Json;
import mc.feature.symbols2json.sup1.Sup1Mill;
import mc.feature.symbols2json.sup2.Sup2Mill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class STPForLanguageCompositionTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGetAndSetJsonPrinter(){
    SubSymbols2Json symTabPrinter = new SubSymbols2Json();
    JsonPrinter printer = symTabPrinter.getJsonPrinter();
    Assertions.assertNotNull(printer);
    symTabPrinter.setJsonPrinter(new JsonPrinter());
    Assertions.assertNotEquals(printer, symTabPrinter.getJsonPrinter());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSerializeLocalSymbols(){
    //create scope with symbols of the grammar SymbolTablePrinterSub and both of its supergrammars
    ISubScope scope = SubMill
            .scope();
    scope.setName("alphabet");
    scope.add(Sup1Mill.aSymbolBuilder().setName("a").setType("Type1").build());
    scope.add(Sup2Mill.bSymbolBuilder().setName("b").setType("Type2").build());
    scope.add(Sup2Mill.bSymbolBuilder().setName("b2").setTypeAbsent().build());
    scope.add(SubMill.cSymbolBuilder().setName("c").setTypeList(Lists.newArrayList("Type3", "Type4")).build());
    scope.add(SubMill.cSymbolBuilder().setName("c2").setTypeList(Lists.newArrayList("Type3")).build());
    scope.add(SubMill.cSymbolBuilder().setName("c3").setTypeList(Lists.newArrayList()).build());

    //serialize symbols and assert that the serialized String contains all the symbols
    SubSymbols2Json symbols2JSon = new SubSymbols2Json();
    scope.accept(symbols2JSon.getTraverser());
    String serialized = symbols2JSon.getSerializedString();

    Assertions.assertTrue(serialized.contains("symbols"));
    Assertions.assertTrue(serialized.contains("\"name\":\"a\""));
    Assertions.assertTrue(serialized.contains("\"name\":\"b\""));
    Assertions.assertTrue(serialized.contains("\"name\":\"b2\""));
    Assertions.assertTrue(serialized.contains("\"name\":\"c\""));
    Assertions.assertTrue(serialized.contains("\"name\":\"c2\""));
    Assertions.assertTrue(serialized.contains("\"name\":\"c3\""));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSecondConstructor(){
    JsonPrinter printer = new JsonPrinter();
    SubSymbols2Json symTabPrinter = new SubSymbols2Json(SubMill.traverser(), printer);
    Assertions.assertEquals(printer, symTabPrinter.getJsonPrinter());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
