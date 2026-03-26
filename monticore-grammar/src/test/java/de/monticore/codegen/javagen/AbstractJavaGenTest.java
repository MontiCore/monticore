// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.codegen.CodeGenerator;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractJavaGenTest extends AbstractMCTest {

  protected JShell jshell;

  @BeforeEach
  void beforeEach() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    DefsTypesForTests.set_boxedPrimitives();
    DefsTypesForTests.set_thePrimitives();

    addMaxValueToInteger();

    SymTypeRelations.init();
    OOWithinScopeBasicSymbolsResolver.init();
    OOWithinTypeBasicSymbolsResolver.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();

    jshell = JShell.create();
  }

  protected void addMaxValueToInteger() {
    FieldSymbol maxValueField = DefsTypesForTests.field("MAX_VALUE", DefsTypesForTests._intSymType);
    maxValueField.setIsStatic(true);
    DefsTypesForTests.inScope(
        CombineExpressionsWithLiteralsMill.globalScope().resolveType("java.lang.Integer").get().getSpannedScope(),
        maxValueField
    );
  }

  protected CodeGenerator createCodeGenerator() {
    return new CombineExpressionWithLiteralsCodeGenerator(new IndentPrinter());
  }

  /**
   * Evaluates an expression.
   * First, generates Java code.
   * Then, generates required functional interfaces.
   * Ultimately, evaluates the Java code using the JShell API.
   *
   * @param javaStr The java expression.
   * @return A List of {@see jdk.shell.SnippetEvent}s.
   */
  protected List<SnippetEvent> evalJava(String javaStr) {
    String[] classpaths = System.getProperty("java.class.path")
        .split(File.pathSeparator);
    for (String classpath : classpaths) {
      getJShell().addToClasspath(classpath);
    }
    return getJShell().eval(javaStr + ";");
  }

  protected void checkValue(String exprStr, String expectedValue) {
    String javaStr = generateJavaFromExpression(exprStr);
    List<SnippetEvent> events = evalJava(javaStr);
    assertEquals(1, events.size(),
        "More/Less than exactly one events where triggered by the evaluation."
    );
    SnippetEvent event = events.get(0);
    assertEquals(Snippet.Status.VALID, event.status(),
        "JShell failed:" + System.lineSeparator()
            + printDiags(event.snippet()) + System.lineSeparator()
            + "Expression:" + System.lineSeparator() + javaStr
    );
    assertEquals(expectedValue, event.value(),
        "The evaluated Java code does not match the expected output."
            + " Expression:" + System.lineSeparator() + javaStr
    );
  }

  protected String generateJavaFromExpression(String exprStr) {
    ASTExpression astExpr = parseExpr(exprStr);

    generateScopes(astExpr);
    SymTypeExpression type = TypeCheck3.typeOf(astExpr);
    assertNoFindings();
    assertFalse(type.isObscureType());

    CodeGenerator generator = createCodeGenerator();
    generator.generateCode(astExpr);
    String javaExpr = generator.getPrinter().getContent();
    assertNoFindings();

    return javaExpr;
  }

  protected String generateJavaFromType(String typeStr) {
    ASTMCType astType = parseMCType(typeStr);

    generateScopes(astType);
    SymTypeExpression type = TypeCheck3.symTypeFromAST(astType);
    assertNoFindings();
    assertFalse(type.isObscureType());

    CodeGenerator generator = createCodeGenerator();
    generator.generateCode(astType);
    return generator.getPrinter().getContent();
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
    // complete the symbol table
    expr.accept(getSymbolTableCompleter());
  }

  protected void generateScopes(ASTMCType mcType) {
    // create an expression to contain the type
    // currently (MC 7.8) lambda expressions are the only expressions
    // which can directly contain MCTypes
    ASTLambdaExpression lambda = CombineExpressionsWithLiteralsMill
        .lambdaExpressionBuilder()
        .setLambdaParameters(
            CombineExpressionsWithLiteralsMill.lambdaParametersBuilder()
                .setLambdaParametersList(List.of(
                    CombineExpressionsWithLiteralsMill.lambdaParameterBuilder()
                        .setName("parameter")
                        .setMCType(mcType)
                        .build()
                ))
                .build()
        )
        .setLambdaBody(
            CombineExpressionsWithLiteralsMill.lambdaExpressionBodyBuilder()
                .setExpression(
                    CombineExpressionsWithLiteralsMill.literalExpressionBuilder()
                        .setLiteral(
                            CombineExpressionsWithLiteralsMill
                                .natLiteralBuilder()
                                .setDigits("8243721")
                                .build()
                        )
                        .build()
                )
                .setType(SymTypeExpressionFactory.createPrimitive("int"))
                .build()
        )
        .build();
    // create a root
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(lambda)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
  }

  // Parse a String expression of the according language
  protected Optional<ASTExpression> parseStringExpr(String exprStr) {
    try {
      return CombineExpressionsWithLiteralsMill.parser()
          .parse_StringExpression(exprStr);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

  // Parse a String type identifier of the according language
  protected Optional<ASTMCType> parseStringMCType(String mcTypeStr) {
    try {
      return CombineExpressionsWithLiteralsMill.parser()
          .parse_StringMCType(mcTypeStr);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

  protected ASTExpression parseExpr(String exprStr) {
    Optional<ASTExpression> astExpression = parseStringExpr(exprStr);
    assertNoFindings();
    assertTrue(astExpression.isPresent());
    return astExpression.get();
  }

  protected ASTMCType parseMCType(String typeStr) {
    Optional<ASTMCType> mcType = parseStringMCType(typeStr);
    assertNoFindings();
    assertTrue(mcType.isPresent());
    return mcType.get();
  }

  protected ITraverser getSymbolTableCompleter() {
    CombineExpressionsWithLiteralsTraverser combinedScopesCompleter =
        CombineExpressionsWithLiteralsMill.traverser();
    combinedScopesCompleter.add4LambdaExpressions(
        new LambdaExpressionsSTCompleteTypes2()
    );
    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    combinedScopesCompleter.add4OCLExpressions(oclExprCompleter);
    combinedScopesCompleter.setOCLExpressionsHandler(oclExprCompleter);
    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    combinedScopesCompleter.add4SetExpressions(setExprCompleter);
    combinedScopesCompleter.setSetExpressionsHandler(setExprCompleter);
    return combinedScopesCompleter;
  }

  protected JShell getJShell() {
    return jshell;
  }

  protected String printDiags(Snippet snippet) {
    return getJShell().diagnostics(snippet)
        .map(diag ->
            "Diag<" + diag.getStartPosition()
                + ", " + diag.getEndPosition() + ">: "
                + diag.getMessage(null)
        )
        .collect(Collectors.joining(System.lineSeparator()));
  }

}
