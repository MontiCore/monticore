/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.se_rwth.langeditor.util.antlr;

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
