/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * class adds Stereotype to CDAttributes whichs name is derived from the name of the
 * corresponding element
 * this means that no usage name was defined in the grammar
 * e.g State -> has derived name 'state' -> gets the Stereotype
 *     foo:State -> has a defined usage name  -> does NOT get the Stereotype
 */
public class DerivedAttributeName implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    // nonterminals
    Set<Link<ASTNonTerminal, ASTCDAttribute>> attributesFromNT =
        rootLink.getLinks(ASTNonTerminal.class, ASTCDAttribute.class);
    for (Link<ASTNonTerminal, ASTCDAttribute> links : attributesFromNT) {
      if (!links.source().isPresentUsageName()) {
        addDerivedStereotypeToAttributes(links.target());
      }
    }

    // constant groups
    Set<Link<ASTConstantGroup, ASTCDAttribute>> attributesFromConstant =
        rootLink.getLinks(ASTConstantGroup.class, ASTCDAttribute.class);
    for (Link<ASTConstantGroup, ASTCDAttribute> links : attributesFromConstant) {
      if (!links.source().isPresentUsageName()) {
        addDerivedStereotypeToAttributes(links.target());
      }
    }

    // terminals
    Set<Link<ASTTerminal, ASTCDAttribute>> attributesFromTerminal =
        rootLink.getLinks(ASTTerminal.class, ASTCDAttribute.class);
    for (Link<ASTTerminal, ASTCDAttribute> links : attributesFromTerminal) {
      if (!links.source().isPresentUsageName()) {
        addDerivedStereotypeToAttributes(links.target());
      }
    }

    // key terminals
    Set<Link<ASTKeyTerminal, ASTCDAttribute>> attributesFromKeyTerminal =
        rootLink.getLinks(ASTKeyTerminal.class, ASTCDAttribute.class);
    for (Link<ASTKeyTerminal, ASTCDAttribute> links : attributesFromKeyTerminal) {
      if (!links.source().isPresentUsageName()) {
        addDerivedStereotypeToAttributes(links.target());
      }
    }
    return rootLink;
  }

  protected void addDerivedStereotypeToAttributes(ASTCDAttribute attribute) {
    TransformationHelper.addStereoType(attribute,
        MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString(), "");
  }
}
