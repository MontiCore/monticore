/* (c) https://github.com/MontiCore/monticore */
package de.monticore.antlr4;

import de.monticore.ast.ASTNode;
import de.monticore.ast.ASTNodeBuilder;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * BuildVisitors are used in the second stage of the two-phase-parsers.
 * Phase one results in an ANTLR parse tree (without any AST-specific actions),
 * and phase is done by ANTLR {@link org.antlr.v4.runtime.tree.ParseTreeVisitor}-instances of this class building the
 * MontiCore-AST representation of the input.
 * <p>
 * This class mainly provides {@link SourcePosition} and {@link Comment} related functionality.
 * The concrete ParseTree-to-AST transformation is handled in the generated instances of this class.
 */
public abstract class MCBuildVisitor {

  protected String fileName;
  protected BufferedTokenStream bufferedTokenStream;

  /**
   * A flag for further debugging of comment-handling.
   * Not printed by default due to performance
   */
  public static boolean debug = false;

  public MCBuildVisitor(String fileName, BufferedTokenStream bufferedTokenStream) {
    this.fileName = fileName;
    this.bufferedTokenStream = bufferedTokenStream;
  }

  public String getFileName() {
    return this.fileName;
  }

  protected <T extends ASTNodeBuilder<?>> T setSourcePos(T builder, ParserRuleContext ctx) {
    builder.set_SourcePositionStart(computePosition(ctx.start));
    // ctx.stop may be null for e.g. NoWSLast2 = {noSpace()}? ;
    builder.set_SourcePositionEnd(computeEndPosition(ctx.stop == null ? ctx.start : ctx.stop));
    return builder;
  }


  public de.se_rwth.commons.SourcePosition computePosition(Token token) {
    if (token == null) {
      return null;
    }
    int line = token.getLine();
    int column = token.getCharPositionInLine();
    return new de.se_rwth.commons.SourcePosition(line, column, getFileName());
  }


  public SourcePosition computeEndPosition(@Nonnull Token token) {
    int line = token.getLine();
    int column = token.getCharPositionInLine();
    String text = token.getText();
    if (text == null) {
      throw new IllegalArgumentException("0xA0708 text was null!");
    } else if ("\n".equals(text)) {
      column += text.length();
    } else if (text.indexOf("\n") == -1) {
      column += text.length();
    } else {
      String[] splitted = text.split("\n", -1);
      line += splitted.length - 1;
      // +1: if there is 1 character on the last line, sourcepos must
      // be 2...
      column = splitted[splitted.length - 1].length() + 1;
    }
    return new de.se_rwth.commons.SourcePosition(line, column, getFileName());
  }

  protected int pevPreCommentTokenIndex = 0;

  // tokens
  protected int tokenIndex = -1;

  protected int commentChannel = Token.HIDDEN_CHANNEL;

  protected IntervalSet commentSet = new IntervalSet();

  /**
   * Collect fresh tokens on the {@link MCBuildVisitor#commentChannel} channel and store them as pre-comments
   * @param builder the builder to store the pre-comments on
   * @param ctx the context object to walk up to
   */
  protected void handlePreComments(ASTNodeBuilder<?> builder, ParserRuleContext ctx) {
    // Handle the pre-comments before the rule (such as JavaDoc comments on methods)
    int start = ctx.start.getTokenIndex();

    // We might have to skip over token definitions
    if (tokenIndex != -1)
      start = tokenIndex;

    // Expand left side to include hidden pres
    @Nullable
    List<Token> hiddenLeft = bufferedTokenStream.getHiddenTokensToLeft(start, this.commentChannel);
    if (hiddenLeft != null) {
      start = Math.min(start, hiddenLeft.stream().map(Token::getTokenIndex).min(Integer::compare).orElse(Integer.MAX_VALUE));
    }


    doComments(builder::add_PreComment, start, ctx.start.getTokenIndex());
    pevPreCommentTokenIndex = ctx.start.getTokenIndex();
  }

