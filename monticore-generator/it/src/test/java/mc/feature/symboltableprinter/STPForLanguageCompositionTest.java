/* (c) https://github.com/MontiCore/monticore */
package mc.feature.symboltableprinter;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;
import mc.feature.symboltableprinter.symboltableprintersub.SymbolTablePrinterSubMill;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubScope;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubScopeDeSer;
import mc.feature.symboltableprinter.symboltableprintersub._symboltable.SymbolTablePrinterSubSymbolTablePrinter;
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
    SymbolTablePrinterSubSymbolTablePrinter symTabPrinter = new SymbolTablePrinterSubSymbolTablePrinter();
    JsonPrinter printer = symTabPrinter.getJsonPrinter();
    assertNotNull(printer);
    symTabPrinter.setJsonPrinter(new JsonPrinter());
    assertNotEquals(printer, symTabPrinter.getJsonPrinter());
  }

  @Test
  public void testSerializeLocalSymbols(){
    //create scope with symbols of the grammar SymbolTablePrinterSub and both of its supergrammars
    SymbolTablePrinterSubScope scope = SymbolTablePrinterSubMill
        .symbolTablePrinterSubScopeBuilder().setName("alphabet").build();
    scope.add(SymbolTablePrinterSup1Mill.aSymbolBuilder().setName("a").build());
    scope.add(SymbolTablePrinterSup2Mill.bSymbolBuilder().setName("b").build());
    scope.add(SymbolTablePrinterSubMill.cSymbolBuilder().setName("c").build());

    System.out.println("=== Before serialization ===");
    //serialize symbols and assert that the serialized String contains all the symbols
    SymbolTablePrinterSubScopeDeSer deSer = new SymbolTablePrinterSubScopeDeSer();
    String serialized =  deSer.serialize(scope);
    System.out.println("=== Serialized: "+serialized+". ===");
    assertTrue(serialized.contains("cSymbols"));
    assertTrue(serialized.contains("\"name\":\"alphabet.c\""));
    assertTrue(serialized.contains("bSymbols"));
    assertTrue(serialized.contains("\"name\":\"alphabet.b\""));
    assertTrue(serialized.contains("aSymbols"));
    assertTrue(serialized.contains("\"name\":\"alphabet.a\""));
  }

  @Test
  public void testSecondConstructor(){
    JsonPrinter printer = new JsonPrinter();
    SymbolTablePrinterSubSymbolTablePrinter symTabPrinter = new SymbolTablePrinterSubSymbolTablePrinter(printer);
    assertEquals(printer,symTabPrinter.getJsonPrinter());
  }


}
