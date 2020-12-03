/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symbols2json;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import mc.feature.symbols2json.sub.SubMill;
import mc.feature.symbols2json.sub._symboltable.ISubScope;
import mc.feature.symbols2json.sub._symboltable.SubScopeDeSer;
import mc.feature.symbols2json.sub._symboltable.SubSymbols2Json;
import mc.feature.symbols2json.sup1.Sup1Mill;
import mc.feature.symbols2json.sup2.Sup2Mill;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class STPForLanguageCompositionTest {

  @BeforeClass
  public static void initLog(){
    Log.enableFailQuick(false);
  }

  @Test
  public void testGetAndSetJsonPrinter(){
    SubSymbols2Json symTabPrinter = new SubSymbols2Json();
    JsonPrinter printer = symTabPrinter.getJsonPrinter();
    assertNotNull(printer);
    symTabPrinter.setJsonPrinter(new JsonPrinter());
    assertNotEquals(printer, symTabPrinter.getJsonPrinter());
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
    SubScopeDeSer deSer = new SubScopeDeSer();
    String serialized =  deSer.serialize(scope);
    assertTrue(serialized.contains("symbols"));
    assertTrue(serialized.contains("\"name\":\"a\""));
    assertTrue(serialized.contains("\"name\":\"b\""));
    assertTrue(serialized.contains("\"name\":\"b2\""));
    assertTrue(serialized.contains("\"name\":\"c\""));
    assertTrue(serialized.contains("\"name\":\"c2\""));
    assertTrue(serialized.contains("\"name\":\"c3\""));
  }

  @Test
  public void testSecondConstructor(){
    JsonPrinter printer = new JsonPrinter();
    SubSymbols2Json symTabPrinter = new SubSymbols2Json(printer);
    assertEquals(printer,symTabPrinter.getJsonPrinter());
  }


}
