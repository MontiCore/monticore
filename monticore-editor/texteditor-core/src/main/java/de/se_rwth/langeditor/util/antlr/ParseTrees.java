/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util.antlr;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getLast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.se_rwth.langeditor.util.Misc;

public final class ParseTrees {
  
  private ParseTrees() {
    // noninstantiable
  }
  
  public static Interval tokenInterval(ParseTree parseTree) {
    Optional<TerminalNode> firstTerminal = getFirstTerminal(parseTree);
    Optional<TerminalNode> lastTerminal = getLastTerminal(parseTree);
    if (firstTerminal.isPresent() && lastTerminal.isPresent()) {
      int startIndex = firstTerminal.get().getSymbol().getStartIndex();
      int stopIndex = lastTerminal.get().getSymbol().getStopIndex() + 1;
      return new Interval(startIndex, stopIndex);
    }
    else {
      return Interval.INVALID;
    }
  }
  
  public static Stream<ParseTree> successors(ParseTree parseTree) {
    return Misc.preorder(parseTree, ParseTrees::getChildren);
  }
  
  public static List<ParseTree> getChildren(ParseTree parseTree) {
    List<ParseTree> children = new ArrayList<ParseTree>();
    for (int i = 0; i < parseTree.getChildCount(); i++) {
      children.add(parseTree.getChild(i));
    }
    return children;
  }
  
  public static ParseTree root(ParseTree parseTree) {
    ParseTree root = parseTree;
    while (root.getParent() != null) {
      root = root.getParent();
    }
    return root;
  }
  
  public static Optional<TerminalNode> getTerminalBySourceCharIndex(ParseTree parseTree,
      int documentOffset) {
    List<TerminalNode> terminals = Trees.getDescendants(parseTree).stream()
        .filter(TerminalNode.class::isInstance)
        .map(TerminalNode.class::cast)
        .collect(Collectors.toList());
    for (TerminalNode terminal : terminals) {
      int startIndex = terminal.getSymbol().getStartIndex();
      int stopIndex = terminal.getSymbol().getStopIndex() + 1;
      if (startIndex <= documentOffset && documentOffset < stopIndex) {
        return Optional.of(terminal);
      }
    }
    return Optional.empty();
  }
  
  /** Find smallest subtree of t enclosing range start..stop
   *  inclusively using postorder traversal.  Recursive depth-first-search.
   *
   *  @since 4.5.1
   */
  public static ParserRuleContext getRootOfSubtreeEnclosingRegion(ParseTree t,
                                  int start, // inclusive
                                  int stop)  // inclusive
  {
    int n = t.getChildCount();
    for (int i = 0; i<n; i++) {
      ParseTree child = t.getChild(i);
      ParserRuleContext r = getRootOfSubtreeEnclosingRegion(child, start, stop);
      if ( r!=null ) return r;
    }
    if ( t instanceof ParserRuleContext ) {
      ParserRuleContext r = (ParserRuleContext) t;
      if ( start>=r.getStart().getStartIndex() && // is range fully contained in t?
         (r.getStop()==null || stop<=r.getStop().getStopIndex()) )
      {
        // note: r.getStop()==null likely implies that we bailed out of parser and there's nothing to the right
        return r;
      }
    }
    return null;
  }
  
  public static Optional<TerminalNode> getTerminalByLineAndColumn(ParseTree parseTree,
      int line, int column) {
    List<TerminalNode> terminals = Trees.getDescendants(parseTree).stream()
        .filter(TerminalNode.class::isInstance)
        .map(TerminalNode.class::cast)
        .collect(Collectors.toList());
    for (TerminalNode terminal : terminals) {
      boolean sameLine = terminal.getSymbol().getLine() == line;
      boolean sameColumn = terminal.getSymbol().getCharPositionInLine() == column;
      if (sameLine && sameColumn) {
        return Optional.of(terminal);
      }
    }
    return Optional.empty();
  }
  
  public static Optional<TerminalNode> getFirstTerminal(ParseTree parseTree) {
    return Trees.getDescendants(parseTree).stream()
        .filter(TerminalNode.class::isInstance)
        .map(TerminalNode.class::cast)
        .findFirst();
  }
  
  public static Optional<TerminalNode> getLastTerminal(ParseTree parseTree) {
    TerminalNode lastTerminal = getLast(filter(Trees.getDescendants(parseTree), TerminalNode.class), null);
    return Optional.ofNullable(lastTerminal);
  }
  
  public static Optional<Token> getFirstToken(ParseTree parseTree) {
    return getFirstTerminal(parseTree).map(TerminalNode::getSymbol);
  }
  
  public static Optional<Token> getLastToken(ParseTree parseTree) {
    return getLastTerminal(parseTree).map(TerminalNode::getSymbol);
  }
  
  public static int getTokenLength(Token token) {
    return (token.getStopIndex() + 1) - token.getStartIndex();
  }
  
  public static ImmutableSet<ParseTree> filterContexts(ParseTree parseTree,
      ImmutableSet<Class<? extends ParseTree>> types) {
    Set<ParserRuleContext> matchingRules = Trees.getDescendants(parseTree).stream()
        .filter(descendant -> types.contains(descendant.getClass()))
        .map(ParserRuleContext.class::cast)
        .collect(Collectors.toSet());
    return ImmutableSet.copyOf(matchingRules);
  }
  
  public static ImmutableList<ParseTree> bottomUpAncestors(ParseTree parseTree) {
    return ImmutableList.copyOf(
        Iterables.filter(Lists.reverse(Trees.getAncestors(parseTree)), ParseTree.class));
  }
}
