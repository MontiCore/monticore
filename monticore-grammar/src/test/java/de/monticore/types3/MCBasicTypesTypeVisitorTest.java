/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
      typeOpt.get().accept(typeMapTraverser);
      Assertions.assertTrue(!Log.getFindings().isEmpty());
      Assertions.assertNotEquals("void", getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName());
    }
  }

  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("void");
    Assertions.assertTrue(typeOpt.isPresent());
    typeOpt.get().accept(typeMapTraverser);
    Assertions.assertEquals("void", getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName());
    assertNoFindings();
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("Person");
    Assertions.assertTrue(typeOpt.isPresent());
    Assertions.assertTrue(typeOpt.get().isPresentMCType());
    generateScopes(typeOpt.get().getMCType());
    typeOpt.get().accept(typeMapTraverser);
    Assertions.assertEquals("Person", getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName());
    assertNoFindings();
  }

}
