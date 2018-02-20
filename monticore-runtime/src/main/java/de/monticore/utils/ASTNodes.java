/* (c) https://github.com/MontiCore/monticore */

package de.monticore.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.Util;

import java.util.Collection;
import java.util.List;

/**
 * Helper class containing common operations concerning ASTNodes
 *
 * @author Sebastian Oberhoff
 */
public final class ASTNodes {

  private ASTNodes() {
    // noninstantiable
  }

  /**
   * Checks whether two ASTNodes are in a vertical relationship with each other with any number of
   * other nodes in between.
   * <p>
   * This operation is O(n), where n is the number of nodes contained in the subtree spanned by the
   * ancestor.
   *
   * @param ancestor  the node further up in the AST
   * @param successor the node further down in the AST
   * @return true if there exists a descending path from the ancestor to the successor
   */
  public static boolean areAncestorAndSuccessor(ASTNode ancestor, ASTNode successor) {
    Iterable<ASTNode> successors = Util.preOrder(ancestor, ASTNode::get_Children);
    return Iterables.any(successors, otherSuccessor -> otherSuccessor == successor);
  }

  /**
   * Calculates the list of nodes spanning a vertical path from ancestor node to successor.
   * <p>
   * The returned list will be empty if no path exists between ancestor and successor.
   * <p>
   * The returned list also contains both ancestor and successor if a path is indeed present.
   * <p>
   * This operation is O(n), where n is the number of nodes contained in the subtree spanned by
   * the ancestor.
   *
   * @param ancestor  the node at the top of the path
   * @param successor the node at the bottom of the path
   * @return the list of nodes from the ancestor to the successor, sorted from ancestor (first
   * element) to successor (last element)
   */
  public static List<ASTNode> getIntermediates(ASTNode ancestor, ASTNode successor) {
    ImmutableMap<ASTNode, ASTNode> childToParentMap = childToParentMap(ancestor);
    List<ASTNode> allAncestors =
        Util.listTillPredicate(successor, childToParentMap::get, node -> node != null);
    return Lists.reverse(allAncestors);
  }

  /**
   * Gathers a list of all successor nodes of an ASTNode with a specified type.
   *
   * @param ancestor the ancestor node of which successors with the specified type are to be
   *                 gathered
   * @param nodeType the type of successors to be gathered
   * @return the list of successors of the ancestor node with the specified type, ordered according
   * to a pre-order traversal
   */
  public static <T extends ASTNode> List<T> getSuccessors(ASTNode ancestor, Class<T> nodeType) {
    Iterable<ASTNode> successors = Util.preOrder(ancestor, ASTNode::get_Children);
    Iterable<T> successorsWithMatchingType = Iterables.filter(successors, nodeType);
    return Lists.newArrayList(successorsWithMatchingType);
  }

  /**
   * Gathers a list of all successor nodes of an ASTNode with a specified type.
   *
   * @param ancestor the ancestor node of which successors with the specified type are to be
   *                 gathered
   * @param nodeType the type of successors to be gathered
   * @return the list of successors of the ancestor node with the specified type, ordered according
   * to a pre-order traversal
   */
  public static List<ASTNode> getSuccessors(ASTNode ancestor, Collection<Class<? extends ASTNode>> types) {
    Iterable<ASTNode> successors = Util.preOrder(ancestor, ASTNode::get_Children);
    Iterable<ASTNode> successorsWithMatchingType = Iterables.filter(successors, successor -> types.contains(successor.getClass()));
    return Lists.newArrayList(successorsWithMatchingType);
  }

  /**
   * @param root the root of the subtree for which the map is to be created
   * @return an {@link ImmutableMap} from child-ASTNode to parent-ASTNode for all ASTNodes below the given root
   */
  public static ImmutableMap<ASTNode, ASTNode> childToParentMap(ASTNode root) {
    ImmutableMap.Builder<ASTNode, ASTNode> builder = ImmutableMap.builder();
    Util.preOrder(root, ASTNode::get_Children).stream().forEach(node -> {
      for (ASTNode child : node.get_Children()) {
        builder.put(child, node);
      }
    });
    return builder.build();
  }
}
