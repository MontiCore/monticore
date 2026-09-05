package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RelationshipGraphChangeProcessor implements IIncrementalListener {
  
  protected final RelationshipGraph graph;
  
  protected final Deque<DelayedEdgeUpdate> delayedEdgeUpdates;
  
  protected final Consumer<ASTNode> symbolTableUpdate;

  protected LinkedHashSet<ASTNode> danglingSubtrees;
  
  public RelationshipGraphChangeProcessor(RelationshipGraph graph,
      Consumer<ASTNode> symbolTableUpdate) {
    this.graph = graph;
    this.delayedEdgeUpdates = new ArrayDeque<>();
    this.symbolTableUpdate = symbolTableUpdate;
    this.danglingSubtrees = new LinkedHashSet<>();
  }

  @Override
  public void onTransformationEnd(@Nonnull String transformationName) {
    processDelayedEdgeUpdates();
    removeDanglingSubtrees();
  }

  @Override
  public void onASTNodeCreation(@Nonnull ASTNode node) {
    getRelationshipGraph().addNode(node);
  }

  @Override
  public void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    if (parent == null) {
      return;
    }
    this.danglingSubtrees.remove(node);
    getRelationshipGraph().connectNodes(node, parent, RGEdgeType.PARENT_EDGE, RGNode.PARENT_CODE);
  }


  @Override
  public void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    this.danglingSubtrees.add(node);
  }

  protected void removeDanglingSubtrees() {
    this.danglingSubtrees.forEach(node -> getRelationshipGraph().removeNodeRecursively(node));
    this.danglingSubtrees.clear();
  }

  protected RelationshipGraph getRelationshipGraph() {
    return graph;
  }
  
  protected void delayEdgeCreation(Supplier<ASTNode> sourceSupplier,
      Supplier<ASTNode> targetSupplier, RGEdgeType type, String label) {
    DelayedEdgeUpdate delayedEdgeUpdate =
        new DelayedEdgeUpdate(sourceSupplier, targetSupplier, type, label);
    this.delayedEdgeUpdates.add(delayedEdgeUpdate);
  }
  
  protected void processDelayedEdgeUpdates() {
    if (delayedEdgeUpdates.isEmpty()) {
      return;
    }
    getRelationshipGraph().getRoots().forEach(this.symbolTableUpdate);
    
    while (!this.delayedEdgeUpdates.isEmpty()) {
      DelayedEdgeUpdate delayedEdgeUpdate = this.delayedEdgeUpdates.pop();
      ASTNode source = delayedEdgeUpdate.sourceSupplier.get();
      ASTNode target = delayedEdgeUpdate.targetSupplier.get();
      if (source != null && target != null) {
        getRelationshipGraph().connectNodes(source, target, delayedEdgeUpdate.type,
            delayedEdgeUpdate.label);
      }
      else {
        Log.error(
            "Failed to connect nodes in the relationship graph, as one of the nodes is null. Source: "
                + source + ", Target: " + target);
      }
    }
  }
  
  protected record DelayedEdgeUpdate(Supplier<ASTNode> sourceSupplier,
      Supplier<ASTNode> targetSupplier, RGEdgeType type, String label) {
    
  }
}
