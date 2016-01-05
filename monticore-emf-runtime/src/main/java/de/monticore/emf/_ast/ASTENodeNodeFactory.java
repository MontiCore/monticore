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
  
  
}
