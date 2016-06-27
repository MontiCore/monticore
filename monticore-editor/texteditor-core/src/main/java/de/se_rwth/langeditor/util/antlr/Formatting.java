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
