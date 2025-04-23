/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.Value;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    ISymbolicStereotype stereotype = new MockSymbolicStereotype("a.b.c.StereoType");

    // When
    String json = StereoinfoDeSer.printAsJson(stereotype, Optional.empty());

    // Then
    assertEquals("{\"stereotype\":\"a.b.c.StereoType\"}", json);
  }

  /** As we serialize a reference to a symbolic stereotype, we must be able to construct such a
   * stereotype. The only relevant field that we need, which is mocked with useful information, is
   * {@link MockSymbolicStereotype#getFullName()}
   */
  protected static final class MockSymbolicStereotype implements ISymbolicStereotype {

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
    public Class<? extends ISymbol> getAnnotatedElement() {
      return null;
    }

    @Override
    public List<StereoValueType> getAllowedValueTypesList() {
      return List.of();
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
    public Map<ISymbolicStereotype, Optional<Value>> getStereoinfo() {
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
