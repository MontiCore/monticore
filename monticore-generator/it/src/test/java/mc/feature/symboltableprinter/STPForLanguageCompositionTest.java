/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symboltableprinter;

import de.monticore.symboltable.serialization.JsonPrinter;
import mc.feature.symboltableprinter.symboltableprintersub.SymbolTablePrinterSubMill;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubScope;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubSymbolTablePrinter;
import mc.feature.symboltableprinter.symboltableprintersup1.SymbolTablePrinterSup1Mill;
import mc.feature.symboltableprinter.symboltableprintersup2.SymbolTablePrinterSup2Mill;
import org.junit.Test;

import static org.junit.Assert.*;

public class STPForLanguageCompositionTest {

  SymbolTablePrinterSubSymbolTablePrinter symTabPrinter = new SymbolTablePrinterSubSymbolTablePrinter();

  @Test
  public void testGetAndSetJsonPrinter(){
    JsonPrinter printer = symTabPrinter.getJsonPrinter();
    assertNotNull(printer);
    symTabPrinter.setJsonPrinter(new JsonPrinter());
    assertNotEquals(printer, symTabPrinter.getJsonPrinter());
  }

  @Test
  public void testSerializeLocalSymbols(){
    //create scope with symbols of the grammar SymbolTablePrinterSub and both of its supergrammars
    SymbolTablePrinterSubScope scope = SymbolTablePrinterSubMill.symbolTablePrinterSubScopeBuilder().build();
    scope.add(SymbolTablePrinterSup1Mill.aSymbolBuilder().setName("a").build());
    scope.add(SymbolTablePrinterSup2Mill.bSymbolBuilder().setName("b").build());
    scope.add(SymbolTablePrinterSubMill.cSymbolBuilder().setName("c").build());

    //serialize symbols and assert that the serialized String contains all the symbols
    symTabPrinter.serializeLocalSymbols(scope);
    String serialized = symTabPrinter.getSerializedString();
    System.out.println(serialized);
    assertTrue(serialized.contains("cSymbols"));
    assertTrue(serialized.contains("\"name\":\"c\""));
    assertTrue(serialized.contains("bSymbols"));
    assertTrue(serialized.contains("\"name\":\"b\""));
    assertTrue(serialized.contains("aSymbols"));
    assertTrue(serialized.contains("\"name\":\"a\""));
  }

  @Test
  public void testSecondConstructor(){
    JsonPrinter printer = new JsonPrinter();
    SymbolTablePrinterSubSymbolTablePrinter symTabPrinter = new SymbolTablePrinterSubSymbolTablePrinter(printer);
    assertEquals(printer,symTabPrinter.getJsonPrinter());
  }


}
