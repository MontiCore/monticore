/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactoryTOP;

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
