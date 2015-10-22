package de.monticore.emf._ast;

public class ASTENodeNodeFactory {
  
  protected static ASTENodeNodeFactory factory = null;
  
  private static ASTENodeNodeFactory getFactory() {
    if (factory == null) {
      factory = new ASTENodeNodeFactory();
    }
    return factory;
  }
  
  protected ASTENodeNodeFactory() {
  }
  
  public static ASTENodeList createASTENodeList() {
    return getFactory().doCreateASTENodeList();
  }
  
  public static ASTENodeList createASTENodeList(boolean strictlyOrdered) {
    return getFactory().doCreateASTENodeList(strictlyOrdered);
  }
  
  public ASTENodeList doCreateASTENodeList() {
    return new ASTENodeList();
  }
  
  public ASTENodeList doCreateASTENodeList(boolean strictlyOrdered) {
    return new ASTENodeList(strictlyOrdered);
  }
  
}
