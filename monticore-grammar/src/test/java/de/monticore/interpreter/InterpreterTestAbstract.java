// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.monticore.expressions.assignmentexpressions.interpreter.AssignmentExpressionsInterpreter;
import de.monticore.expressions.bitexpressions.interpreter.BitExpressionsInterpreter;
import de.monticore.expressions.cocos.ExpressionValid;
import de.monticore.expressions.commonexpressions.interpreter.CommonExpressionsInterpreter;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionCalculationLogVisitor;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionsBasisInterpreter;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.expressions.lambdaexpressions.interpreter.LambdaExpressionsInterpreter;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.util.AClass;
import de.monticore.interpreter.util.InterpreterAccess4Tests;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.literals.mccommonliterals.interpreter.MCCommonLiteralsInterpreter;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.optionaloperators.interpreter.OptionalOperatorsInterpreter;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.ocl.setexpressions.interpreter.SetExpressionsInterpreter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.statements.mcassertstatements.interpreter.MCAssertStatementsInterpreter;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymTabCompletion;
import de.monticore.statements.mccommonstatements.cocos.AssertIsValid;
import de.monticore.statements.mccommonstatements.cocos.DoWhileConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mccommonstatements.cocos.WhileConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.interpreter.MCCommonStatementsInterpreter;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import de.monticore.statements.mcvardeclarationstatements.interpreter.MCVarDeclarationStatementsInterpreter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.tests.interpretertestlang.InterpreterTestLangMill;
import de.monticore.tests.interpretertestlang._ast.ASTInterpreterInput;
import de.monticore.tests.interpretertestlang._cocos.InterpreterTestLangCoCoChecker;
import de.monticore.tests.interpretertestlang._symboltable.IInterpreterTestLangArtifactScope;
import de.monticore.tests.interpretertestlang._visitor.InterpreterTestLangTraverser;
import de.monticore.tests.interpretertestlang.interpreter.InterpreterTestLangInterpreter;
import de.monticore.tests.interpretertestlang.types3.InterpreterTestLangTypeCheck3;
import de.monticore.types.mcbasictypes.cocos.QualifiedTypeHasNoTypeParameters;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class InterpreterTestAbstract extends AbstractMCTest {

  protected InterpreterAccess4Tests interpreter;

  @BeforeEach
  public void setup() {
    LogStub.initPlusLog();

    // Mill
    InterpreterTestLangMill.reset();
    InterpreterTestLangMill.init();
    InterpreterTestLangMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();

    // TypeCheck
    InterpreterTestLangTypeCheck3.init();
    // Class2MC
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    OOClass2MCResolver resolver = new OOClass2MCResolver();
    globalScope.addAdaptedOOTypeSymbolResolver(resolver);
    globalScope.addAdaptedTypeSymbolResolver(resolver);
    addTestClassesToGlobalScope();

    interpreter = initializeInterpreter();
  }

  protected void addTestClassesToGlobalScope() {
    addClassPathEntry(AClass.class);
    addClassPathEntry(ByteArrayOutputStream.class);
  }

  private void addClassPathEntry(Class<?> clazz) {
    try {
      CodeSource codeSource = clazz
          .getProtectionDomain()
          .getCodeSource();
      if (codeSource == null) {
        return;
      }
      Path classPath = Paths.get(codeSource.getLocation().toURI());
      BasicSymbolsMill.globalScope().getSymbolPath().addEntry(classPath);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  protected InterpreterAccess4Tests initializeInterpreter() {
    InterpreterDataForBasicSymbols iData = new InterpreterDataForBasicSymbols();
    InterpreterTestLangTraverser traverser = InterpreterTestLangMill.inheritanceTraverser();
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
    traverser.setMCVarDeclarationStatementsHandler(new MCVarDeclarationStatementsInterpreter(iData));
    traverser.setInterpreterTestLangHandler(new InterpreterTestLangInterpreter(iData));
    InterpreterAccess4Tests access =
        new InterpreterAccess4Tests(traverser, iData);
    return access;
  }

  protected InterpreterAccess4Tests initializeInterpreterWithLog() {
    InterpreterAccess4Tests access = initializeInterpreter();
    InterpreterTestLangTraverser traverser =
        (InterpreterTestLangTraverser) access.getTraverser();
    traverser.add4ExpressionsBasis(
        new ExpressionCalculationLogVisitor(access.getInterpreterData())
    );
    return access;
  }

  // helper

  protected ASTInterpreterInput parse(String modelStr) {
    try {
      Optional<ASTInterpreterInput> astOpt =
          InterpreterTestLangMill.parser().parse_String(modelStr);
      assertNoFindings();
      assertTrue(astOpt.isPresent());
      return astOpt.get();
    }
    catch (IOException e) {
      fail(e);
      return null;
    }
  }

  protected IInterpreterTestLangArtifactScope runSymTabGenitor(
      ASTInterpreterInput ast
  ) {
    IInterpreterTestLangArtifactScope scope =
        InterpreterTestLangMill.scopesGenitorDelegator().createFromAST(ast);
    assertNoFindings();
    // default import
    scope.addImports(new ImportStatement("java.lang", true));
    scope.addImports(new ImportStatement("java.util", true));
    return scope;
  }

  protected void runSymTabCompleter(
      ASTInterpreterInput ast
  ) {
    InterpreterTestLangTraverser symTabCompleter =
        InterpreterTestLangMill.inheritanceTraverser();

    // Expressions

    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    symTabCompleter.setOCLExpressionsHandler(oclExprCompleter);
    symTabCompleter.add4BasicSymbols(oclExprCompleter);
    symTabCompleter.add4OCLExpressions(oclExprCompleter);

    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    symTabCompleter.setSetExpressionsHandler(setExprCompleter);
    symTabCompleter.add4BasicSymbols(setExprCompleter);
    symTabCompleter.add4SetExpressions(setExprCompleter);

    LambdaExpressionsSTCompleteTypes2 lambdaExprCompleter =
        new LambdaExpressionsSTCompleteTypes2();
    symTabCompleter.add4LambdaExpressions(lambdaExprCompleter);

    // Statements

    MCCommonStatementsSymTabCompletion commonStatementsCompleter =
        new MCCommonStatementsSymTabCompletion();
    symTabCompleter.add4MCCommonStatements(commonStatementsCompleter);

    MCVarDeclarationStatementsSymTabCompletion mcVarDeclarationStatementsCompleter =
        new MCVarDeclarationStatementsSymTabCompletion();
    symTabCompleter.add4MCVarDeclarationStatements(mcVarDeclarationStatementsCompleter);

    ast.accept(symTabCompleter);
    assertNoFindings();
  }

  protected void runContextConditionChecks(
      ASTInterpreterInput ast
  ) {
    InterpreterTestLangCoCoChecker checker = new InterpreterTestLangCoCoChecker();
    checker.addCoCo(new DoWhileConditionHasBooleanType());
    checker.addCoCo(new ExpressionStatementIsValid());
    checker.addCoCo(new ForConditionHasBooleanType());
    checker.addCoCo(new ForEachIsValid());
    checker.addCoCo(new IfConditionHasBooleanType());
    checker.addCoCo(new SwitchStatementValid());
    //checker.addCoCo(new SynchronizedArgIsReftype());
    checker.addCoCo(new WhileConditionHasBooleanType());
    checker.addCoCo(new AssertIsValid());
    //checker.addCoCo(new CatchIsValid());
    //checker.addCoCo(new ThrowIsValid());
    //checker.addCoCo(new ResourceInTryStatementCloseable());
    checker.addCoCo(new ExpressionValid());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());
    //checker.addCoCo(new RangeHasLowerOrUpperBound());
    checker.addCoCo(new QualifiedTypeHasNoTypeParameters());
    //checker.addCoCo(new TypeParameterNoCyclicInheritance());
    //checker.addCoCo(new TypeParametersHaveUniqueNames());
    checker.checkAll(ast);
    assertNoFindings();
  }

  ASTInterpreterInput getPreparedAST(String modelStr) {
    ASTInterpreterInput ast = parse(modelStr);
    runSymTabGenitor(ast);
    runSymTabCompleter(ast);
    runContextConditionChecks(ast);
    return ast;
  }

  MICalculation getCalculation(
      ASTInterpreterInput ast
  ) {
    MICalculation calculation = interpreter.getCalculation(ast);
    assertNoFindings();
    assertNotNull(calculation);
    InterpreterDataForBasicSymbols iData = interpreter.getInterpreterData();
    assertEquals(0, iData.getFrameLayoutStack().size());
    assertTrue(!iData.isPresentCalculation());
    return calculation;
  }

  protected MCValue interpret(String modelStr) {
    ASTInterpreterInput ast = getPreparedAST(modelStr);
    // explicitly get the calculation to check if there are errors
    getCalculation(ast);
    MCValue value = interpreter.interpretNode(ast);
    assertNoFindings();
    assertNotNull(value);
    return value;
  }

  protected <T> T interpretAndCast(String modelStr) {
    MCValue value = interpret(modelStr);
    Object valueObj = value.asNativeObject();
    @SuppressWarnings("unchecked")
    T casted = (T) valueObj;
    return casted;
  }

}
