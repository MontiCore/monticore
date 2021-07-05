/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class AttributeInASTsToCDAttributes implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(ASTASTRule.class,
            ASTCDClass.class)) {
      for (ASTAdditionalAttribute attributeInAST : link.source().getAdditionalAttributeList()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().setModifier(CD4CodeMill.modifierBuilder().build()).uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }

    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
            ASTCDInterface.class)) {
      for (ASTAdditionalAttribute attributeInAST :link.source().getAdditionalAttributeList()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().setModifier(CD4CodeMill.modifierBuilder().build()).uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }

    return rootLink;
  }

}
