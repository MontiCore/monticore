// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createInferenceVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeOfNull;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVoid;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types.check.SymTypeExpressionFactory.createWildcard;

/**
 * A generator to generate random SymTypeExpressions for testing purposes.
 * <p>
 * This class should be considered a draft/rough prototype.
 */
public class SymTypeExpressionGenerator {

  protected Random rand;

  public SymTypeExpressionGenerator() {
    // no fixed seed for monkey testing
    rand = new Random();
  }

  public SymTypeExpressionGenerator(long seed) {
    rand = new Random(seed);
  }

  protected enum SymTypeExpressionKind {
    PRIMITIVE,
    INFERENCE_VAR,
    ARRAY,
    NULL,
    FUNCTION,
    TUPLE,
    UNION,
    INTERSECTION,
    WILDCARD,

    // MUST be the last in the list
    // to efficiently remove it from the options
    OBSCURE;
  }

  protected static final List<SymTypeExpressionKind> ALL_KINDS =
      List.of(SymTypeExpressionKind.values());

  protected static final List<SymTypeExpressionKind> ALL_KINDS_NO_OBSCURE =
      ALL_KINDS.subList(0, ALL_KINDS.size() - 2);

  protected static final List<SymTypeExpressionKind> LEAF_KINDS =
      List.of(
          SymTypeExpressionKind.PRIMITIVE,
          SymTypeExpressionKind.INFERENCE_VAR,
          SymTypeExpressionKind.NULL,
          SymTypeExpressionKind.OBSCURE
      );

  protected static final List<SymTypeExpressionKind> LEAF_KINDS_NO_OBSCURE =
      LEAF_KINDS.subList(0, LEAF_KINDS.size() - 2);

  protected static final List<String> PRIMITIVES = List.of(
      BasicSymbolsMill.BOOLEAN,
      BasicSymbolsMill.BYTE,
      BasicSymbolsMill.SHORT,
      BasicSymbolsMill.CHAR,
      BasicSymbolsMill.INT,
      BasicSymbolsMill.LONG,
      BasicSymbolsMill.FLOAT,
      BasicSymbolsMill.DOUBLE
  );

  /**
   *
   * @param number       amount of SymTypeExpressions
   * @param maxDepth     inclusive
   * @param maxWidth     exclusive
   * @param allowObscure whether Obscure may be contained in the SymtypeExpressions
   * @return List of SymTypeExpressions with the given complexity
   */
  public List<SymTypeExpression> createSymTypeExpressions(int number, int maxDepth, int maxWidth, boolean allowObscure) {
    List<SymTypeExpression> symTypeExpressions = new ArrayList<>(number);
    for (int i = 0; i < number; i++) {
      symTypeExpressions.add(createSymTypeExpression(maxDepth, maxWidth, allowObscure));
    }
    return symTypeExpressions;
  }

  public SymTypeExpression createSymTypeExpression(int maxDepth, int maxWidth, boolean allowObscure) {
    SymTypeExpression res;
    List<SymTypeExpressionKind> possibleKindList;
    if (maxDepth <= 0) {
      possibleKindList = allowObscure ? LEAF_KINDS : LEAF_KINDS_NO_OBSCURE;
    }
    else {
      possibleKindList = allowObscure ? ALL_KINDS : ALL_KINDS_NO_OBSCURE;
    }
    int nextDepth = maxDepth - 1;
    SymTypeExpressionKind randKind =
        possibleKindList.get(rand.nextInt(possibleKindList.size()));
    switch (randKind) {
      case PRIMITIVE:
        String primStr = PRIMITIVES.get(rand.nextInt(PRIMITIVES.size()));
        res = createPrimitive(primStr);
        break;
      case INFERENCE_VAR:
        res = createInferenceVariable();
        break;
      case ARRAY:
        int randDim = rand.nextInt(4) + 1;
        res = createTypeArray(
            createSymTypeExpression(nextDepth, maxWidth, allowObscure),
            randDim
        );
        break;
      case NULL:
        res = createTypeOfNull();
        break;
      case FUNCTION:
        res = createFunction(
            rand.nextBoolean()
                ? createSymTypeExpression(nextDepth, maxWidth, allowObscure)
                : createTypeVoid(),
            createSymTypeExpressions(rand.nextInt(maxWidth), nextDepth, maxWidth, allowObscure),
            rand.nextBoolean()
        );
        break;
      case TUPLE:
        res = createTuple(
            createSymTypeExpressions(rand.nextInt(maxWidth), nextDepth, maxWidth, allowObscure)
        );
        break;
      case UNION:
        res = createUnion(
            createSymTypeExpressions(rand.nextInt(maxWidth), nextDepth, maxWidth, allowObscure)
        );
        break;
      case INTERSECTION:
        res = createIntersection(
            createSymTypeExpressions(rand.nextInt(maxWidth), nextDepth, maxWidth, allowObscure)
        );
        break;
      case WILDCARD:
        res = createWildcard(
            rand.nextBoolean(),
            createSymTypeExpression(nextDepth, maxWidth, allowObscure)
        );
        break;
      case OBSCURE:
        res = createObscureType();
        break;
      default:
        throw new IllegalStateException();
    }
    return res;
  }

}
