package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class StartProdTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    if (rootLink.source().isPresentStartRule()) {
      String startProdRuleFullName = rootLink.source().getSymbol().getStartProd().get().getFullName();
      TransformationHelper.addStereoType(rootLink.target().getCDDefinition(), MC2CDStereotypes.START_PROD.toString(), startProdRuleFullName);
    }
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class, ASTCDClass.class)) {
      if (link.source().getSymbol().isIsStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class)) {
      if (link.source().getSymbol().isIsStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class, ASTCDInterface.class)) {
      if (link.source().getSymbol().isIsStartProd()) {
        TransformationHelper.addStereoType(link.target(), MC2CDStereotypes.START_PROD.toString());
      }
    }
    return rootLink;
  }
}
