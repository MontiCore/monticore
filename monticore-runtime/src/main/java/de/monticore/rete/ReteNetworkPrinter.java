/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.rete.nodes.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Prints the structure of a {@link ReteNetwork} as a
 * <a href="https://mermaid.js.org/syntax/flowchart.html">Mermaid flowchart</a>,
 * e.g. to visualize a network in Markdown documentation or to debug its
 * construction.
 * <p>
 * Each node is shown with its kind, its parameters (e.g. the key columns of
 * an indexer or the parameter names of a production) and the types of its
 * columns, where {@code ?} marks optional columns. Edges point in the
 * direction of the data flow, from the input nodes to the productions; the
 * edges from an indexer to a join, left outer join or existence node are
 * labeled with the side of the indexer. Nodes not reachable from an
 * input node (i.e. disposed nodes) are not printed.
 */
public final class ReteNetworkPrinter {

  private ReteNetworkPrinter() {
  }

  /**
   * @return the Mermaid code of a flowchart showing all nodes of the network
   * and their connections, starting with {@code flowchart TD}
   */
  public static String print(ReteNetwork network) {
    // collect the nodes in the order of discovery, starting at the inputs
    Map<Object, String> ids = new IdentityHashMap<>();
    List<Object> nodes = new ArrayList<>();
    Set<String> edges = new LinkedHashSet<>();
    Deque<Object> toVisit = new ArrayDeque<>();
    List<Object> inputs = new ArrayList<>(network.typeInputs.values());
    inputs.addAll(network.featureInputs.values());
    for (Object input : inputs) {
      register(input, ids, nodes, toVisit);
    }
    while (!toVisit.isEmpty()) {
      Object node = toVisit.poll();
      if (node instanceof TupleSource source) {
        for (Receiver child : source.getChildren()) {
          register(child, ids, nodes, toVisit);
          edges.add(ids.get(node) + " --> " + ids.get(child));
        }
      }
      if (node instanceof Indexer indexer) {
        for (IndexerListener listener : indexer.getListeners()) {
          register(listener, ids, nodes, toVisit);
          edges.add(ids.get(node) + " -->|" + side(indexer, listener) + "| " + ids.get(listener));
        }
      }
    }

    StringBuilder sb = new StringBuilder("flowchart TD\n");
    for (Object node : nodes) {
      sb.append("  ").append(ids.get(node)).append(shape(node, label(node))).append('\n');
    }
    for (String edge : edges) {
      sb.append("  ").append(edge).append('\n');
    }
    sb.append("  classDef input fill:#dbeafe,stroke:#1d4ed8\n");
    sb.append("  classDef memory fill:#fef3c7,stroke:#b45309\n");
    sb.append("  classDef production fill:#dcfce7,stroke:#15803d\n");
    appendClass(sb, "input", nodes, ids, n -> n instanceof TypeInputNode || n instanceof FeatureInputNode);
    appendClass(sb, "memory", nodes, ids, n -> n instanceof Indexer);
    appendClass(sb, "production", nodes, ids, n -> n instanceof ProductionNode);
    return sb.toString();
  }

  private static void register(Object node, Map<Object, String> ids, List<Object> nodes,
      Deque<Object> toVisit) {
    if (!ids.containsKey(node)) {
      ids.put(node, "n" + ids.size());
      nodes.add(node);
      toVisit.add(node);
    }
  }

  /**
   * @return the side(s) of a join or existence node the indexer is attached to
   */
  private static String side(Indexer indexer, IndexerListener listener) {
    Indexer left = null;
    Indexer right = null;
    if (listener instanceof JoinNode join) {
      left = join.getLeft();
      right = join.getRight();
    } else if (listener instanceof ExistenceNode existence) {
      left = existence.getLeft();
      right = existence.getRight();
    } else if (listener instanceof LeftOuterJoinNode outerJoin) {
      left = outerJoin.getLeft();
      right = outerJoin.getRight();
    }
    if (left == indexer && right == indexer) {
      return "left, right";
    }
    return left == indexer ? "left" : right == indexer ? "right" : "";
  }

