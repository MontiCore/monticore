/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryThisExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.expressions.javaclassexpressions._ast.ASTThisExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTTypePattern;
import de.monticore.expressions.javaclassexpressions._prettyprint.JavaClassExpressionsFullPrettyPrinter;
import de.monticore.expressions.testjavaclassexpressions.TestJavaClassExpressionsMill;
import de.monticore.expressions.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.expressions.uglyexpressions._prettyprint.UglyExpressionsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UglyExpressionsJavaPrinterTest {
  
  protected UglyExpressionsFullPrettyPrinter javaPrinter;

}
