/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.jupyter.AbstractMCTest;
import de.monticore.types.typeparameterstest.TypeParametersTestMill;
import de.monticore.types.typeparameterstest._parser.TypeParametersTestParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.runtime.junit.jupyter.PrettyPrinterTester.testPrettyPrinter;

public class TypeParametersPrettyPrinterTest extends AbstractMCTest {

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

  @FunctionalInterface
  protected interface ParseFunction<N extends ASTNode> {
    Optional<N> apply(String t) throws IOException;
  }

}
