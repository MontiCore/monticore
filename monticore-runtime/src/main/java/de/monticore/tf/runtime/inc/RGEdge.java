package de.monticore.tf.runtime.inc;

public class RGEdge {
  protected final RGNode source;
  protected final RGNode target;
  protected final RGEdgeType type;
  protected final String label;
  
  public RGEdge(RGNode source, RGNode target, RGEdgeType type, String label) {
    this.source = source;
    this.target = target;
    this.type = type;
    this.label = label;
  }
  
  public RGNode getSource() {
    return source;
  }
  
  public RGNode getTarget() {
    return target;
  }
  
  public RGEdgeType getType() {
    return type;
  }
  
  public String getLabel() {
    return label;
  }
}
