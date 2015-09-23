package mc.feature.statechart.statechartdsl._ast;

public class StatechartDSLNodeFactory extends StatechartDSLNodeFactoryTOP {
  
  protected StatechartDSLNodeFactory() {
  }
  
  public static ASTStatechartList createASTStatechartList() {
    if (factoryASTStatechartList == null) {
      factoryASTStatechartList = new StatechartDSLNodeFactory();
    }
    return factoryASTStatechartList.doCreateASTStatechartList();
  }
  
  protected ASTStatechartList doCreateASTStatechartList() {
    return new ASTStatechartList(false);
  }
  
}
