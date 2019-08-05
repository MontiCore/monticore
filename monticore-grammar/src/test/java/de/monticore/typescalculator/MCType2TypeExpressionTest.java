package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListTypeBuilder;
import de.monticore.types.mccollectiontypes._ast.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class MCType2TypeExpressionTest {

  @Test
  public void test() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<Person>");

    if(type.isPresent()) {
      TypesCalculatorHelper.mcType2TypeExpression(type.get());
    }

  }

  @Test
  public void testPrimitiveBoolean() throws IOException {
    Optional<ASTMCPrimitiveType> type = new MCCollectionTypesTestParser().parse_StringMCPrimitiveType("boolean");

    assertTrue(type.isPresent());

    ASTMCPrimitiveType booleanType = type.get();

    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(booleanType);

    assertTrue(typeExpression instanceof TypeConstant);

    assertTrue("boolean".equals(typeExpression.getName()));

  }


}
