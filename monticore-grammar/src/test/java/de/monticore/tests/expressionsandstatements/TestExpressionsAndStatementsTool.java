// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.CodeGenerator;
import de.monticore.expressions.assignmentexpressions.interpreter.AssignmentExpressionsInterpreter;
import de.monticore.expressions.bitexpressions.interpreter.BitExpressionsInterpreter;
import de.monticore.expressions.commonexpressions.interpreter.CommonExpressionsInterpreter;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionCalculationLogVisitor;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionsBasisInterpreter;
import de.monticore.expressions.lambdaexpressions.interpreter.LambdaExpressionsInterpreter;
import de.monticore.interpreter.util.InterpreterAccess4Tests;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.literals.mccommonliterals.interpreter.MCCommonLiteralsInterpreter;
import de.monticore.ocl.optionaloperators.interpreter.OptionalOperatorsInterpreter;
import de.monticore.ocl.setexpressions.interpreter.SetExpressionsInterpreter;
import de.monticore.runtime.junit.AbstractTestLanguageTool;
import de.monticore.runtime.junit.TestJavaCompiler;
import de.monticore.statements.mcassertstatements.interpreter.MCAssertStatementsInterpreter;
import de.monticore.statements.mccommonstatements.interpreter.MCCommonStatementsInterpreter;
import de.monticore.statements.mclowlevelstatements.interpreter.MCLowLevelStatementsInterpreter;
import de.monticore.statements.mcvardeclarationstatements.interpreter.MCVarDeclarationStatementsInterpreter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._symboltable.IExpressionsAndStatementsArtifactScope;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.tests.expressionsandstatements.codegen.javagen.ExpressionsAndStatementsJavaGenerator;
import de.monticore.tests.expressionsandstatements.interpreter.ExpressionsAndStatementsInterpreter;
import de.monticore.tests.expressionsandstatements.types3.ExpressionsAndStatementsTypeCheck3;
import de.monticore.values.MCValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * {@link ExpressionsAndStatementsTool}, wrapped to simplify writing tests.
 * <p>
 * Additionally, provides (test-specific) access
 * to Java-generation and interpretation.
 */
