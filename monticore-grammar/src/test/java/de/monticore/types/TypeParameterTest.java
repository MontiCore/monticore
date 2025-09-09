/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.types.typeparameterstest.TypeParametersTestMill;
import de.monticore.types.typeparameterswithoutintersectiontypestest.TypeParametersWithoutIntersectionTypesTestMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeParameterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testTypeParameters() throws IOException {
    String model = "<T, U extends A.B>";
    TypeParametersTestMill.reset();
    TypeParametersTestMill.init();
    Optional<ASTTypeParameters> tpsOpt =
        TypeParametersTestMill.parser().parse_StringTypeParameters(model);
    assertTrue(tpsOpt.isPresent());
    ASTTypeParameters tps = tpsOpt.get();
    assertEquals(2, tps.sizeTypeParameters());
    ASTTypeParameter tp0 = tps.getTypeParameter(0);
    assertEquals("T", tp0.getName());
    assertTrue(tp0.isEmptyMCTypes());
    ASTTypeParameter tp1 = tps.getTypeParameter(1);
    assertEquals("U", tp1.getName());
    assertFalse(tp1.isEmptyMCTypes());
    assertEquals(1, tp1.sizeMCTypes());
    assertEquals("A.B", tp1.getMCType(0).printType());
    assertTrue(Log.getFindings().isEmpty());
  }

  // todo FDr write Trafo iff required -> discuss
  @Test
  @Disabled("needs trafo")
  public void testTypeParametersMultipleBounds() throws IOException {
    String model = "<T extends A & B>";
    TypeParametersTestMill.reset();
    TypeParametersTestMill.init();
    Optional<ASTTypeParameters> tpsOpt =
        TypeParametersTestMill.parser().parse_StringTypeParameters(model);
    assertTrue(tpsOpt.isPresent());
    ASTTypeParameters tps = tpsOpt.get();
    assertEquals(1, tps.sizeTypeParameters());
    ASTTypeParameter tp0 = tps.getTypeParameter(0);
    assertEquals("T", tp0.getName());
    assertEquals(2, tp0.sizeMCTypes());
    assertEquals("A", tp0.getMCType(0).printType());
    assertEquals("B", tp0.getMCType(1).printType());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTypeParametersMultipleBoundsWithoutIntersectionTypes() throws IOException {
    String model = "<T extends A & B>";
    TypeParametersWithoutIntersectionTypesTestMill.reset();
    TypeParametersWithoutIntersectionTypesTestMill.init();
    Optional<ASTTypeParameters> tpsOpt =
        TypeParametersWithoutIntersectionTypesTestMill.parser().parse_StringTypeParameters(model);
    assertTrue(tpsOpt.isPresent());
    ASTTypeParameters tps = tpsOpt.get();
    assertEquals(1, tps.sizeTypeParameters());
    ASTTypeParameter tp0 = tps.getTypeParameter(0);
    assertEquals("T", tp0.getName());
    assertEquals(2, tp0.sizeMCTypes());
    assertEquals("A", tp0.getMCType(0).printType());
    assertEquals("B", tp0.getMCType(1).printType());
    assertTrue(Log.getFindings().isEmpty());
  }

}
