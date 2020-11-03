/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcarraystatements._ast.ASTMCArrayStatementsNode;
import de.monticore.statements.testmcarraystatements._parser.TestMCArrayStatementsParser;
import de.monticore.statements.testmcarraystatements._visitor.TestMCArrayStatementsDelegatorVisitor;
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

public class MCArrayStatementsPrettyPrinterTest {

  private TestMCArrayStatementsParser parser = new TestMCArrayStatementsParser();

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
  public void testArrayInit() throws IOException {
    Optional<ASTArrayInit> result = parser.parse_StringArrayInit("{a, b, foo}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayInit ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayInit(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testArrayDeclaratorId() throws IOException {
    Optional<ASTArrayDeclaratorId> result = parser.parse_StringArrayDeclaratorId("a [] []");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayDeclaratorId ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDeclaratorId(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  class PPDelegator extends TestMCArrayStatementsDelegatorVisitor {

    protected PPDelegator realThis;

    protected IndentPrinter printer;

    public PPDelegator(IndentPrinter printer) {
      this.realThis = this;
      this.printer = printer;
      setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
      setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
      setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
      setMCArrayStatementsVisitor(new MCArrayStatementsPrettyPrinter(printer));
      setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    }

    public IndentPrinter getPrinter() {
      return this.printer;
    }

    public String prettyprint(ASTMCArrayStatementsNode a) {
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
