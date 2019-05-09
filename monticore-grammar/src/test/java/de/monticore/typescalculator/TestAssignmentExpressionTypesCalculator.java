package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testassignmentexpressions._ast.*;
import de.monticore.typescalculator.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;

import java.util.Map;
import java.util.Optional;

public class TestAssignmentExpressionTypesCalculator extends AssignmentExpressionsWithLiteralsTypesCalculator implements TestAssignmentExpressionsVisitor {


}
