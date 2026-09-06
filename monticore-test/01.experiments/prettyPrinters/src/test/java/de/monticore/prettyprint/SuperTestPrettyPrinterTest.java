// (c) https://github.com/MontiCore/monticore
package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.supertestprettyprinters.SuperTestPrettyPrintersMill;
import de.monticore.supertestprettyprinters._prettyprint.SuperTestPrettyPrintersFullPrettyPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test the PrettyPrinter Generation when it comes to overriden productions
 */
@TestWithMCLanguage(SuperTestPrettyPrintersMill.class)
public class SuperTestPrettyPrinterTest extends PPTestClass {

  @Override
  protected String fullPrettyPrint(ASTNode node){
    return  (new SuperTestPrettyPrintersFullPrettyPrinter(new IndentPrinter())).prettyprint(node);
  }

  @Override
  protected String fullPrettyPrintV2(ASTNode node) {
    return new SuperTestPrettyPrintersFullPrettyPrinter(new FormattingPrinter(new IFormatter.DefaultIFormatter()), true).prettyprint(node);
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
