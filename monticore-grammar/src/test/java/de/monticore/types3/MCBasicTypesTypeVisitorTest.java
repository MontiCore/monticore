/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCBasicTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_Test() throws IOException {
    checkTypeRoundTrip("double");
  }

  @Test
  public void symTypeFromAST_Test2() throws IOException {
    checkTypeRoundTrip("int");
  }

  @Test
  public void symTypeFromAST_Test3() throws IOException {
    checkErrorMCType("notAType", "0xA0324");
  }

  @Test
  public void symTypeFromAST_Test4() throws IOException {
    checkTypeRoundTrip("Person");
  }

  @Test
  public void symTypeFromAST_VoidTest() throws IOException {
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType("void");
    if (parser.hasErrors()) {
      // OK
    }
    else {
      // if it can be parsed, we expect an error
      Assertions.assertTrue(typeOpt.isPresent());
      generateScopes(typeOpt.get());
      checkType(typeOpt.get(), "void");
    }
  }

  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("void");
    Assertions.assertTrue(typeOpt.isPresent());
    SymTypeExpression type = TypeCheck3.symTypeFromAST(typeOpt.get());
    Assertions.assertEquals("void", type.printFullName());
    assertNoFindings();
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("Person");
    Assertions.assertTrue(typeOpt.isPresent());
    Assertions.assertTrue(typeOpt.get().isPresentMCType());
    generateScopes(typeOpt.get().getMCType());
    SymTypeExpression type = TypeCheck3.symTypeFromAST(typeOpt.get());
    Assertions.assertEquals("Person", type.printFullName());
    assertNoFindings();
  }

}
