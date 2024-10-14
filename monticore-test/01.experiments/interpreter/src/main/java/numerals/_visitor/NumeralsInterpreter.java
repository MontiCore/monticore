/* (c) https://github.com/MontiCore/monticore */
package numerals._visitor;

import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import numerals._ast.ASTFloat;
import numerals._ast.ASTInteger;

public class NumeralsInterpreter extends NumeralsInterpreterTOP {

  public NumeralsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  public Value interpret(ASTFloat node) {
    return ValueFactory.createValue((float)
        (Integer.parseInt(node.getPre()) +
        Integer.parseInt(node.getPost()) * Math.pow(10, -node.getPost().length()))
            * (node.isNegative() ? -1 : 1));
  }

  public Value interpret(ASTInteger node) {
    return ValueFactory.createValue(
        Integer.parseInt(node.getDigits()) * (node.isNegative() ? -1 : 1));
  }

}
