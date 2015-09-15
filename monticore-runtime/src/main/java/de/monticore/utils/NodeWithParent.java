/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
 * ******************************************************************************
 */

package de.monticore.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.monticore.ast.ASTNode;

import java.util.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

public class NodeWithParent<T extends ASTNode> extends ForwardingASTNode<T> implements
    Iterable<NodeWithParent<?>> {
  
  private final T astNode;
  
  private final NodeWithParent<? extends ASTNode> parent;
  
  private final Set<NodeWithParent<?>> children = new LinkedHashSet<>();
  
  public NodeWithParent(T astNode) {
    this.astNode = astNode;
    this.parent = null;
    for (ASTNode childNode : astNode.get_Children()) {
      this.children.add(new NodeWithParent<>(childNode, this));
    }
  }
  
  private NodeWithParent(T astNode, NodeWithParent<?> parent) {
    this.astNode = astNode;
    this.parent = parent;
    for (ASTNode childNode : astNode.get_Children()) {
      this.children.add(new NodeWithParent<>(childNode, this));
    }
  }
  
  @Override
  public T delegate() {
    return astNode;
  }
  
  @Override
  public Iterator<NodeWithParent<?>> iterator() {
    Iterator<NodeWithParent<?>> childIterator = Iterables.concat(children).iterator();
    return Iterators.concat(Iterators.singletonIterator(this), childIterator);
  }
  
  public Optional<NodeWithParent<?>> getParentNodeWithParent() {
    return Optional.ofNullable(parent);
  }
  
  public Set<NodeWithParent<?>> getChildNodesWithParent() {
    return children;
  }
  
  public List<NodeWithParent<?>> getAncestorNodesWithParent() {
    List<NodeWithParent<?>> ancestors = new ArrayList<>();
    
    for (NodeWithParent<?> ancestor = this; ancestor != null; ancestor = ancestor
        .getParentNodeWithParent().orElse(null)) {
      ancestors.add(ancestor);
    }
    
    return ancestors;
  }
  
  /**
   * This method is a more general and typed version of {@link #getParentNodeWithParent()}. The set
   * of examined ancestors includes this NodeWithParent.
   * 
   * @param nodeType the type of ancestor to be returned
   * @return the closest ancestor of the specified type. The Optional wrapper should serve as a
   * reminder that such an ancestor may not necessarily exist.
   */
  // This method requires raw types since the generic type of any NodeWithParent is erased at
  // runtime and the compiler can't confirm the correctness of the runtime check used in this
  // method.
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <C extends ASTNode> Optional<NodeWithParent<C>> getClosestAncestor(Class<C> nodeType) {
    Optional closestAncestor = Optional.empty();
    
    for (NodeWithParent<?> ancestor : getAncestorNodesWithParent()) {
      if (nodeType.isInstance(ancestor.delegate())) {
        closestAncestor = Optional.of(ancestor);
        break;
      }
    }
    
    return closestAncestor;
  }
  
  /**
   * Examines the entire subtree below and including this NodeWithParent.
   * 
   * @param nodeType the type of successor to be returned
   * @return the set of all successors of the specified type
   */
  // This method requires raw types since the generic type of any NodeWithParent is erased at
  // runtime and the compiler can't confirm the correctness of the runtime check used in this
  // method.
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <C extends ASTNode> Set<NodeWithParent<C>> getSuccessors(Class<C> nodeType) {
    
    Set filteredNodesWithParent = Sets.newLinkedHashSet(Iterables.filter(this,
        node -> nodeType.isInstance(node.astNode)));
    
    return filteredNodesWithParent;
  }

}
