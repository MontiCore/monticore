/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public abstract class DeriveSymTypeAbstractTest {

    @BeforeClass
    public static void setup() {
        LogStub.init();         // replace log by a sideffect free variant
        Log.enableFailQuick(false);
    }

    @Before
    public void setupForEach() {
        LogStub.init();         // replace log by a sideffect free variant
    }

    // Setup the TypeCheck with according derive and synthesize
    protected abstract void setupTypeCheck();

    // Parse a String expression of the according language
    protected abstract Optional<ASTExpression> parseStringExpression(String expression) throws IOException;

    // Provide the Traverser for the used language
    protected abstract ExpressionsBasisTraverser getUsedLanguageTraverser();

    private TypeCalculator tc;

    protected final void setTypeCheck(TypeCalculator tc) {
        this.tc = tc;
    }

    protected TypeCalculator getTypeCalculator() {
      return this.tc;
    }

    protected ASTExpression parseExpression(String expression) throws IOException {
        Optional<ASTExpression> astExpression = parseStringExpression(expression);
        assertTrue(astExpression.isPresent());
        return astExpression.get();
    }

    private ExpressionsBasisTraverser flatExpressionScopeSetterTraverser;

    protected final void setFlatExpressionScopeSetter(ICombineExpressionsWithLiteralsScope enclosingScope) {
        flatExpressionScopeSetterTraverser = getUsedLanguageTraverser();
        addToTraverser(flatExpressionScopeSetterTraverser, enclosingScope);
    }

    protected final void setFlatExpressionScope(ASTExpression astex) {
      if (flatExpressionScopeSetterTraverser != null) {
        astex.accept(flatExpressionScopeSetterTraverser);
      }
    }

    protected final void check(String expression, String expectedType) throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        setFlatExpressionScope(astex);

        assertEquals(expectedType, tc.typeOf(astex).print());
    }

    protected final void checkError(String expression, String expectedError) throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        setFlatExpressionScope(astex);

        Log.getFindings().clear();
        try {
            tc.typeOf(astex);
        } catch (RuntimeException e) {
            assertEquals(expectedError, getFirstErrorCode());
            return;
        }
        fail();
    }

    protected final void checkErrors(String expression, List<String> expectedErrors) throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        setFlatExpressionScope(astex);

        Log.getFindings().clear();
        try {
            tc.typeOf(astex);
        } catch (RuntimeException e) {
            assertEquals(expectedErrors, getAllErrorCodes());
            return;
        }
        fail();
    }

    protected final void checkErrors(String expression, String... expectedErrors) throws IOException {
        checkErrors(expression, Arrays.asList(expectedErrors));
    }

    protected final void checkErrorsAndFailOnException(String expression, List<String> expectedErrors)
      throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        setFlatExpressionScope(astex);

        Log.getFindings().clear();
        try {
            TypeCheckResult result = tc.iDerive.deriveType(astex);

            if(expectedErrors.isEmpty()) {
                assertEquals("Found errors even though there should be none", 0, Log.getErrorCount());
                assertTrue("Missing type check result (in the form of a SymTypeExpression)", result.isPresentResult());
            } else {
                assertEquals(expectedErrors, getAllErrorCodes());
            }

        } catch (Exception e) {
            fail("An unexpected Exception was thrown during running the typecheck on " + expression + ":\n"
              + e.getClass().getName() + e.getMessage()
            );
        }
    }

    protected final void checkErrorsAndFailOnException(String expression, String... expectedErrors) throws IOException {
        checkErrorsAndFailOnException(expression, Arrays.asList(expectedErrors));
    }

    private String getFirstErrorCode() {
        if (Log.getFindings().size() > 0) {
            String firstFinding = Log.getFindings().get(0).getMsg();
            return firstFinding.split(" ")[0];
        }
        return "";
    }

    private List<String> getFirstErrorCodes(long n) {
        List<String> errorsInLog = Log.getFindings().stream()
          .filter(Finding::isError)
          .map(err -> err.getMsg().split(" ")[0])
          .limit(n)
          .collect(Collectors.toList());
        List<String> errorsToReturn;

        if(errorsInLog.size() < n) {
            errorsToReturn = errorsInLog;
            for(int i = 0; i < n - errorsInLog.size(); i++) {
                errorsToReturn.add("");
            }
        } else {
            errorsToReturn = errorsInLog.subList(0, (int) n);
        }
        return errorsToReturn;
    }

    private List<String> getAllErrorCodes() {
        return getFirstErrorCodes(Log.getErrorCount());
    }


    private void addToTraverser(ExpressionsBasisTraverser traverser, IExpressionsBasisScope enclosingScope) {
        FlatExpressionScopeSetter flatExpressionScopeSetter = new FlatExpressionScopeSetter(enclosingScope);
        traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
        if (traverser instanceof AssignmentExpressionsTraverser) {
            ((AssignmentExpressionsTraverser) traverser).add4AssignmentExpressions(flatExpressionScopeSetter);
        }
        if (traverser instanceof CommonExpressionsTraverser) {
            ((CommonExpressionsTraverser) traverser).add4CommonExpressions(flatExpressionScopeSetter);
        }
        if (traverser instanceof JavaClassExpressionsTraverser) {
            ((JavaClassExpressionsTraverser) traverser).add4JavaClassExpressions(flatExpressionScopeSetter);
        }
        if (traverser instanceof LambdaExpressionsTraverser) {
            ((LambdaExpressionsTraverser) traverser).add4LambdaExpressions(flatExpressionScopeSetter);
        }
        if (traverser instanceof BitExpressionsTraverser) {
            ((BitExpressionsTraverser) traverser).add4BitExpressions(flatExpressionScopeSetter);
        }
        if (traverser instanceof MCBasicTypesTraverser) {
            ((MCBasicTypesTraverser) traverser).add4MCBasicTypes(flatExpressionScopeSetter);
        }
        if(traverser instanceof MCCollectionTypesTraverser) {
            ((MCCollectionTypesTraverser) traverser).add4MCCollectionTypes(flatExpressionScopeSetter);
        }
        if(traverser instanceof MCSimpleGenericTypesTraverser) {
            ((MCSimpleGenericTypesTraverser) traverser).add4MCSimpleGenericTypes(flatExpressionScopeSetter);
        }
    }

}
