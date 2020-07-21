/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCFullGenericTypesTest {

  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCMultipleGenericType> multipleGenericType = parser.parse_StringMCMultipleGenericType("a.B<C>.D.E<F>.G");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("a.B<C>.D.E<F>.G");
    assertTrue(genericType.isPresent());
    assertTrue(multipleGenericType.isPresent());
    assertEquals("a.B.D.E.G", multipleGenericType.get().printWithoutTypeArguments());
    assertEquals("a.B.D.E.G", genericType.get().printWithoutTypeArguments());
    assertFalse(parser.hasErrors());
  }

}
