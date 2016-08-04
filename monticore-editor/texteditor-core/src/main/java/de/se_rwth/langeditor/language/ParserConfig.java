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
package de.se_rwth.langeditor.language;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

public final class ParserConfig<P extends Parser> {
  
  private final Function<CharStream, Lexer> lexerCreator;
  
  private final Function<TokenStream, P> parserCreator;
  
  private final Function<P, ParserRuleContext> startingRule;
  
  public ParserConfig(
      Function<CharStream, Lexer> lexerCreator,
      Function<TokenStream, P> parserCreator,
      Function<P, ParserRuleContext> startingRule) {
    this.lexerCreator = lexerCreator;
    this.parserCreator = parserCreator;
    this.startingRule = startingRule;
  }
  
  public Parser emptyParser() {
    return parserCreator.apply(null);
  }
  
  public ParserRuleContext parse(String document) {
    return startingRule.apply(setupParser(document));
  }
  
  public ParserRuleContext parse(String document, BiConsumer<Token, String> errorListener) {
    P parser = setupParser(document);
    parser.addErrorListener(new BaseErrorListener() {
      
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
          int line, int charPositionInLine, String msg, RecognitionException e) {
        errorListener.accept((Token) offendingSymbol, msg);
      }
    });
    return startingRule.apply(parser);
  }
  
  private P setupParser(String document) {
    ANTLRInputStream inputStream = new ANTLRInputStream(document);
    Lexer lexer = lexerCreator.apply(inputStream);
    TokenStream tokens = new CommonTokenStream(lexer);
    return parserCreator.apply(tokens);
  }
}
