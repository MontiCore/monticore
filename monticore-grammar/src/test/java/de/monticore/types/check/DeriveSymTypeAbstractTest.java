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
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Optional;

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

    private TypeCheck tc;

    protected final void setTypeCheck(TypeCheck tc) {
        this.tc = tc;
    }

    private ASTExpression parseExpression(String expression) throws IOException {
        Optional<ASTExpression> astExpression = parseStringExpression(expression);
        assertTrue(astExpression.isPresent());
        return astExpression.get();
    }

    private ExpressionsBasisTraverser flatExpressionScopeSetterTraverser;

    protected final void setFlatExpressionScopeSetter(ICombineExpressionsWithLiteralsScope enclosingScope) {
        flatExpressionScopeSetterTraverser = getUsedLanguageTraverser();
        addToTraverser(flatExpressionScopeSetterTraverser, enclosingScope);
    }

    protected final void check(String expression, String expectedType) throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        if (flatExpressionScopeSetterTraverser != null) {
            astex.accept(flatExpressionScopeSetterTraverser);
        }

        assertEquals(expectedType, tc.typeOf(astex).print());
    }

    protected final void checkError(String expression, String expectedError) throws IOException {
        setupTypeCheck();
        ASTExpression astex = parseExpression(expression);
        if (flatExpressionScopeSetterTraverser != null)
            astex.accept(flatExpressionScopeSetterTraverser);

        Log.getFindings().clear();
        try {
            tc.typeOf(astex);
        } catch (RuntimeException e) {
            assertEquals(expectedError, getFirstErrorCode());
            return;
        }
        fail();
    }

    private String getFirstErrorCode() {
        if (Log.getFindings().size() > 0) {
            String firstFinding = Log.getFindings().get(0).getMsg();
            return firstFinding.split(" ")[0];
        }
        return "";
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
