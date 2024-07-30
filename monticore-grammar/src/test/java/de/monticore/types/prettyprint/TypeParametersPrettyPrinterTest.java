/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.types.typeparameterstest.TypeParametersTestMill;
import de.monticore.types.typeparameterstest._parser.TypeParametersTestParser;
import de.monticore.types3.AbstractTypeTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public class TypeParametersPrettyPrinterTest extends AbstractTypeTest {

  @BeforeEach
  public void init() {
    TypeParametersTestMill.reset();
    TypeParametersTestMill.init();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<T>",
      " < T > ",
      "<I, H, S, O, Y>",
      "<T, LT extends List<T>>",
      "<E extends Edge<E, N>, N extends Node<E, N>>",
      "<T, U extends A<T>&B<T>, V extends C&D>",
  })
  public void testTypeParameters(String model) throws IOException {
    TypeParametersTestParser parser = TypeParametersTestMill.parser();
    testPrettyPrinter(
        model, parser, parser::parse_StringTypeParameters,
        ast -> TypeParametersTestMill.prettyPrint(ast, true)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "T",
      "LongTypeParameterName",
      "T extends String",
      "T extends Map<T,List<T>>",
      "T extends A & B",
      "T extends A & B & C & D"
  })
  public void testTypeParameter(String model) throws IOException {
    TypeParametersTestParser parser = TypeParametersTestMill.parser();
    testPrettyPrinter(
        model, parser, parser::parse_StringTypeParameter,
        ast -> TypeParametersTestMill.prettyPrint(ast, true)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "String",
      "Map<T,List<T>>",
      "A & B",
      "A & B & C & D"
  })
  public void testTypeBound(String model) throws IOException {
    TypeParametersTestParser parser = TypeParametersTestMill.parser();
    testPrettyPrinter(
        model, parser, parser::parse_StringTypeBounds,
        ast -> TypeParametersTestMill.prettyPrint(ast, true)
    );
  }

  // this function could be used for all pretty printer tests if required,
  // however, the parameters are not great.
  protected <N extends ASTNode> void testPrettyPrinter(
      String model,
      MCConcreteParser parser,
      ParseFunction<N> parseFunc,
      Function<N, String> prettyPrintFunc
  ) throws IOException {
    Optional<N> astOpt = parseFunc.apply(model);
    assertNoFindings();
    Assertions.assertTrue(astOpt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    N ast = astOpt.get();
    String output = prettyPrintFunc.apply(ast);
    assertNoFindings();
    astOpt = parseFunc.apply(output);
    assertNoFindings();
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astOpt.isPresent());
    Assertions.assertTrue(ast.deepEquals(astOpt.get()));
  }

  @FunctionalInterface
  protected interface ParseFunction<N extends ASTNode> {
    Optional<N> apply(String t) throws IOException;
  }

}
