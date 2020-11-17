/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symboltableprinter;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import mc.feature.symboltableprinter.symboltableprintersub.SymbolTablePrinterSubMill;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.ISymbolTablePrinterSubScope;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubScopeDeSer;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubSymbols2Json;
import mc.feature.symboltableprinter.symboltableprintersup1.SymbolTablePrinterSup1Mill;
import mc.feature.symboltableprinter.symboltableprintersup2.SymbolTablePrinterSup2Mill;
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
    SymbolTablePrinterSubSymbols2Json symTabPrinter = new SymbolTablePrinterSubSymbols2Json();
    JsonPrinter printer = symTabPrinter.getJsonPrinter();
    assertNotNull(printer);
    symTabPrinter.setJsonPrinter(new JsonPrinter());
    assertNotEquals(printer, symTabPrinter.getJsonPrinter());
  }

  @Test
  public void testSerializeLocalSymbols(){
    //create scope with symbols of the grammar SymbolTablePrinterSub and both of its supergrammars
    ISymbolTablePrinterSubScope scope = SymbolTablePrinterSubMill
        .symbolTablePrinterSubScopeBuilder().setName("alphabet").build();
    scope.add(SymbolTablePrinterSup1Mill.aSymbolBuilder().setName("a").build());
    scope.add(SymbolTablePrinterSup2Mill.bSymbolBuilder().setName("b").build());
    scope.add(SymbolTablePrinterSubMill.cSymbolBuilder().setName("c").build());

    //serialize symbols and assert that the serialized String contains all the symbols
    SymbolTablePrinterSubScopeDeSer deSer = new SymbolTablePrinterSubScopeDeSer();
    String serialized =  deSer.serialize(scope);
    assertTrue(serialized.contains("symbols"));
    assertTrue(serialized.contains("\"name\":\"a\""));
    assertTrue(serialized.contains("\"name\":\"b\""));
    assertTrue(serialized.contains("\"name\":\"c\""));
  }

  @Test
  public void testSecondConstructor(){
    JsonPrinter printer = new JsonPrinter();
    SymbolTablePrinterSubSymbols2Json symTabPrinter = new SymbolTablePrinterSubSymbols2Json(printer);
    assertEquals(printer,symTabPrinter.getJsonPrinter());
  }


}
