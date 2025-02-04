/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions.cocos;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsCoCoChecker;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.check.types3wrapper.TypeCheck3AsIDerive;
import de.monticore.types.check.types3wrapper.TypeCheck3AsISynthesize;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LiteralAssignmentMatchesRegExExpressionCoCoTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    new CombineExpressionsWithLiteralsTypeTraverserFactory()
        .initTypeCheck3();
  }

  @Test
  public void testCorrectAssignments() throws IOException {
    testValid("R\"hello\"", "t = \"hello\"");
    testValid("R\"a|b\" ", "t =  \"a\"");
    testValid("R\"a|b\" ", "t =  \"b\"");
    testValid("R\"a[b]\" ", "t =  \"ab\"");
    testValid("R\"a[bc]d\" ", "t =  \"abd\"");
    testValid("R\"a[bc]d\" ", "t =  \"acd\"");
    testValid("R\"a[b.c]\" ", "t =  \"a.\"");
    testValid("R\"a[b-c]\" ", "t =  \"ab\"");
    testValid("R\"a(?:bcd)e\" ", "t =  \"abcde\"");
    testValid("R\"a(b|c)\\1d\" ", "t =  \"abbd\"");
    testValid("R\"a(b|c)\\1d\" ", "t =  \"accd\"");
    testValid("R\"abZ\" ", "t =  \"abZ\"");
    testValid("R\"...\" ", "t =  \"abc\"");
    testValid("R\"...\" ", "t =  \"123\"");
    testValid("R\"...\" ", "t =  \"z9x\"");
    testValid("R\"0129\" ", "t =  \"0129\"");
    testValid("R\"^(b|c)d\" ", "t =  \"bd\"");
    testValid("R\"a(bc)*d\" ", "t =  \"abcbcbcd\"");
    testValid("R\"a(b|c){2,33}d\" ", "t =  \"abbbbbbcd\"");
    testValid("R\"a(b|c){4}d\" ", "t =  \"acbbcd\"");
    testValid("R\"a\\p{Lower}b\" ", "t =  \"azb\"");
    testValid("R\"a\\w\\Bc\" ", "t =  \"abc\"");
  }

  @Test
  public void testIncorrectAssignments() throws IOException {
    testInvalid("R\"hello\"", "t = \"hi\"");
    testInvalid("R\"a|b\" ", "t =  \"c\"");
    testInvalid("R\"a[b]\" ", "t =  \"aa\"");
    testInvalid("R\"a[bc]d\" ", "t =  \"adc\"");
    testInvalid("R\"a[bc]d\" ", "t =  \"ad\"");
    testInvalid("R\"a[b.c]\" ", "t =  \"a\"");
    testInvalid("R\"a[b-c]\" ", "t =  \"lol\"");
    testInvalid("R\"a(?:bcd)e\" ", "t =  \"ae\"");
    testInvalid("R\"a(b|c)\\1d\" ", "t =  \"test\"");
    testInvalid("R\"a(b|c)\\1d\" ", "t =  \"the\"");
    testInvalid("R\"abZ\" ", "t =  \"cake\"");
    testInvalid("R\"...\" ", "t =  \"is\"");
    testInvalid("R\"...\" ", "t =  \"a\"");
    testInvalid("R\"...\" ", "t =  \"liee\"");
    testInvalid("R\"0129\" ", "t =  \"\"");
    testInvalid("R\"^(b|c)d\" ", "t =  \"aaaaaaaaaa\"");
    testInvalid("R\"a(bc)*d\" ", "t =  \"baby\"");
    testInvalid("R\"a(b|c){2,33}d\" ", "t =  \"dont\"");
    testInvalid("R\"a(b|c){4}d\" ", "t =  \"hurt\"");
    testInvalid("R\"a\\p{Lower}b\" ", "t =  \"me\"");
    testInvalid("R\"a\\w\\Bc\" ", "t =  \"ende\"");
  }

  protected void testValid(String type, String exprStr) throws IOException {
    check(type, exprStr);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    Log.clearFindings();
  }

  protected void testInvalid(String type, String exprStr) throws IOException {
    check(type, exprStr);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xFD724"));
    Log.clearFindings();
  }

  protected void check(String type, String exprStr) throws IOException {
    CombineExpressionsWithLiteralsMill.globalScope().clear();

    CombineExpressionsWithLiteralsTypeTraverserFactory factory =
        new CombineExpressionsWithLiteralsTypeTraverserFactory();
    Type4Ast type4Ast = new Type4Ast();

    Optional<ASTMCType> optType = CombineExpressionsWithLiteralsMill
        .parser()
        .parse_StringMCType(type);
    Assertions.assertTrue(optType.isPresent());

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
    Assertions.assertTrue(exprOpt.isPresent());

    generateScopes(exprOpt.get());

    Assertions.assertTrue(Log.getFindings().isEmpty());
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
