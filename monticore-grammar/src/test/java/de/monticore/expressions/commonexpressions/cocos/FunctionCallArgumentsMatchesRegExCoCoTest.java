/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsCoCoChecker;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.types3wrapper.TypeCheck3AsIDerive;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.types3.util.DefsTypesForTests;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

public class FunctionCallArgumentsMatchesRegExCoCoTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
  }

  @Test
  public void testCorrectFunctionCallSingleParameter() throws IOException {
    testValid("f(\"hello\")", List.of(List.of("hello")), false);
    testValid("f(\"a\")", List.of(List.of("a|b")), false);
    testValid("f(\"b\")", List.of(List.of("a|b")), false);
    testValid("f(\"ab\")", List.of(List.of("a[b]")), false);
    testValid("f(\"abd\")", List.of(List.of("a[bc]d")), false);
    testValid("f(\"acd\")", List.of(List.of("a[bc]d")), false);
    testValid("f(\"a.\")", List.of(List.of("a[b.c]")), false);
    testValid("f(\"ab\")", List.of(List.of("a[b-c]")), false);
    testValid("f(\"abcde\")", List.of(List.of("a(?:bcd)e")), false);
    testValid("f(\"abbd\")", List.of(List.of("a(b|c)\\1d")), false);
    testValid("f(\"accd\")", List.of(List.of("a(b|c)\\1d")), false);
    testValid("f(\"abZ\")", List.of(List.of("abZ")), false);
    testValid("f(\"abc\")", List.of(List.of("...")), false);
    testValid("f(\"123\")", List.of(List.of("...")), false);
    testValid("f(\"z9x\")", List.of(List.of("...")), false);
    testValid("f(\"0129\")", List.of(List.of("0129")), false);
    testValid("f(\"bd\")", List.of(List.of("^(b|c)d")), false);
    testValid("f(\"abcbcbcd\")", List.of(List.of("a(bc)*d")), false);
    testValid("f(\"abbbbbbcd\")", List.of(List.of("a(b|c){2,33}d")), false);
    testValid("f(\"acbbcd\")", List.of(List.of("a(b|c){4}d")), false);
    testValid("f(\"azb\")", List.of(List.of("a\\p{Lower}b")), false);
    testValid("f(\"abc\")", List.of(List.of("a\\w\\Bc")), false);
  }

  @Test
  public void testCorrectFunctionCallMultipleParameter() throws IOException {
    testValid("f(\"hello\", \"a\")", List.of(List.of("hello", "a|b")), false);
    testValid("f(\"b\", \"ab\")", List.of(List.of("a|b", "a[b]")), false);
    testValid("f(\"abd\", \"acd\")", List.of(List.of("a[bc]d", "a[bc]d")), false);
    testValid("f(\"a.\", \"ab\")", List.of(List.of("a[b.c]", "a[b-c]")), false);
    testValid("f(\"abcde\", \"abbd\")", List.of(List.of("a(?:bcd)e", "a(b|c)\\1d")), false);
    testValid("f(\"accd\", \"abZ\")", List.of(List.of("a(b|c)\\1d", "abZ")), false);
    testValid("f(\"abc\", \"123\")", List.of(List.of("...", "...")), false);
    testValid("f(\"z9x\", \"0129\")", List.of(List.of("...", "0129")), false);
    testValid("f(\"bd\", \"abcbcbcd\")", List.of(List.of("^(b|c)d", "a(bc)*d")), false);
    testValid("f(\"abbbbbbcd\", \"acbbcd\")", List.of(List.of("a(b|c){2,33}d", "a(b|c){4}d")), false);
    testValid("f(\"azb\", \"abc\")", List.of(List.of("a\\p{Lower}b", "a\\w\\Bc")), false);
  }

  @Test
  public void testCorrectFunctionCallMultipleMethods() throws IOException {
    testValid("f(\"hello\")", List.of(List.of("hello"), List.of("hello")), false);
    testValid("f(\"a\")", List.of(List.of("a|b"), List.of("a|b")), false);
    testValid("f(\"b\")", List.of(List.of("a|b"), List.of("a|b")), false);
    testValid("f(\"ab\")", List.of(List.of("a[b]"), List.of("a[b]")), false);
    testValid("f(\"abd\")", List.of(List.of("a[bc]d"), List.of("a[bc]d")), false);
    testValid("f(\"acd\")", List.of(List.of("a[bc]d"), List.of("a[bc]d")), false);
    testValid("f(\"a.\")", List.of(List.of("a[b.c]"), List.of("a[b.c]")), false);
    testValid("f(\"ab\")", List.of(List.of("a[b-c]"), List.of("a[b-c]")), false);
    testValid("f(\"abcde\")", List.of(List.of("a(?:bcd)e"), List.of("a(?:bcd)e")), false);
    testValid("f(\"abbd\")", List.of(List.of("a(b|c)\\1d"), List.of("a(b|c)\\1d")), false);
    testValid("f(\"accd\")", List.of(List.of("a(b|c)\\1d"), List.of("a(b|c)\\1d")), false);
    testValid("f(\"abZ\")", List.of(List.of("abZ"), List.of("abZ")), false);
    testValid("f(\"abc\")", List.of(List.of("..."), List.of("...")), false);
    testValid("f(\"123\")", List.of(List.of("..."), List.of("...")), false);
    testValid("f(\"z9x\")", List.of(List.of("..."), List.of("...")), false);
    testValid("f(\"0129\")", List.of(List.of("0129"), List.of("0129")), false);
    testValid("f(\"bd\")", List.of(List.of("^(b|c)d"), List.of("^(b|c)d")), false);
    testValid("f(\"abcbcbcd\")", List.of(List.of("a(bc)*d"), List.of("a(bc)*d")), false);
    testValid("f(\"abbbbbbcd\")", List.of(List.of("a(b|c){2,33}d"), List.of("a(b|c){2,33}d")), false);
    testValid("f(\"acbbcd\")", List.of(List.of("a(b|c){4}d"), List.of("a(b|c){4}d")), false);
    testValid("f(\"azb\")", List.of(List.of("a\\p{Lower}b"), List.of("a\\p{Lower}b")), false);
    testValid("f(\"abc\")", List.of(List.of("a\\w\\Bc"), List.of("a\\w\\Bc")), false);
  }

  @Test
  @Disabled("There is no implementation for regEx type checking yet.")
  public void testCorrectFunctionCallMultipleMethodsMultipleParameters() throws IOException {
    testValid("f(\"hello\", \"a\")", List.of(List.of("hello", "a|b"), List.of("hello", "a|b")), false);
    testValid("f(\"b\", \"ab\")", List.of(List.of("a|b", "a[b]"), List.of("a?[b]", "a[bc]d?")), false);
    testValid("f(\"abd\", \"acd\")", List.of(List.of("a[bc]d", "a[bc]d"), List.of("a[bc]d", "a[b.c]d")), false);
    testValid("f(\"a.\", \"ab\")", List.of(List.of("a[b.c]", "a[b-c]"), List.of("a[b-c]?\\.", "a[?:bcd.]e?")), false);
    testValid("f(\"abcde\", \"abbd\")", List.of(List.of("a(?:bcd)e", "a(b|c)\\1d"), List.of("a(?:bcd)e", "a(b|c)\\1d")), false);
    testValid("f(\"accd\", \"abZ\")", List.of(List.of("a(b|c)\\1d", "abZ"), List.of("a(b|c)\\1d", "abZ")), false);
    testValid("f(\"abc\", \"123\")", List.of(List.of("...", "..."), List.of("...", "...")), false);
    testValid("f(\"z9x\", \"0129\")", List.of(List.of("...", "0129"), List.of("...", "0129")), false);
    testValid("f(\"bd\", \"abcbcbcd\")", List.of(List.of("^(b|c)d", "a(bc)*d"), List.of("^(b|c)d", "a(bc)*d")), false);
    testValid("f(\"abbbbbbcd\", \"acbbcd\")", List.of(List.of("a(b|c){2,33}d", "a(b|c){4}d"), List.of("a(b|c){2,33}d", "a(b|c){4}d")), false);
    testValid("f(\"azb\", \"abc\")", List.of(List.of("a\\p{Lower}b", "a\\w\\Bc"), List.of("a\\p{Lower}b", "a\\w\\Bc")), false);
  }

  @Test
  public void testCorrectFunctionCallVarArgsParameter() throws IOException {
    testValid("f(\"abc\")", List.of(List.of("a[bd]c", "d[ef]g")), true);
    testValid("f(\"abc\", \"deg\")", List.of(List.of("a[bd]c", "d[ef]g")), true);
    testValid("f(\"abc\", \"deg\", \"dfg\")", List.of(List.of("a[bd]c", "d[ef]g")), true);
  }

  @Test
  public void testIncorrectFunctionCalls() throws IOException {
    testInvalid("f(\"hallo\")", List.of(List.of("hello")), false);
    testInvalid("f(\"hi\", \"hallo\", \"moin\")", List.of(List.of("hi", "hallo", "mion")), false);
    testInvalid("f(\"hallo\")", List.of(List.of("h.lli")), true);
    testInvalid("f(\"hi\", \"hallo\", \"hello\")", List.of(List.of("hi", "hallo")), true);
  }

  protected void testValid(String expression, List<List<String>> functions, boolean varArgs) throws IOException {
    check(expression, functions, varArgs);
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator())));
    Log.clearFindings();
  }

  protected void testInvalid(String expression, List<List<String>> functions, boolean varArgs) throws IOException {
    check(expression, functions, varArgs);
    Assertions.assertTrue(Log.getFindings().stream().anyMatch(
        f -> f.getMsg().startsWith("0xFD725")
    ));
    Log.clearFindings();
  }

  protected void check(String expression, List<List<String>> functions, boolean varArgs) throws IOException {
    CombineExpressionsWithLiteralsMill.globalScope().clear();
    BasicSymbolsMill.initializeString();

    CombineExpressionsWithLiteralsTypeTraverserFactory factory =
        new CombineExpressionsWithLiteralsTypeTraverserFactory();
    Type4Ast type4Ast = new Type4Ast();
    CombineExpressionsWithLiteralsTraverser traverser =
        factory.createTraverser(type4Ast);
    TypeCheck3AsIDerive derive = new TypeCheck3AsIDerive(
        traverser, type4Ast, new CommonExpressionsLValueRelations());

    functions.forEach(parameters -> {
      List<SymTypeExpression> parameterList = parameters.stream()
          .map(SymTypeExpressionFactory::createTypeRegEx)
          .collect(Collectors.toList());

      DefsTypesForTests.inScope(CombineExpressionsWithLiteralsMill.globalScope(),
          DefsTypesForTests.function("f", DefsTypesForTests._voidSymType,
              parameterList, varArgs));
    });

    Optional<ASTExpression> optExpr = CombineExpressionsWithLiteralsMill
        .parser().parse_StringExpression(expression);
    Assertions.assertTrue(optExpr.isPresent());

    ASTExpression expr = optExpr.get();
    generateScopes(expr);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    getChecker(derive).checkAll(expr);
  }

  protected CommonExpressionsCoCoChecker getChecker(IDerive derive) {
    CommonExpressionsCoCoChecker checker = new CommonExpressionsCoCoChecker();
    checker.addCoCo(new FunctionCallArgumentsMatchesRegExCoCo(derive));
    return checker;
  }

  protected void generateScopes(ASTExpression expr) {
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(expr)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
  }

}
