/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.cd.facade.CDModifier.PROTECTED;

public class AttributeInSymbolRuleToCDAttribute implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTSymbolRule, ASTCDClass> link : rootLink.getLinks(ASTSymbolRule.class,
            ASTCDClass.class)) {
      for (ASTAdditionalAttribute attributeInAST : link.source().getAdditionalAttributeList()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().
                setName(attributeInAST.getName()).
                setModifier(PROTECTED.build()).uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
    return rootLink;
  }

}
