/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcstructuraltypes._cocos.MCStructuralTypesASTMCUnionTypeCoCo;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTestTypeCheck3;
import de.monticore.types.mcstructuraltypestest.MCStructuralTypesTestMill;
import de.monticore.types.mcstructuraltypestest._cocos.MCStructuralTypesTestCoCoChecker;
import de.monticore.types.mcstructuraltypestest._parser.MCStructuralTypesTestParser;
import de.monticore.types.mcstructuraltypestest._symboltable.IMCStructuralTypesTestArtifactScope;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(MCStructuralTypesTestMill.class)
public class UnionOrIntersectionTypeIsRedundantTest {
  MCStructuralTypesTestCoCoChecker checker;

  @BeforeEach
  public void init() {
    MCStructuralTypesTestTypeCheck3.init();
    DefsTypesForTests.setup();
    checker = new MCStructuralTypesTestCoCoChecker();
    checker.setTraverser(MCStructuralTypesTestMill.traverser());
    checker.addCoCo((MCStructuralTypesASTMCUnionTypeCoCo) new UnionOrIntersectionTypeIsRedundant());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Person | Car",
      "Person | Car | School",
      "Person & Teachable",
      "Person & Car & School"
  })
  public void testValid(String model) throws IOException {
    ASTMCType mcType = parseAndCreateSymTab(model);
    checker.checkAll(mcType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Person | Student",
      "Person | Car | Student",
      "Person & Student",
      "Person & School & Student"
  })
  public void testInvalid(String model) throws IOException {
    ASTMCType mcType = parseAndCreateSymTab(model);
    checker.checkAll(mcType);
    MCAssertions.assertHasFindingStartingWith("0xE0000");
  }

  protected ASTMCType parseAndCreateSymTab(String model) throws IOException {
    MCStructuralTypesTestParser parser = MCStructuralTypesTestMill.parser();
    Optional<ASTMCType> astOpt = parser.parse_String(model);
    MCAssertions.assertNoFindings();
    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    IMCStructuralTypesTestArtifactScope artifactScope =
        MCStructuralTypesTestMill.scopesGenitorDelegator().createFromAST(astOpt.get());
    artifactScope.setName("unimportant");
    MCAssertions.assertNoFindings();
    return astOpt.get();
  }

}
