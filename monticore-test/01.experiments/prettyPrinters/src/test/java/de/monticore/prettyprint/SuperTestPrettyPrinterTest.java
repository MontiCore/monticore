// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.supertestprettyprinters.SuperTestPrettyPrintersMill;
import de.monticore.supertestprettyprinters._prettyprint.SuperTestPrettyPrintersFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.*;

import java.io.IOException;

/**
 * Test the PrettyPrinter Generation when it comes to overriden productions
 */
public class SuperTestPrettyPrinterTest extends PPTestClass {

  @BeforeClass
  public static void setup() {
    SuperTestPrettyPrintersMill.init();
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @Override
  protected String fullPrettyPrint(ASTNode node){
    return  (new SuperTestPrettyPrintersFullPrettyPrinter(new IndentPrinter())).prettyprint(node);
  }

  @Test
  public void testInterfaceI() throws IOException {
    testPP("InterfaceImpl1", SuperTestPrettyPrintersMill.parser()::parse_StringInterfaceImpl1);
    testPP("InterfaceImpl2", SuperTestPrettyPrintersMill.parser()::parse_StringInterfaceImpl2);
    testPP("InterfaceImpl1", SuperTestPrettyPrintersMill.parser()::parse_StringInterfaceI);
    testPP("InterfaceImpl2", SuperTestPrettyPrintersMill.parser()::parse_StringInterfaceI);
    testPP("a", SuperTestPrettyPrintersMill.parser()::parse_StringProductionFromTestPrettyPrintersA);
    testPP("a", SuperTestPrettyPrintersMill.parser()::parse_StringInterfaceI);
  }

  @Test
  public void testSuperInterface() throws IOException {
    testPP("b", SuperTestPrettyPrintersMill.parser()::parse_StringProductionFromTestPrettyPrintersB);
    testPP("b", SuperTestPrettyPrintersMill.parser()::parse_StringSuperInterfaceI);
  }


}
