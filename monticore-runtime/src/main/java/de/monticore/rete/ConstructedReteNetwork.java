package de.monticore.rete;

import de.monticore.ast.ASTNode;

public abstract class ConstructedReteNetwork {

  protected ReteNetwork network;
  protected FullReteFactEmitter fullReteFactEmitter;

  public ConstructedReteNetwork() {
    network = new ReteNetwork();
  }

  protected ReteNetwork getNetwork() {
    return network;
  }

  protected abstract void init();

  public void track(ASTNode node) {
    this.fullReteFactEmitter.addSubtree(node);
  }

  public void untrack(ASTNode node) {
    this.fullReteFactEmitter.removeSubtree(node);
  }
}
