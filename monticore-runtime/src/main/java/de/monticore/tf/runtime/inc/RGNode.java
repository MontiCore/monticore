package de.monticore.tf.runtime.inc;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class RGNode {
  protected final ASTNode astNode;
  protected final Class<? extends ASTNode> clazz;
  protected final Multimap<Class<? extends ASTNode>, RGEdge> incoming;
  protected final Multimap<Class<? extends ASTNode>, RGEdge> outgoing;

  protected final static String PARENT_CODE = "#PARENT";

  public RGNode(ASTNode astNode) {
    this.astNode = astNode;
    this.clazz = astNode.getClass();
    this.incoming = LinkedHashMultimap.create();
    this.outgoing = LinkedHashMultimap.create();
  }
  
  public ASTNode getAstNode() {
    return astNode;
  }
  
  public Class<? extends ASTNode> getClazz() {
    return clazz;
  }
  
  public Multimap<Class<? extends ASTNode>, RGEdge> getIncoming() {
    return incoming;
  }
  
  public Multimap<Class<? extends ASTNode>, RGEdge> getOutgoing() {
    return outgoing;
  }
  
  public Collection<RGEdge> getOutgoingEdgesByTargetType(Class<? extends ASTNode> clazz) {
    return getOutgoingEdgesByTargetTypeStream(clazz).toList();
  }
  
  public Collection<RGEdge> getIncomingEdgesBySourceType(Class<? extends ASTNode> clazz) {
    return getIncomingEdgesBySourceTypeStream(clazz).toList();
  }
  
  public Collection<RGNode> getOutgoingEdgeNodesByTargetType(Class<? extends ASTNode> clazz) {
    return getOutgoingEdgeNodesByTargetTypeStream(clazz).toList();
  }
  
  public Collection<RGNode> getIncomingEdgeNodesBySourceType(Class<? extends ASTNode> clazz) {
    return getIncomingEdgeNodesBySourceTypeStream(clazz).toList();
  }

  public Stream<RGEdge> getOutgoingEdgesByTargetTypeStream(Class<? extends ASTNode> clazz) {
    return this.outgoing.get(clazz).stream()
        .filter(x -> !x.getType().equals(RGEdgeType.PARENT_EDGE));
  }

  public Stream<RGEdge> getIncomingEdgesBySourceTypeStream(Class<? extends ASTNode> clazz) {
    return this.incoming.get(clazz).stream()
        .filter(x -> !x.getType().equals(RGEdgeType.PARENT_EDGE));
  }

  public Stream<RGNode> getOutgoingEdgeNodesByTargetTypeStream(Class<? extends ASTNode> clazz) {
    return this.outgoing.get(clazz).stream()
        .filter(x -> !x.getType().equals(RGEdgeType.PARENT_EDGE))
        .map(RGEdge::getTarget);
  }

  public Stream<RGNode> getIncomingEdgeNodesBySourceTypeStream(Class<? extends ASTNode> clazz) {
    return this.incoming.get(clazz).stream()
        .filter(x -> !x.getType().equals(RGEdgeType.PARENT_EDGE))
        .map(RGEdge::getSource);
  }
  
  public Collection<RGNode> getChildren() {
    return getChildrenStream().toList();
  }

  public Stream<RGNode> getChildrenStream() {
    return this.incoming.values().stream()
        .filter(x -> x.getType().equals(RGEdgeType.PARENT_EDGE))
        .map(RGEdge::getSource);
  }
  
  public Collection<RGNode> getChildrenByType(Class<? extends ASTNode> clazz) {
    return this.incoming.get(clazz).stream()
        .filter(x -> x.getType().equals(RGEdgeType.PARENT_EDGE))
        .map(RGEdge::getSource)
        .toList();
  }
  
  public RGNode getParent() {
    List<RGNode> parents = this.outgoing.values().stream()
        .filter(x -> x.getType().equals(RGEdgeType.PARENT_EDGE))
        .map(RGEdge::getTarget)
        .toList();
    if (parents.size() != 1) {
      Log.error("Tried to infer parent but found " + parents.size());
      return null;
    }
    return parents.getFirst();
  }
  
  public void addEdge(RGEdge edge) {
   if (this.equals(edge.getSource())) {
     this.outgoing.put(edge.getTarget().getClazz(), edge);
   } else if(this.equals(edge.getTarget())) {
     this.incoming.put(edge.getSource().getClazz(), edge);
   }
  }
  
  public void removeEdge(RGEdge edge) {
    if (this.equals(edge.getSource())) {
      this.outgoing.remove(edge.getTarget().getClazz(), edge);
    } else if(this.equals(edge.getTarget())) {
      this.incoming.remove(edge.getSource().getClazz(), edge);
    }
  }
}
