/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfLiteralsTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  // This is the core Visitor under Test (but rather empty)
  ITypesCalculator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2b
  
  @Test
  public void deriveTFromLiteral1(){
    ASTLiteral lit = MCCommonLiteralsMill.natLiteralBuilder().setDigits("17").build();
    assertEquals("int",tc.typeOf(lit).print());
  }
  
  
}
