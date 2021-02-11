/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class LeftRecursiveTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {


  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    ASTMCGrammar grammar = rootLink.source();
    MCGrammarInfo grammarInfo = new MCGrammarInfo(grammar.getSymbol());
    for(Link<ASTEnumProd, ASTCDEnum> link: rootLink.getLinks(ASTEnumProd.class, ASTCDEnum.class)){
      if(grammarInfo.isProdLeftRecursive(link.source().getName())){
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.LEFT_RECURSIVE.toString());
      }
    }
    for(Link<ASTInterfaceProd, ASTCDInterface> link: rootLink.getLinks(ASTInterfaceProd.class, ASTCDInterface.class)){
      if(grammarInfo.isProdLeftRecursive(link.source().getName())){
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.LEFT_RECURSIVE.toString());
      }
    }
    for(Link<ASTExternalProd, ASTCDInterface> link: rootLink.getLinks(ASTExternalProd.class, ASTCDInterface.class)){
      if(grammarInfo.isProdLeftRecursive(link.source().getName())){
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.LEFT_RECURSIVE.toString());
      }
    }
    for(Link<ASTClassProd, ASTCDClass> link: rootLink.getLinks(ASTClassProd.class, ASTCDClass.class)){
      if(grammarInfo.isProdLeftRecursive(link.source().getName())){
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.LEFT_RECURSIVE.toString());
      }
    }
    for(Link<ASTAbstractProd, ASTCDClass> link: rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class)){
      if(grammarInfo.isProdLeftRecursive(link.source().getName())){
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.LEFT_RECURSIVE.toString());
      }
    }
    return rootLink;
  }
}
