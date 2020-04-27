/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symboltableprinter;

import de.monticore.symboltable.serialization.JsonPrinter;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubScope;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubSymTabMill;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.serialization.SymbolTablePrinterSubSymbolTablePrinter;
import mc.feature.symboltableprinter.symboltableprintersup1._symboltable.SymbolTablePrinterSup1SymTabMill;
import mc.feature.symboltableprinter.symboltableprintersup2._symboltable.SymbolTablePrinterSup2SymTabMill;
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
    SymbolTablePrinterSubScope scope = SymbolTablePrinterSubSymTabMill.symbolTablePrinterSubScopeBuilder().build();
    scope.add(SymbolTablePrinterSup1SymTabMill.aSymbolBuilder().setName("a").build());
    scope.add(SymbolTablePrinterSup2SymTabMill.bSymbolBuilder().setName("b").build());
    scope.add(SymbolTablePrinterSubSymTabMill.cSymbolBuilder().setName("c").build());

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
