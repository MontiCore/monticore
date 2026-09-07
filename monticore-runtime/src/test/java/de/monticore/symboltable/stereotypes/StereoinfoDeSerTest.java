/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StereoinfoDeSerTest {

  @BeforeEach
  void setup() {
    LogStub.init();
  }

  @Test
  void shouldSerializeStereoInfoWithoutValue() {
    // Given
    IStereotypeSymbol stereotype = new MockSymbolicStereotype("a.b.c.StereoType");
    IStereotypeReference stereotypeRef = new SymbolBackedStereotypeReference(stereotype);

    // When
    String json = StereoinfoDeSer.printAsJson(stereotypeRef, Optional.empty());

    // Then
    assertEquals("{\"stereotype\":\"a.b.c.StereoType\"}", json);
  }

  /** As we serialize a reference to a symbolic stereotype, we must be able to construct such a
   * stereotype. The only relevant field that we need, which is mocked with useful information, is
   * {@link MockSymbolicStereotype#getFullName()}
   */
  protected static final class MockSymbolicStereotype implements IStereotypeSymbol {

    private final String fullName;

    public MockSymbolicStereotype(String fullName) {
      this.fullName = fullName;
    }

    /** Only  interesting method */
    @Override
    public String getFullName() {
      return fullName;
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public String getPackageName() {
      return "";
    }

    @Override
    public IScope getEnclosingScope() {
      return null;
    }

    @Override
    public void setAccessModifier(AccessModifier accessModifier) { }

    @Override
    public Map<IStereotypeReference, Optional<MCValue>> getStereoinfo() {
      return Map.of();
    }

    @Override
    public boolean isPresentAstNode() {
      return false;
    }

    @Override
    public ASTNode getAstNode() {
      return null;
    }
  }
}
