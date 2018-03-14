/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

public class ConstantTypeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTConstant, ASTCDAttribute> link : rootLink.getLinks(ASTConstant.class,
        ASTCDAttribute.class)) {
      // TODO SO <- GV: fix an attribute type according to this code
//      if (!att.isIterated()) {
//        if (attributeType.getEnumValues().size() > 1) {
//          attribDef.setObjectType("int");
//        }
//        else {
//          attribDef.setObjectType("boolean");
//        }
//      }
//      else {
//        if (attributeType.getEnumValues().size() > 1) {
//          attribDef.setObjectType("java.util.List<Integer>");
//        }
//        else {
//          attribDef.setObjectType("java.util.List<Boolean>");
//        }
//      }
      link.target().setType(TypesNodeFactory.createASTPrimitiveType(ASTConstantsTypes.BOOLEAN));
    }
    
    return rootLink;
  }
}
