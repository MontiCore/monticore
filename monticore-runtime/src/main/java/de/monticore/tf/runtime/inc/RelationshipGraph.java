package de.monticore.tf.runtime.inc;

import com.google.common.collect.*;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RelationshipGraph {

  protected final Set<ASTNode> roots;
  protected final HashMap<ASTNode, RGNode> nodes;
  protected final Multimap<Class<? extends ASTNode>, RGNode> nodeTypes;
  protected final Multimap<String, RGEdge> edges;

  public RelationshipGraph(Set<ASTNode> roots) {
    this.roots = roots;
    this.nodes = Maps.newHashMap();
    this.nodeTypes = HashMultimap.create();
    this.edges = HashMultimap.create();
  }

  public RGNode getNode(ASTNode node) {
    return this.nodes.computeIfAbsent(node, RGNode::new);
  }

  public Collection<RGNode> getNodesByType(Class<? extends ASTNode> clazz) {
    return this.nodeTypes.get(clazz);
  }

  public RGNode addNode(ASTNode node) {
    RGNode rgNode = this.nodes.computeIfAbsent(node, RGNode::new);
    this.nodeTypes.put(rgNode.getClazz(), rgNode);
    return rgNode;
  }

  public void connectNodes(ASTNode node1, ASTNode node2, RGEdgeType type, String label) {
    RGNode rgNode1 = nodes.get(node1);
    RGNode rgNode2 = nodes.get(node2);

    if (rgNode1 == null) {
      Log.error("Missing RGNode of type: " + node1.getClass().getCanonicalName());
      return;
    }
    else if (rgNode2 == null) {
      Log.error("Missing RGNode of type: " + node2.getClass().getCanonicalName());
      return;
    }

    RGEdge edge = new RGEdge(rgNode1, rgNode2, type, label);
    rgNode1.addEdge(edge);
    rgNode2.addEdge(edge);
    edges.put(label, edge);
  }

  public void removeNode(ASTNode node) {
    Optional.ofNullable(nodes.get(node)).ifPresent(this::removeNode);
  }

  public void removeNode(RGNode node) {
    if (node == null) {
      return;
    }
    this.nodes.remove(node.getAstNode());
    this.nodeTypes.remove(node.getClazz(), node);
    List<RGEdge> edgesToRemove = Streams.concat(
        node.getIncoming().values().stream(),
        node.getOutgoing().values().stream()
    ).toList();
    edgesToRemove.forEach(this::removeEdge);
  }

  public void removeNodeRecursively(ASTNode node) {
    Optional.ofNullable(nodes.get(node)).ifPresent(this::removeNodeRecursively);
  }

  public void removeNodeRecursively(RGNode node) {
    removeNode(node);
    node.getChildren().forEach(this::removeNodeRecursively);
  }

  protected void removeEdge(RGEdge edge) {
    this.edges.remove(edge.getLabel(), edge);
    edge.getSource().removeEdge(edge);
    edge.getTarget().removeEdge(edge);
  }

  public Set<ASTNode> getRoots() {
    return roots;
  }

  public String getAsMermaid(boolean astOnly) {
    IndentPrinter printer = new IndentPrinter();
    printer.println("flowchart BT");
    printer.indent();

    for (RGNode node : this.nodes.values()) {
      try {
        Method method = node.getClazz().getMethod("getName");
        printer.println("%s@{ shape: stadium, label: \"%s <br> (%s)\" }".formatted(node.hashCode(),
            node.getClazz().getSimpleName(), method.invoke(node.getAstNode())));
      }
      catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        printer.println("%s@{ shape: stadium, label: \"%s\" }".formatted(node.hashCode(),
            node.getClazz().getSimpleName()));
      }
    }

    for (RGEdge edge : this.edges.values()) {
      if (edge.getType().equals(RGEdgeType.PARENT_EDGE)) {
        printer.println("%d-- %s -->%d".formatted(edge.getSource().hashCode(), edge.getLabel(),
            edge.getTarget().hashCode()));
      }
      else if (!astOnly) {
        printer.println("%d-. %s .->%d".formatted(edge.getSource().hashCode(), edge.getLabel(),
            edge.getTarget().hashCode()));
      }
    }

    return printer.getContent();
  }
}
