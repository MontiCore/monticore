/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

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
  public void symTypeFromAST_Test5() throws IOException {
    // tests with resolving in a sub scope
    checkType("java.util.Map",
        "java.util.Map<java.util.Map.KeyT,java.util.Map.ValueT>");
  }

  @Test
  public void symTypeFromAST_VoidTest() throws IOException {
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType("void");
    if (parser.hasErrors()) {
      // OK
    }
    else {
      // if it can be parsed, we expect an error
      assertTrue(typeOpt.isPresent());
      generateScopes(typeOpt.get());
      typeOpt.get().accept(typeMapTraverser);
      assertTrue(!Log.getFindings().isEmpty());
      assertNotEquals(
          "void",
          getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName()
      );
    }
  }

  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("void");
    assertTrue(typeOpt.isPresent());
    typeOpt.get().accept(typeMapTraverser);
    assertEquals(
        "void",
        getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName()
    );
    assertNoFindings();
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    Optional<ASTMCReturnType> typeOpt =
        parser.parse_StringMCReturnType("Person");
    assertTrue(typeOpt.isPresent());
    assertTrue(typeOpt.get().isPresentMCType());
    generateScopes(typeOpt.get().getMCType());
    typeOpt.get().accept(typeMapTraverser);
    assertEquals(
        "Person",
        getType4Ast().getPartialTypeOfTypeId(typeOpt.get()).printFullName()
    );
    assertNoFindings();
  }

}
