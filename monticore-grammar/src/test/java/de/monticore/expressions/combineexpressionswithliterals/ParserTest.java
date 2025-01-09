/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals;

import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ParserTest  {

  @Test
  public void parseBigExpr() throws IOException {

    String expr = "(!(x1 && x2) || !(x1 && x3) || !(x1 && x4) || !(x1 && x5) " +
        "|| !(x1 && x6) || !(x1 && x7) || !(x1 && x8) || !(x1 && x9) " +
        "|| !(x1 && x10) || !(x1 && x11) || !(x1 && x12) || !(x1 && x13) " +
        "|| !(x1 && x14) || !(x1 && x15) " +
        "|| !(x2 && x3) || !(x2 && x4) || !(x2 && x5) || !(x2 && x6) " +
        "|| !(x2 && x7) || !(x2 && x8) || !(x2 && x9) || !(x2 && x10) " +
        "|| !(x2 && x11) || !(x2 && x12) || !(x2 && x13) || !(x2 && x14) " +
        "|| !(x2 && x15)) ";

    Optional<ASTExpression> ast = CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expr);
    assertTrue(ast.isPresent());
  }
  
  @Test
  public void parseBigExpr2() throws IOException {

    String expr = 
        "((f1 + f2) * (f1 + f3) * (f1 + f4) / (f1 + f5) + (f1 + f6) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + f14) +\n" +
            " (f1 + f3) * (f1 + f4) / (f1 + f5) + (f1 + f6) / (f1 + f7) + (f1 + f8) * (f1 + f9) + (f1 + f10) + (f1 + f11) / (f1 + f12) + (f1 + f13) + (f + f14) +\n" +
            "  (f2 + f3) * (f2 + f4) * (f2 + f5) * (f2 + f6) / (f2 + f7) / (f2 + f8) * (f2 + f9) + (f2 + f10) * (f2 + f11) * (f2 + f12) * (f2 + f13) * (f2 + f14))";


    Optional<ASTExpression> ast = CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expr);
    assertTrue(ast.isPresent());
  }

  @Test
  public void parseWithMode() throws IOException {
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    parser.setMode("REGEX");
    parser.parse_StringCharRange("a-c").get();
  }

}