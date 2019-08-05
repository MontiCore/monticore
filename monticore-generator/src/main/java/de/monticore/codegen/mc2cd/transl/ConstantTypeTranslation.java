/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesNodeFactory;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

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
      link.target().setMCType(MCBasicTypesNodeFactory.createASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN));
    }
    
    return rootLink;
  }
}