public class TestExpressionsAndStatementsTool
    extends AbstractTestLanguageTool<ASTBehaviorInput, IExpressionsAndStatementsArtifactScope> {

  // enable for experimenting:
  // it will print the generated code before evaluation
  protected static final boolean PRINT_GENERATED_CODE = false;

  protected static final String GENERATED_METHOD_NAME =
      "executeSystemUnderTest";

  protected static final Path GENERATED_SOURCE_DIR =
      Path.of("target", "codegen-test", "java");

  protected static final Path GENERATED_CLASS_DIR =
      Path.of("target", "codegen-test", "class");

  protected CodeGenerator generator =
      new ExpressionsAndStatementsJavaGenerator();

  protected TestJavaCompiler javaCompiler = new TestJavaCompiler();

  public TestExpressionsAndStatementsTool() {
    // assure the directories exist
    try {
      Files.createDirectories(GENERATED_SOURCE_DIR);
      Files.createDirectories(GENERATED_CLASS_DIR);
    }
    catch (IOException e) {
      fail(e);
    }
  }

  public static void initLanguage() {
    // Mill
    ExpressionsAndStatementsMill.reset();
    ExpressionsAndStatementsMill.init();
    ExpressionsAndStatementsMill.globalScope().clear();

    // TypeCheck
    ExpressionsAndStatementsTypeCheck3.init();

    // Symbols
    BasicSymbolsMill.initializePrimitives();
  }

  @Override
  protected Optional<ASTBehaviorInput> _parse(String modelStr) throws IOException {
    return ExpressionsAndStatementsMill.parser().parse_String(modelStr);
  }

  @Override
  protected IExpressionsAndStatementsArtifactScope _createSymbolTable(ASTBehaviorInput ast) {
    return ExpressionsAndStatementsMill.scopesGenitorDelegator()
        .createFromAST(ast);
  }

  @Override
  protected void _runBetween_createSymbolTable_completeSymbolTable(ASTBehaviorInput ast) {
    IExpressionsAndStatementsArtifactScope as =
        (IExpressionsAndStatementsArtifactScope) ast.getEnclosingScope();
    // default imports
    as.addImports(new ImportStatement("java.lang", true));
    as.addImports(new ImportStatement("java.util", true));
  }

  @Override
  protected void _completeSymbolTable(ASTBehaviorInput ast) {
    new ExpressionsAndStatementsTool().completeSymbolTable(ast);
  }

  @Override
  protected void _runPost_completeSymbolTable(ASTBehaviorInput ast) {
    new ExpressionsAndStatementsTool().runDefaultCoCos(ast);
  }

  // JavaGen

  /**
   * The result of AST to Java generation and execution
   *
   * @param sourceFile the file containing the generated Java source code
   * @param result     the result of the execution of the behavior
   */
  public record JavaGenResult(
      Path sourceFile,
      Object result
  ) {
    public String getSourceCode() {
      try {
        return Files.readString(sourceFile, StandardCharsets.UTF_8);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * generates a .java artifact, which is loaded and executed.
   *
   * @param ast       the behavior that will be turned into a .java artifact
   * @param className the name of the class to generate
   * @return
   */
  public JavaGenResult generateJavaAndRun(
      ASTBehaviorInput ast,
      String className
  ) {
    Path javaSource = createJavaSource(ast, className);
    Class<?> generatedClass = javaCompiler.compile(javaSource.toFile());
    Object result = invokeGeneratedMethod(generatedClass);
    return new JavaGenResult(javaSource, result);
  }

  /**
   * creates a .java artifact.
   * <p>
   * It can be evaluated using
   * {@link #invokeGeneratedMethod(Class)}
   * after compiling with, e.g.,
   * {@link TestJavaCompiler#compile(File)}.
   *
   * @param ast the behavior that will be turned into a .java artifact
   * @return the Path of the created .java artifact
   */
  public Path createJavaSource(ASTBehaviorInput ast, String className) {
    String javaStatementsStr = generateCode(ast);
    String javaReturnType = ast.isPresentExpression() ?
        getJavaTypePrint(normalize(typeOf(ast.getExpression()))) :
        "void";
    assertNoFindings();
    // assure that we don't have any comments
    String prettyPrintedModel =
        ExpressionsAndStatementsMill.prettyPrint(ast, false);

    String javaMethodStr = "public static " + javaReturnType + " "
        + GENERATED_METHOD_NAME + "() {" + lineSeparator()
        + javaStatementsStr
        + "}";
    // add the option to execute the generated code directly per main()
    String mainMethod;
    if (!javaReturnType.equals("void")) {
      mainMethod = String.format("""
          public static void main(String[] args) {
            Object result = %s();
            System.out.println(result.toString());
          }
          """, GENERATED_METHOD_NAME
      );
    }
    else {
      mainMethod = String.format("""
          public static void main(String[] args) {
            %s();
          }
          """, GENERATED_METHOD_NAME
      );
    }
    String javaClassStr = "public class " + className + " {"
        + lineSeparator() + javaMethodStr + lineSeparator()
        + lineSeparator() + mainMethod + lineSeparator()
        + "}" + lineSeparator();

    // prints the generated code,
    // can help during development
    if (PRINT_GENERATED_CODE) {
      System.out.println("***** Generated Code *****");
      System.out.println(javaClassStr);
      System.out.println("**************************");
    }
    // add a comment of the original input
    // to better identify the generated artifacts
    String artifactFullStr = javaClassStr + lineSeparator()
        + "// ********** Original Model **********" + lineSeparator()
        + "/*" + lineSeparator()
        + prettyPrintedModel + lineSeparator()
        + "*/" + lineSeparator();

    // store as a file
    Path sourceFile = GENERATED_SOURCE_DIR.resolve(className + ".java");
    assertDoesNotThrow(() ->
        Files.writeString(sourceFile, artifactFullStr, StandardCharsets.UTF_8)
    );

    return sourceFile;
  }

  /**
   * produces Java code given a model.
   * HookPoint
   *
   * @param node the node to generate code from
   * @return the generated Java code
   */
  protected String generateCode(ASTNode node) {
    return generator.generateCode(node);
  }

  /**
   * compiles the Java file and loads the class.
   *
   * @param javaFile the file to compile
   * @return the public class of the Java file
   */
  public Class<?> compile(Path javaFile) {
    return javaCompiler.compile(javaFile.toFile());
  }

  /**
   * Executes the test method and returns the result.
   * <p>
   * The class should be generated by
   * {@link #createJavaSource(ASTBehaviorInput, String)}.
   *
   * @param clazz the class containing the method
   * @return the result of the method call
   */
  public Object invokeGeneratedMethod(Class<?> clazz) {
    try {
      return getGeneratedMethodInvoker(clazz).call();
    }
    catch (Exception e) {
      fail(e);
      return null;
    }
  }

  /**
   * Returns the behavior of the generated model as a {@link Callable}
   *
   * @param clazz the generated class
   * @return the behavior in executable form
   */
  public Callable<Object> getGeneratedMethodInvoker(Class<?> clazz) {
    try {
      Method method = clazz.getDeclaredMethod(GENERATED_METHOD_NAME);
      return () -> {
        try {
          return method.invoke(null);
        }
        // unpack InvocationTargetException if possible
        catch (InvocationTargetException ite) {
          if (ite.getCause() instanceof Exception e) {
            throw e;
          }
          else if (ite.getCause() instanceof Error err) {
            throw err;
          }
          else {
            throw ite;
          }
        }
      };
    }
    catch (NoSuchMethodException e) {
      fail(e);
      return null;
    }
  }

  // Interpreter

  /**
   * Given a model, this will interpret it and return the result.
   *
   * @param ast     the model to interpret
   * @param withLog whether interpreter with Log should be used
   * @return the interpreted result
   */
  public MCValue interpret(ASTBehaviorInput ast, boolean withLog) {
    // setup
    InterpreterAccess4Tests interpreter = withLog ?
        initializeInterpreterWithLog() :
        initializeInterpreter();
    // actually interpret
    MCValue value = interpreter.interpretNode(ast);
    assertNoFindings();
    assertNotNull(value);
    return value;
  }

  public MCValue interpret(ASTBehaviorInput ast) {
    return interpret(ast, false);
  }

  protected InterpreterAccess4Tests initializeInterpreter() {
    InterpreterDataForBasicSymbols iData = new InterpreterDataForBasicSymbols();
    ExpressionsAndStatementsTraverser traverser = ExpressionsAndStatementsMill.inheritanceTraverser();
    traverser.setExpressionsBasisHandler(new ExpressionsBasisInterpreter(iData));
    traverser.setCommonExpressionsHandler(new CommonExpressionsInterpreter(iData));
    traverser.setAssignmentExpressionsHandler(new AssignmentExpressionsInterpreter(iData));
    traverser.setMCCommonLiteralsHandler(new MCCommonLiteralsInterpreter(iData));
    traverser.setSetExpressionsHandler(new SetExpressionsInterpreter(iData));
    traverser.setBitExpressionsHandler(new BitExpressionsInterpreter(iData));
    traverser.setLambdaExpressionsHandler(new LambdaExpressionsInterpreter(iData));
    traverser.setOptionalOperatorsHandler(new OptionalOperatorsInterpreter(iData));
    traverser.setMCAssertStatementsHandler(new MCAssertStatementsInterpreter(iData));
    traverser.setMCCommonStatementsHandler(new MCCommonStatementsInterpreter(iData));
    traverser.setMCLowLevelStatementsHandler(new MCLowLevelStatementsInterpreter(iData));
    traverser.setMCVarDeclarationStatementsHandler(new MCVarDeclarationStatementsInterpreter(iData));
    traverser.setExpressionsAndStatementsHandler(new ExpressionsAndStatementsInterpreter(iData));
    InterpreterAccess4Tests access =
        new InterpreterAccess4Tests(traverser, iData);
    return access;
  }

  protected InterpreterAccess4Tests initializeInterpreterWithLog() {
    InterpreterAccess4Tests access = initializeInterpreter();
    ExpressionsAndStatementsTraverser traverser =
        (ExpressionsAndStatementsTraverser) access.getTraverser();
    traverser.add4ExpressionsBasis(
        new ExpressionCalculationLogVisitor(access.getInterpreterData())
    );
    return access;
  }

}