  /**
   * @return the lines of the node's label: its kind, its parameters and its
   * column types
   */
  private static List<String> label(Object node) {
    List<String> lines = new ArrayList<>();
    if (node instanceof TypeInputNode<?> input) {
      lines.add("TypeInput");
      lines.add(input.getType().getSimpleName());
    } else if (node instanceof FeatureInputNode<?, ?> input) {
      lines.add("FeatureInput");
      lines.add(input.getFeature().toString());
    } else if (node instanceof Indexer indexer) {
      lines.add("Indexer");
      lines.add("key " + Arrays.toString(indexer.getMask().getIndices()) + ": "
          + ColumnTypes.toString(indexer.getKeyTypes(), indexer.getKeyOptionality()));
    } else if (node instanceof JoinNode) {
      lines.add("Join");
    } else if (node instanceof LeftOuterJoinNode) {
      lines.add("LeftOuterJoin (optional)");
    } else if (node instanceof ExistenceNode existence) {
      lines.add(existence.isNegative() ? "AntiJoin (not exists)" : "SemiJoin (exists)");
    } else if (node instanceof FilterNode) {
      lines.add("Filter");
    } else if (node instanceof EvalNode) {
      lines.add("Eval");
    } else if (node instanceof ProjectionNode projection) {
      lines.add("Projection " + Arrays.toString(projection.getMask().getIndices()));
    } else if (node instanceof DistinctNode) {
      lines.add("Distinct");
    } else if (node instanceof UnionNode) {
      lines.add("Union");
    } else if (node instanceof AggregatorNode aggregator) {
      String column = aggregator.getAggregatedColumn() == AggregatorNode.WHOLE_TUPLE
          ? "" : " of column " + aggregator.getAggregatedColumn();
      lines.add("Aggregator " + aggregator.getOperator() + column);
      lines.add("group by " + Arrays.toString(aggregator.getGroupMask().getIndices()));
    } else if (node instanceof TransitiveClosureNode) {
      lines.add("TransitiveClosure");
    } else if (node instanceof ProductionNode production) {
      lines.add("Production");
      lines.add("(" + String.join(", ", production.getParameterNames()) + ")");
    } else {
      lines.add(node.getClass().getSimpleName());
    }
    if (node instanceof TupleSource source) {
      lines.add(ColumnTypes.toString(source.getColumnTypes(), source.getColumnOptionality()));
    } else if (node instanceof ProductionNode production) {
      lines.add(ColumnTypes.toString(production.getParent().getColumnTypes(),
          production.getParent().getColumnOptionality()));
    }
    return lines;
  }

  /**
   * @return the Mermaid shape of a node: stadium for inputs, cylinder for
   * indexers (memories), subroutine for productions and rectangle otherwise
   */
  private static String shape(Object node, List<String> lines) {
    String text = "\"" + lines.stream().map(ReteNetworkPrinter::escape)
        .collect(Collectors.joining("<br/>")) + "\"";
    if (node instanceof TypeInputNode || node instanceof FeatureInputNode) {
      return "([" + text + "])";
    } else if (node instanceof Indexer) {
      return "[(" + text + ")]";
    } else if (node instanceof ProductionNode) {
      return "[[" + text + "]]";
    }
    return "[" + text + "]";
  }

  /**
   * Escapes the characters with a special meaning in Mermaid labels.
   */
  private static String escape(String text) {
    return text.replace("\"", "#quot;").replace("<", "#lt;").replace(">", "#gt;");
  }

  private static void appendClass(StringBuilder sb, String styleClass, List<Object> nodes,
      Map<Object, String> ids, Predicate<Object> filter) {
    List<String> members = nodes.stream().filter(filter).map(ids::get).toList();
    if (!members.isEmpty()) {
      sb.append("  class ").append(String.join(",", members)).append(' ').append(styleClass).append('\n');
    }
  }
}
