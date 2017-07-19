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
package de.se_rwth.langeditor.modelstates;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import com.google.inject.Singleton;

import de.monticore.ast.ASTNode;
import de.monticore.utils.ASTNodes;

@Singleton
public class Nodes {
  
  private final Map<ASTNode, ParseTree> astToParseTree = new WeakHashMap<>();
  
  private final Set<Map<ASTNode, ASTNode>> astParentMaps = new HashSet<>();
  
  void addNodes(ParseTree parseTree) {
    getAstNode(parseTree)
        .ifPresent(astNode -> astParentMaps.add(ASTNodes.childToParentMap(astNode)));
    
    for (ParseTree descendant : Trees.descendants(parseTree)) {
      getAstNode(descendant).ifPresent(astNode -> astToParseTree.put(astNode, descendant));
    }
  }
  
  public Optional<ParseTree> getParseTree(ASTNode astNode) {
    return Optional.ofNullable(astToParseTree.get(astNode));
  }
  
  public Optional<ASTNode> getParent(ASTNode astNode) {
    return astParentMaps.stream()
        .map(astParentMap -> astParentMap.get(astNode))
        .filter(parent -> parent != null)
        .findFirst();
  }
  
  public static Optional<ASTNode> getAstNode(ParseTree parseTree) {
    try {
      Field retField = parseTree.getClass().getField("ret");
      Object ret = retField.get(parseTree);
      if (ret instanceof ASTNode) {
        return Optional.of((ASTNode) ret);
      }
      else {
        return Optional.empty();
      }
    }
    catch (NoSuchFieldException
           | SecurityException
           | IllegalArgumentException
           | IllegalAccessException e) {
      return Optional.empty();
    }
  }
}
