package de.monticore.rete;

import de.monticore.ast.ASTNode;
import de.monticore.rete.nodes.ProductionNode;

import java.util.HashMap;
import java.util.Map;

public abstract class ConstructedReteNetwork {
  protected Map<String, ProductionNode> patterns;
  protected ReteNetwork network;
  protected FullReteFactEmitter fullReteFactEmitter;

  public ConstructedReteNetwork() {
    patterns = new HashMap<>();
    network = new ReteNetwork();
  }

  protected ReteNetwork getNetwork() {
    return network;
  }

  protected abstract void init();

  public abstract void track(ASTNode node);

  public abstract void untrack(ASTNode node);

  protected ProductionNode getPattern(String name) {
    return patterns.get(name);
  }

}
