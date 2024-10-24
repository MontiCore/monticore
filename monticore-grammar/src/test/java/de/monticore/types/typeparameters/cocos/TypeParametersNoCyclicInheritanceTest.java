package de.monticore.types.typeparameters.cocos;

import de.monticore.types.typeparameters.TypeParametersMill;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.types.typeparameters._symboltable.ITypeParametersArtifactScope;
import de.monticore.types.typeparameters._symboltable.TypeParametersSTCompleteTypes;
import de.monticore.types.typeparameterstest.TypeParametersTestMill;
import de.monticore.types.typeparameterstest._cocos.TypeParametersTestCoCoChecker;
import de.monticore.types.typeparameterstest._parser.TypeParametersTestParser;
import de.monticore.types.typeparameterstest._visitor.TypeParametersTestTraverser;
import de.monticore.types3.ITypeCalculator;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCalculator3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

public class TypeParametersNoCyclicInheritanceTest {

  TypeParametersTestCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TypeParametersTestMill.reset();
    TypeParametersTestMill.init();
    checker = new TypeParametersTestCoCoChecker();
    checker.setTraverser(TypeParametersTestMill.traverser());
    checker.addCoCo(new TypeParameterNoCyclicInheritance());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<T>",
      "<T extends U, U extends S, S>"
  })
  public void testValid(String model) throws IOException {
    ASTTypeParameters params = parseAndCreateSymTab(model);
    checker.checkAll(params);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<T extends T>",
      "<T extends U, U extends S, S extends T>",
  })
  public void testInvalid(String model) throws IOException {
    ASTTypeParameters params = parseAndCreateSymTab(model);
    checker.checkAll(params);
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertEquals(
        "0xFDC12",
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
    ITypeParametersArtifactScope artifactScope = TypeParametersMill
        .scopesGenitorDelegator().createFromAST(astOpt.get());
    artifactScope.setName("aName");
    TypeParametersTestTraverser stCompleter =
        TypeParametersTestMill.traverser();
    ITypeCalculator tc = getTypeCalculator();
    stCompleter.add4TypeParameters(new TypeParametersSTCompleteTypes(tc));
    astOpt.get().accept(stCompleter);
    return astOpt.get();
  }

  protected ITypeCalculator getTypeCalculator() {
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast infCtx4Ast = new InferenceContext4Ast();
    ITraverser typeTraverser =
        new CombineExpressionsWithLiteralsTypeTraverserFactory()
            .createTraverser(type4Ast, infCtx4Ast);
    return new TypeCalculator3(typeTraverser, type4Ast, infCtx4Ast);
  }

}
