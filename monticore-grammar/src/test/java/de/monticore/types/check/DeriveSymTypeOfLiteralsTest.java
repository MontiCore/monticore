/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

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
  ITypesCalculator derLit = new DeriveSymTypeOfLiteralsAndExpressions();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2b
  
  @Test
  public void deriveTFromLiteral1() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.natLiteralBuilder().setDigits("17").build();
    try {
      tc.typeOf(lit);
    } catch(Throwable x) {
      // alles gut, soll so sein, der getestete Visitor kennt keine Literals
      return;
    }
    assertTrue(false);  // not to be reached
  }
  
  
}
