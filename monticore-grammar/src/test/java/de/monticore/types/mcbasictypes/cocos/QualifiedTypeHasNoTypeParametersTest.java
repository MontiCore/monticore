package de.monticore.types.mcbasictypes.cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._cocos.MCBasicTypesCoCoChecker;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mcbasictypeswithbasicsymbolstest.MCBasicTypesWithBasicSymbolsTestMill;
import de.monticore.types.mcbasictypeswithbasicsymbolstest._symboltable.IMCBasicTypesWithBasicSymbolsTestArtifactScope;
import de.monticore.types.mcbasictypeswithbasicsymbolstest.types3.MCBasicTypesWithBasicSymbolsTestTypeCheck3;
import de.monticore.types3.util.DefsTypesForTests;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QualifiedTypeHasNoTypeParametersTest {

  MCBasicTypesCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    // not using MCBasicTypesTest, as we additionally require BasicSymbols
    MCBasicTypesWithBasicSymbolsTestMill.reset();
    MCBasicTypesWithBasicSymbolsTestMill.init();
    MCBasicTypesWithBasicSymbolsTestTypeCheck3.init();
    DefsTypesForTests.setup();
    checker = new MCBasicTypesCoCoChecker();
    checker.setTraverser(MCBasicTypesTestMill.traverser());
    checker.addCoCo(new QualifiedTypeHasNoTypeParameters());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Person",
      "java.lang.String"
  })
  public void testValid(String model) throws IOException {
    ASTMCType mcType = parseAndCreateSymTab(model);
    checker.checkAll(mcType);
    assertTrue(Log.getFindings().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "List",
      "java.util.Map",
  })
  public void testInvalid(String model) throws IOException {
    ASTMCType mcType = parseAndCreateSymTab(model);
    checker.checkAll(mcType);
    assertFalse(Log.getFindings().isEmpty());
    assertEquals(
        "0xFD123",
        Log.getFindings().get(0).getMsg().substring(0, 7)
    );
  }

  protected ASTMCType parseAndCreateSymTab(String model)
      throws IOException {
    MCBasicTypesTestParser parser = MCBasicTypesTestMill.parser();
    Optional<ASTMCType> astOpt =
        parser.parse_String(model);
    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());
    assertTrue(Log.getFindings().isEmpty());
    IMCBasicTypesWithBasicSymbolsTestArtifactScope artifactScope =
        MCBasicTypesWithBasicSymbolsTestMill.scopesGenitorDelegator()
            .createFromAST(astOpt.get());
    artifactScope.setName("unreasonablyunimportant");
    assertTrue(Log.getFindings().isEmpty());
    return astOpt.get();
  }

}
