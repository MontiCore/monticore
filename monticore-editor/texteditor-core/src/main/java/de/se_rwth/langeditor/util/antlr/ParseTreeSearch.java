/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
