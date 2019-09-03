/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.literals.mccommonliterals._ast.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfMCCommonLiteralsTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  
  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfLiterals derLit = new DeriveSymTypeOfMCCommonLiterals();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2b

  // Mill used ... alternative would be a Parser for Literals
  
  @Test
  public void deriveTFromLiteral1Int() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.natLiteralBuilder().setDigits("17").build();
    assertEquals("int", tc.typeOf(lit).print());
  }
  
  // TODO RE: Testen

}
