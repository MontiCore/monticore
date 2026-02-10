/* (c) https://github.com/MontiCore/monticore */
package numerals._visitor;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import numerals._ast.ASTFloat;
import numerals._ast.ASTInteger;

public class NumeralsInterpreter extends NumeralsInterpreterTOP {

  public NumeralsInterpreter(IModelInterpreter realThis) {
    super(realThis);
  }

  public MIValue interpret(ASTFloat node) {
    return MIValueFactory.createValue((float)
        (Integer.parseInt(node.getPre()) +
            Integer.parseInt(node.getPost()) * Math.pow(10, -node.getPost().length()))
            * (node.isNegative() ? -1 : 1));
  }

  public MIValue interpret(ASTInteger node) {
    return MIValueFactory.createValue(
        Integer.parseInt(node.getDigits()) * (node.isNegative() ? -1 : 1));
  }

}
