package mc.feature.statechart.statechartdsl._ast;

public class StatechartDSLNodeFactory extends StatechartDSLNodeFactoryTOP {
  
  protected StatechartDSLNodeFactory() {
  }
  
  public static ASTStatechart createASTStatechart() {
    if (factoryASTStatechart == null) {
      factoryASTStatechart = new StatechartDSLNodeFactory();
    }
    return factoryASTStatechart.doCreateASTStatechart();
  }
  
  protected ASTStatechart doCreateASTStatechart() {
    ASTStatechart s = new ASTStatechart();
    s.setName("default");
    return s;
  }
  
}
