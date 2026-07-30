/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsCoCoChecker;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
public class LiteralAssignmentMatchesRegExExpressionCoCoTest {

  static Stream<Arguments> testValidArgs() {
    return Stream.of(Arguments.of("R\"hello\"", "t = \"hello\""),
    Arguments.of("R\"a|b\" ", "t =  \"a\""),
    Arguments.of("R\"a|b\" ", "t =  \"b\""),
    Arguments.of("R\"a[b]\" ", "t =  \"ab\""),
    Arguments.of("R\"a[bc]d\" ", "t =  \"abd\""),
    Arguments.of("R\"a[bc]d\" ", "t =  \"acd\""),
    Arguments.of("R\"a[b.c]\" ", "t =  \"a.\""),
    Arguments.of("R\"a[b-c]\" ", "t =  \"ab\""),
    Arguments.of("R\"a(?:bcd)e\" ", "t =  \"abcde\""),
    Arguments.of("R\"a(b|c)\\1d\" ", "t =  \"abbd\""),
    Arguments.of("R\"a(b|c)\\1d\" ", "t =  \"accd\""),
    Arguments.of("R\"abZ\" ", "t =  \"abZ\""),
    Arguments.of("R\"...\" ", "t =  \"abc\""),
    Arguments.of("R\"...\" ", "t =  \"123\""),
    Arguments.of("R\"...\" ", "t =  \"z9x\""),
    Arguments.of("R\"0129\" ", "t =  \"0129\""),
    Arguments.of("R\"^(b|c)d\" ", "t =  \"bd\""),
    Arguments.of("R\"a(bc)*d\" ", "t =  \"abcbcbcd\""),
    Arguments.of("R\"a(b|c){2,33}d\" ", "t =  \"abbbbbbcd\""),
    Arguments.of("R\"a(b|c){4}d\" ", "t =  \"acbbcd\""),
    Arguments.of("R\"a\\p{Lower}b\" ", "t =  \"azb\""),
    Arguments.of("R\"a\\w\\Bc\" ", "t =  \"abc\""));
  }
  
  static Stream<Arguments> testInvalidArgs() {
    return Stream.of(Arguments.of("R\"hello\"", "t = \"hi\""),
    Arguments.of("R\"a|b\" ", "t =  \"c\""),
    Arguments.of("R\"a[b]\" ", "t =  \"aa\""),
    Arguments.of("R\"a[bc]d\" ", "t =  \"adc\""),
    Arguments.of("R\"a[bc]d\" ", "t =  \"ad\""),
    Arguments.of("R\"a[b.c]\" ", "t =  \"a\""),
    Arguments.of("R\"a[b-c]\" ", "t =  \"lol\""),
    Arguments.of("R\"a(?:bcd)e\" ", "t =  \"ae\""),
    Arguments.of("R\"a(b|c)\\1d\" ", "t =  \"test\""),
    Arguments.of("R\"a(b|c)\\1d\" ", "t =  \"the\""),
    Arguments.of("R\"abZ\" ", "t =  \"cake\""),
    Arguments.of("R\"...\" ", "t =  \"is\""),
    Arguments.of("R\"...\" ", "t =  \"a\""),
    Arguments.of("R\"...\" ", "t =  \"liee\""),
    Arguments.of("R\"0129\" ", "t =  \"\""),
    Arguments.of("R\"^(b|c)d\" ", "t =  \"aaaaaaaaaa\""),
    Arguments.of("R\"a(bc)*d\" ", "t =  \"baby\""),
    Arguments.of("R\"a(b|c){2,33}d\" ", "t =  \"dont\""),
    Arguments.of("R\"a(b|c){4}d\" ", "t =  \"hurt\""),
    Arguments.of("R\"a\\p{Lower}b\" ", "t =  \"me\""),
    Arguments.of("R\"a\\w\\Bc\" ", "t =  \"ende\""));
  }

  @ParameterizedTest
  @MethodSource("testValidArgs")
  public void testValid(String type, String exprStr) throws IOException {
    check(type, exprStr);
  }
  
  @ParameterizedTest
  @MethodSource("testInvalidArgs")
  public void testInvalid(String type, String exprStr) throws IOException {
    check(type, exprStr);
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith("0xFD724"));
  }

  protected void check(String type, String exprStr) throws IOException {
    CombineExpressionsWithLiteralsMill.globalScope().clear();

    CombineExpressionsWithLiteralsTypeTraverserFactory factory =
        new CombineExpressionsWithLiteralsTypeTraverserFactory();
    Type4Ast type4Ast = new Type4Ast();

    Optional<ASTMCType> optType = CombineExpressionsWithLiteralsMill
        .parser()
        .parse_StringMCType(type);
    assertTrue(optType.isPresent());

    SymTypeExpression typeExpression = TypeCheck3.symTypeFromAST(optType.get());
    assertFalse(typeExpression.isObscureType());

    CombineExpressionsWithLiteralsMill
        .globalScope()
        .add(CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
        .setName("t")
        .setType(typeExpression)
        .build());

    Optional<ASTExpression> exprOpt = CombineExpressionsWithLiteralsMill
        .parser().parse_StringExpression(exprStr);
    assertTrue(exprOpt.isPresent());

    generateScopes(exprOpt.get());

    assertTrue(Log.getFindings().isEmpty());
    getChecker().checkAll(exprOpt.get());
  }

  protected AssignmentExpressionsCoCoChecker getChecker() {
    AssignmentExpressionsCoCoChecker checker =
        new AssignmentExpressionsCoCoChecker();
    checker.addCoCo(new LiteralAssignmentMatchesRegExExpressionCoCo());
    return checker;
  }

  protected void generateScopes(ASTExpression expr) {
    // create a root
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(expr)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
  }
}
