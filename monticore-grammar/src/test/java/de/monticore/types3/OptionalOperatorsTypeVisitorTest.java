/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static de.monticore.types3.util.DefsVariablesForTests._intUnboxedOptionalVarSym;

public class OptionalOperatorsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void setup() {
    MCCollectionSymTypeRelations.init();
    DefsVariablesForTests.setup();
    addFurtherVariables();
  }

  protected void addFurtherVariables() {
    // add further types to test more complex expressions
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    inScope(gs, variable("varintOptionalOptional",
        MCCollectionSymTypeFactory
            .createOptional(_intUnboxedOptionalVarSym.getType())
    ));
  }

  @Test
  public void deriveFromOptionalExpressionPrefixTest() throws IOException {
    checkExpr("varintOptional ?: -1", "int");
    checkExpr("varintBoxedOptional ?: -1", "int");
    checkExpr("varintOptional ?: varString", "String | int");
    checkExpr("varintOptionalOptional ?: varintOptional", "Optional<int>");
    checkExpr("(varintOptionalOptional ?: varintOptional) ?: -1", "int");
  }

  @Test
  public void testInvalidOptionalExpressionPrefix() throws IOException {
    checkErrorExpr("1 ?: 1", "0xFDB74");
  }

  @Test
  public void deriveFromOptionalComparisonOperatorsTest() throws IOException {
    checkExpr("varintOptional ?<= 1", "boolean");
    checkExpr("varintBoxedOptional ?<= 1", "boolean");
    checkExpr("varintOptional ?>= 1", "boolean");
    checkExpr("varintBoxedOptional ?>= 1", "boolean");
    checkExpr("varintOptional ?< 1", "boolean");
    checkExpr("varintBoxedOptional ?< 1", "boolean");
    checkExpr("varintOptional ?> 1", "boolean");
    checkExpr("varintBoxedOptional ?> 1", "boolean");
    checkExpr("varintOptional ?== 1", "boolean");
    checkExpr("varintBoxedOptional ?== 1", "boolean");
    checkExpr("varintOptional ?!= 1", "boolean");
    checkExpr("varintBoxedOptional ?!= 1", "boolean");
  }

  @Test
  public void testInvalidOperatorComparisonOperatorPrefix() throws IOException {
    checkErrorExpr("1 ?<= 1", "0xFD209");
    checkErrorExpr("varintOptional ?<= varPerson", "0xFD280");
    checkErrorExpr("1 ?>= 1", "0xFD209");
    checkErrorExpr("varintOptional ?>= varPerson", "0xFD280");
    checkErrorExpr("1 ?< 1", "0xFD209");
    checkErrorExpr("varintOptional ?< varPerson", "0xFD280");
    checkErrorExpr("1 ?> 1", "0xFD209");
    checkErrorExpr("varintOptional ?> varPerson", "0xFD280");
    checkErrorExpr("1 ?== 1", "0xFD285");
    checkErrorExpr("varintOptional ?== varPerson", "0xFD285");
    checkErrorExpr("1 ?!= 1", "0xFD285");
    checkErrorExpr("varintOptional ?!= varPerson", "0xFD285");
  }

  @Test
  public void deriveFromOptionalSimilarExpressionTest() throws IOException {
    checkExpr("varintOptional ?~~ 1", "boolean");
    checkExpr("varintOptional ?~~ varPerson", "boolean");
  }

  @Test
  public void testInvalidOptionalSimilarExpression() throws IOException {
    checkErrorExpr("1 ?~~ 1", "0xFD203");
  }

  @Test
  public void deriveFromOptionalNotSimilarExpressionTest() throws IOException {
    checkExpr("varintOptional ?!~ 1", "boolean");
    checkExpr("varintOptional ?!~ varPerson", "boolean");
  }

  @Test
  public void testInvalidOptionalNotSimilarExpression() throws IOException {
    checkErrorExpr("1 ?!~ 1", "0xFD203");
  }

}