  /**
   * Collect all fresh tokens of the same line on the {@link MCBuildVisitor#commentChannel} channel and store them as
   * post-comments
   * @param builder the builder to store the post-comments on
   * @param ctx the context object in which line we check
   */
  protected void handleInnerComments(ASTNodeBuilder<?> builder, ParserRuleContext ctx) {
    // Productions with only predicates might not have a stop
    if (ctx.getStop() == null) return;
    // Handle all comments within the bounds of a rule
    int start = ctx.getStart().getTokenIndex();
    int stop = ctx.getStop().getTokenIndex();

    // Consume all hidden tokens/comments on the same line (but only if the next real token is not in this line)
    @Nullable List<Token> hiddenRight = bufferedTokenStream.getHiddenTokensToRight(stop, this.commentChannel);
    if (hiddenRight != null) {
      int rightStop = stop;
      // Find the rightmost hidden index on the same line
      for (Token hr : hiddenRight) {
        if (hr.getLine() != ctx.stop.getLine()) break;
        rightStop = Math.max(rightStop, hr.getTokenIndex());
      }
      if (rightStop != stop && bufferedTokenStream.size() >= rightStop) {
        // Check if the next real is not on the same line
        Token t = bufferedTokenStream.get(rightStop + 1);
        if (t.getLine() != ctx.stop.getLine()) {
          // Move the stop index to the index of the right-most hidden token in our stop token line
          stop = rightStop;
        }
      }
    }

    // we skip already found tokens
    doComments(builder::add_PostComment, start, stop);
  }

  /**
   * Finally, add all comment-tokens after the final AST-element as post-comments to the element.
   * @param node the final ASTNode
   * @param ctx the parse tree
   */
  public void addFinalComments(ASTNode node, ParserRuleContext ctx) {
    // Productions with only predicates might not have a stop
    if (ctx.getStop() == null) return;
    int stop = ctx.getStop().getTokenIndex();
    // Expand right side to include hidden pres
    @Nullable
    List<Token> hiddenRight = bufferedTokenStream.getHiddenTokensToRight(stop, this.commentChannel);
    if (hiddenRight != null) {
      stop = Math.max(stop, hiddenRight.stream().map(Token::getTokenIndex).min(Integer::compare).orElse(Integer.MAX_VALUE));
    }
    this.doComments(node::add_PostComment, ctx.start.getTokenIndex(), stop);
  }

  /**
   * Find all (not yet found) hidden/comment tokens
   * and calls the consumer for each
   *
   * @param addConsumer
   * @param start       {@link IntervalSet#complement(int, int)}
   * @param stop        {@link IntervalSet#complement(int, int)}
   */
  protected void doComments(Consumer<Comment> addConsumer, int start, int stop) {
    tokenIndex = stop;
    @Nullable
    IntervalSet actual = commentSet.complement(start, stop);

    // Add the interval to the set of checked token indizes
    commentSet.add(start, stop);

    if (debug)
      System.err.println("Everything from " + start + "(" + bufferedTokenStream.get(start).getText() + ") "
              + " to " + stop + "(" + bufferedTokenStream.get(stop).getText() + ")  "
              + " in " + Thread.currentThread().getStackTrace()[3].getMethodName() + " > " + Thread.currentThread().getStackTrace()[2].getMethodName());

    if (actual == null) return;

    // For every (not yet seen) index within the range
    for (int tokenIndex : actual.toList()) {
      // fetch the token
      Token t = bufferedTokenStream.get(tokenIndex);
      if (isComment(t)) {
        // And add the comment iff a comment token is used
        if (debug)
          System.err.println("found " + t.getText() + " at " + t.getTokenIndex());
        addConsumer.accept(createComment(t));
      }
    }
  }

  protected boolean isComment(Token token) {
    return token.getChannel() == this.commentChannel;
  }


  /**
   * Create a new MontiCore comment from an ANTLR token.
   * Sets the start and end position, as well as the text.
   *
   * @param token the token
   * @return the comment
   */
  protected Comment createComment(Token token) {
    de.monticore.ast.Comment _comment = new de.monticore.ast.Comment(token.getText());
    _comment.set_SourcePositionStart(computePosition(token));
    _comment.set_SourcePositionEnd(computeEndPosition(token));
    return _comment;
  }
}
