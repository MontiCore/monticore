// (c) https://github.com/MontiCore/monticore
package de.monticore.regex.regextype.types3;

import de.monticore.regex.regextype._ast.ASTRegExType;
import de.monticore.regex.regextype._visitor.RegExTypeVisitor2;
import de.monticore.regex.regularexpressions.RegularExpressionsMill;
import de.monticore.regex.regularexpressions._ast.ASTRegularExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types3.AbstractTypeVisitor;

public class RegExTypeTypeVisitor extends AbstractTypeVisitor
    implements RegExTypeVisitor2 {

  @Override
  public void endVisit(ASTRegExType node) {
    ASTRegularExpression astRegex = node.getRegExLiteral().getRegularExpression();
    String regex = RegularExpressionsMill.prettyPrint(astRegex, false);
    SymTypeOfRegEx regExType = SymTypeExpressionFactory.createTypeRegEx(regex);
    getType4Ast().setTypeOfTypeIdentifier(node, regExType);
  }

}
