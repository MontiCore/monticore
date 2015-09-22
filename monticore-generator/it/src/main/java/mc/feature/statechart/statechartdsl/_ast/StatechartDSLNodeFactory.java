package mc.feature.statechart.statechartdsl._ast;

public class StatechartDSLNodeFactory extends StatechartDSLNodeFactoryTOP {
  
  private static StatechartDSLNodeFactoryTOP getFactory() {
    if (factory == null) {
      factory = new StatechartDSLNodeFactory();
    }
    return factory;
  }
  
  protected StatechartDSLNodeFactory() {
  }
  
  public static ASTStatechartList createASTStatechartList() {
    if (factoryASTStatechartList == null) {
      factoryASTStatechartList = getFactory();
    }
    return factoryASTStatechartList.doCreateASTStatechartList();
  }
  
  protected ASTStatechartList doCreateASTStatechartList() {
    return new ASTStatechartList(false);
  }
  
}
