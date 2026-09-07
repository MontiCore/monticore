/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype.cocos;

import de.monticore.umlstereotype.UMLStereotypeMill;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._cocos.UMLStereotypeASTStereoValueCoCo;
import de.se_rwth.commons.logging.Log;

/**
 *  In UML Stereo Values may only be String Literals. This CoCo enforces this
 *  requirement.
 */
public class StereoValueIsStringLiteral implements UMLStereotypeASTStereoValueCoCo {

  @Override
  public void check(ASTStereoValue node) {
    if (!node.isPresentExpression()) return;

    if (!(UMLStereotypeMill.typeDispatcher().isExpressionsBasisASTLiteralExpression(node.getExpression())
      && UMLStereotypeMill.typeDispatcher().isMCCommonLiteralsASTStringLiteral(
        UMLStereotypeMill.typeDispatcher().asExpressionsBasisASTLiteralExpression(node.getExpression())
          .getLiteral()))) {
      Log.error("0xFD726 UML Stereo values can only be String literals \""
          + node.getName() + "\" has a value which is not a String literal",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd());
    }
  }
}
