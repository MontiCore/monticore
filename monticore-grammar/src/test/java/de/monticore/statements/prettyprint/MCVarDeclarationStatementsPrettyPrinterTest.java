/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTMCVarDeclarationStatementsNode;
import de.monticore.statements.testmcvardeclarationstatements._parser.TestMCVarDeclarationStatementsParser;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsDelegatorVisitor;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCVarDeclarationStatementsPrettyPrinterTest {

  private TestMCVarDeclarationStatementsParser parser = new TestMCVarDeclarationStatementsParser();

  private PPDelegator prettyPrinter = new PPDelegator(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }


  @Test
  public void testLocalVariableDeclaration() throws IOException {
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("List a = b, c = d");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    prettyPrinter.handle(ast);
    String output = prettyPrinter.getPrinter().getContent();

    result = parser.parse_StringLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  class PPDelegator extends TestMCVarDeclarationStatementsDelegatorVisitor {

    protected PPDelegator realThis;

    protected IndentPrinter printer;

    public PPDelegator(IndentPrinter printer) {
      this.realThis = this;
      this.printer = printer;
      setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
      setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
      setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
      setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    }

    public IndentPrinter getPrinter() {
      return this.printer;
    }

    public String prettyprint(ASTMCVarDeclarationStatementsNode a) {
      getPrinter().clearBuffer();
      a.accept(getRealThis());
      return getPrinter().getContent();
    }

    @Override
    public PPDelegator getRealThis() {
      return realThis;
    }
  }

}
