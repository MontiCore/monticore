package de.monticore.types.typeparameters.cocos;

import de.monticore.types.typeparameters.TypeParametersMill;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.types.typeparameterstest.TypeParametersTestMill;
import de.monticore.types.typeparameterstest._cocos.TypeParametersTestCoCoChecker;
import de.monticore.types.typeparameterstest._parser.TypeParametersTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

public class TypeParametersHaveUniqueNamesTest {

  TypeParametersTestCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TypeParametersTestMill.reset();
    TypeParametersTestMill.init();
    checker = new TypeParametersTestCoCoChecker();
    checker.setTraverser(TypeParametersTestMill.traverser());
    checker.addCoCo(new TypeParametersHaveUniqueNames());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<T>",
      "<A, B, M, U, G>",
      "<T extends Comparable<T>, U extends T>"
  })
  public void testValid(String model) throws IOException {
    ASTTypeParameters params = parseAndCreateSymTab(model);
    checker.checkAll(params);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<T,T>",
      "<T,T,T,T>",
      "<T extends Comparable<T>, U extends T, U extends NotT>"
  })
  public void testInvalid(String model) throws IOException {
    ASTTypeParameters params = parseAndCreateSymTab(model);
    checker.checkAll(params);
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(
        "0xFDC14",
        Log.getFindings().get(0).getMsg().substring(0, 7)
    );
  }

  protected ASTTypeParameters parseAndCreateSymTab(String model)
      throws IOException {
    TypeParametersTestParser parser = TypeParametersTestMill.parser();
    Optional<ASTTypeParameters> astOpt =
        parser.parse_StringTypeParameters(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astOpt.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
    TypeParametersMill.scopesGenitorDelegator().createFromAST(astOpt.get());
    return astOpt.get();
  }

}
