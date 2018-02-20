/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util.antlr;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.tree.ParseTree;

import com.google.common.collect.ImmutableSet;

public final class Formatting {
  
  private Formatting() {
    // noninstantiable
  }
  
  public static void contractWhiteSpaces(TokenStreamRewriter rewriter, int whiteSpaceTokenType) {
    TokenStream tokens = rewriter.getTokenStream();
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      if (token.getType() == whiteSpaceTokenType) {
        rewriter.replace(i, " ");
      }
    }
  }
  
  public static void insertLineBreaks(TokenStreamRewriter rewriter,
      ImmutableSet<String> linebreakerLiterals,
      int whiteSpaceTokenType) {
    TokenStream tokens = rewriter.getTokenStream();
    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      if (linebreakerLiterals.contains(token.getText())) {
        breakLine(token, rewriter, whiteSpaceTokenType);
      }
    }
  }
  
  private static void breakLine(Token precedingToken, TokenStreamRewriter rewriter,
      int whiteSpaceTokenType) {
    Token followingToken = rewriter.getTokenStream().get(precedingToken.getTokenIndex() + 1);
    if (followingToken.getType() == whiteSpaceTokenType) {
      rewriter.replace(followingToken, "\n");
    }
    else {
      rewriter.insertAfter(precedingToken, "\n");
    }
  }
  
  public static void indent(TokenStreamRewriter rewriter,
      ImmutableSet<? extends ParseTree> succeedingSections, String indentation) {
    succeedingSections.stream()
        .map(ParseTrees::getFirstToken)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(token -> rewriter.insertBefore(token, indentation));
  }
  
  public static void insertLineBreaks(TokenStreamRewriter rewriter,
      ImmutableSet<? extends ParseTree> precedingSections) {
    precedingSections.stream()
        .map(ParseTrees::getLastToken)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(token -> rewriter.insertAfter(token, "\n"));
  }
}
