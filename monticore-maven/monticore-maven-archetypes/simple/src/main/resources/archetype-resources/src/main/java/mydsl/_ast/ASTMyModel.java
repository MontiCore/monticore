package ${package}.mydsl._ast;

import java.util.ArrayList;
import java.util.List;

public class ASTMyModel extends ASTMyModelTOP {

  /**
   * create convenient method for AST class, by extending it with the TOP mechanism
   */
  public List<ASTMyField> getAllInnerFields() {
    List<ASTMyField> myFields = new ArrayList<>();
    for (ASTMyElement astMyElement : getMyElementsList()) {
      for (ASTMyField astMyField : astMyElement.getMyFieldsList()) {
        myFields.add(astMyField);
      }
    }
    return myFields;
  }


}
