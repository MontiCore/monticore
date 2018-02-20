/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util.antlr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

public final class BruteforceParse {
  
  private BruteforceParse() {
    // noninstantiable
  }
  
  public static Optional<ParserRuleContext> tryAllParseMethods(Parser parser, TokenStream tokens) {
    Optional<ParserRuleContext> context = Optional.empty();
    for (Method parsingMethod : parsingMethods(parser.getClass())) {
      context = invoke(parsingMethod, parser, tokens);
      if (context.isPresent()) {
        break;
      }
    }
    return context;
  }
  
  private static Optional<ParserRuleContext> invoke(Method parsingMethod, Parser parser,
      TokenStream tokens) {
    parser.reset();
    parser.setInputStream(tokens);
    try {
      ParserRuleContext context = (ParserRuleContext) parsingMethod.invoke(parser);
      boolean parsedAll = tokens.LA(1) == IntStream.EOF;
      boolean noSyntaxErrors = parser.getNumberOfSyntaxErrors() == 0;
      return parsedAll && noSyntaxErrors ? Optional.of(context) : Optional.empty();
    }
    catch (IllegalAccessException | InvocationTargetException e) {
      return Optional.empty();
    }
  }
  
  private static Set<Method> parsingMethods(Class<? extends Parser> parserClass) {
    Method[] methods = parserClass.getDeclaredMethods();
    return Arrays.stream(methods)
        .filter(method -> ParserRuleContext.class.isAssignableFrom(method.getReturnType()))
        .collect(Collectors.toSet());
  }
  
}
