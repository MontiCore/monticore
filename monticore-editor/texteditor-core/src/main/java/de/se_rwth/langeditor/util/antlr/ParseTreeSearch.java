/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util.antlr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.se_rwth.langeditor.util.antlr.ParseTrees.tokenInterval;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public final class ParseTreeSearch {
  
  private ParseTreeSearch() {
    // noninstantiable
  }
  
  public static ImmutableList<ParseTree> getContainingParseTrees(ParseTree tree, Interval interval) {
    checkArgument(tokenInterval(tree).properlyContains(interval),
        "The interval " + interval + " can't fit into the interval " + tokenInterval(tree));
    
    Builder<ParseTree> builder = ImmutableList.builder();
    builder.add(tree);
    Optional<ParseTree> subTree = getSubTreeThatContainsInterval(tree, interval);
    
    while (subTree.isPresent()) {
      builder.add(subTree.get());
      subTree = getSubTreeThatContainsInterval(subTree.get(), interval);
    }
    
    return builder.build();
  }
  
  private static Optional<ParseTree> getSubTreeThatContainsInterval(ParseTree tree,
      Interval interval) {
    List<ParseTree> childrenThatContainInterval = Trees.getChildren(tree).stream()
        .filter(ParseTree.class::isInstance)
        .map(ParseTree.class::cast)
        .filter(childTree -> tokenInterval(childTree).properlyContains(interval))
        .collect(Collectors.toList());
    
    checkState(childrenThatContainInterval.size() < 2,
        "Multiple sibling trees properly contain the interval. This is probably a bug.");
    
    return childrenThatContainInterval.stream().findFirst();
  }
}
