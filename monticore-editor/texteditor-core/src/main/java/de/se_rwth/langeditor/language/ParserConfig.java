/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.language;

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
import org.antlr.v4.runtime.TokenStream;

import de.se_rwth.commons.SourcePosition;

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
  
  public ParserRuleContext parse(String document, BiConsumer<SourcePosition, String> errorListener) {
    P parser = setupParser(document);
    parser.addErrorListener(new BaseErrorListener() {
      
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
          int line, int charPositionInLine, String msg, RecognitionException e) {
        errorListener.accept(new SourcePosition(line,  charPositionInLine), msg);
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
