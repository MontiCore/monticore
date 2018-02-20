/* (c) https://github.com/MontiCore/monticore */

package de.monticore.utils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.se_rwth.commons.TreeUtil;
import de.se_rwth.commons.Util;

/**
 * Represents a link between two ASTNodes.
 * <p>
 * When writing transformations that translate from one AST to another, it is often cleaner not to
 * translate all information in a single operation. Splitting this translation process however
 * necessitates that sequentially executed translations always operate on the same source and target
 * nodes. For this purpose a Link provides a way for the program to "remember" which source nodes
 * are associated with which target nodes.
 * <p>
 * Going beyond that, Links also form a tree that mirrors the structure in both ASTs. This is based
 * on the assumption that the overall structure of both source and target AST will also be
 * equivalent.
 * 
 * @author Sebastian Oberhoff
 */
public final class Link<S extends ASTNode, T extends ASTNode> implements Iterable<Link<?, ?>> {
  
  private final S source;
  
  private final T target;
  
  private final Link<?, ?> parent;
  
  private final Set<Link<?, ?>> childLinks = new LinkedHashSet<Link<?, ?>>();
  
  public Link(S source, T target, @Nullable Link<?, ?> parent) {
    this.source = source;
    this.target = target;
    this.parent = parent;
    if (parent != null) {
      parent.addChildLink(this);
    }
  }
  
  private void addChildLink(Link<?, ?> childLink) {
    childLinks.add(childLink);
  }
  
  /**
   * @return the source node of this Link
   */
  public S source() {
    return source;
  }
  
  /**
   * @return the target node of this Link
   */
  public T target() {
    return target;
  }
  
  /**
   * @return the parent Link of this Link
   */
  public Link<?, ?> parent() {
    return parent;
  }
  
  /**
   * @return the topmost Link in the tree to which this Link belongs
   */
  public Link<?, ?> rootLink() {
    List<Link<?, ?>> parents = Util.listTillNull(this, Link::parent);
    return Iterables.getLast(parents);
  }
  
  @Override
  public Iterator<Link<?, ?>> iterator() {
    Iterable<Link<?, ?>> subtree = TreeUtil.preOrder(this, link -> link.childLinks);
    return subtree.iterator();
  }
  
  /**
   * Looks up all Links in the subtree spanned by this Link by type of source and target. You can
   * use super classes like 'ASTNode.class' if you want to filter more loosely.
   * 
   * @param sourceType the class of the source node
   * @param targetType the class of the target node
   * @return the set of all Links with the specified source and target types
   */
  @SuppressWarnings(value = { "unchecked", "rawtypes" })
  public <O extends ASTNode, D extends ASTNode> Set<Link<O, D>> getLinks(Class<O> sourceType,
      Class<D> targetType) {
    
    Set matchingLinks = Sets.newLinkedHashSet(Iterables.filter(this, link ->
        sourceType.isInstance(link.source) && targetType.isInstance(link.target)));
    
    return matchingLinks;
  }
}
