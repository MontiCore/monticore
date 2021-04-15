/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.Link;
import de.se_rwth.commons.StringTransformations;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.getClassProdName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;
import static de.monticore.grammar.MCGrammarSymbolTableHelper.getConstantGroupName;

/**
 * This function copies over names from source to target nodes.
 *
 */
public class NameTranslation implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

    @Override
    public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
            Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

        for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
                ASTCDDefinition.class)) {
            link.target().setName(link.source().getName());
        }

        for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
                ASTCDClass.class)) {
            String className = getClassProdName(link.source());
            link.target().setName("AST" + className);
        }

        for (Link<ASTEnumProd, ASTCDEnum> link : rootLink.getLinks(ASTEnumProd.class,
                ASTCDEnum.class)) {
            String enumName = link.source().getName();
            link.target().setName("AST" + enumName);
        }

        for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
                ASTCDClass.class)) {
            link.target().setName("AST" + link.source().getName());
        }

        for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
                ASTCDInterface.class)) {
            link.target().setName("AST" + link.source().getName());
        }

        for (Link<ASTExternalProd, ASTCDInterface> link : rootLink.getLinks(ASTExternalProd.class,
                ASTCDInterface.class)) {
            link.target().setName("AST" + link.source().getName() + "Ext");
        }

        for (Link<ASTNonTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTNonTerminal.class,
                ASTCDAttribute.class)) {
            Optional<String> usageName = getUsageName(rootLink.source(), link.source());
            String nameToUse = usageName.isPresent() ? usageName.get() : StringTransformations.uncapitalize(link.source().getName());
            link.target().setName(nameToUse);
        }

        for (Link<ASTITerminal, ASTCDAttribute> link : rootLink.getLinks(ASTITerminal.class,
                ASTCDAttribute.class)) {
            Optional<String> usageName = getUsageName(rootLink.source(), link.source());
            String nameToUse = usageName.isPresent() ? usageName.get() : link.source().getName();
            link.target().setName(nameToUse);
        }

        for (Link<ASTConstantGroup, ASTCDAttribute> link : rootLink.getLinks(ASTConstantGroup.class,
            ASTCDAttribute.class)) {
            Optional<String> usageName = getUsageName(rootLink.source(), link.source());
            String nameToUse = usageName.isPresent() ? usageName.get() :
                getConstantName(link.source().getSymbol());
            link.target().setName(nameToUse);
        }

        for (Link<ASTAdditionalAttribute, ASTCDAttribute> link : rootLink.getLinks(ASTAdditionalAttribute.class,
            ASTCDAttribute.class)) {
            String alternativeName = StringTransformations.uncapitalize(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(link.source().getMCType()));
            String name = link.source().isPresentName() ? link.source().getName() : alternativeName;
            link.target().setName(name);
            link.source().setName(name);
        }

        for (Link<ASTConstant, ASTCDAttribute> link : rootLink.getLinks(ASTConstant.class,
                ASTCDAttribute.class)) {
            Optional<String> usageName = getUsageName(rootLink.source(), link.source());
            // The semicolons surrounding string productions are being kept by the parser
            String nameToUse = usageName.isPresent() ? usageName.get() : link.source().getName()
                    .replaceAll("\"", "");
            link.target().setName(nameToUse);
        }

        return rootLink;
    }
  
  protected String getConstantName(RuleComponentSymbol compSymbol) {
    if (compSymbol.isIsConstantGroup() && compSymbol.isPresentAstNode()
        && compSymbol.getAstNode() instanceof ASTConstantGroup) {
      return getConstantGroupName((ASTConstantGroup) compSymbol.getAstNode());
    }
    if (compSymbol.isIsConstant() && compSymbol.isPresentAstNode()
        && compSymbol.getAstNode() instanceof ASTConstant) {
      return
          getAttributeNameForConstant((ASTConstant) compSymbol.getAstNode());
    }
    return "";
  }
  
  protected String getAttributeNameForConstant(ASTConstant astConstant) {
    String name;
    
    if (astConstant.isPresentUsageName()) {
      name = astConstant.getUsageName();
    } else {
      String constName = astConstant.getName();
      if (matchesJavaIdentifier(constName)) {
        name = constName;
      } else {
        name = LexNamer.createGoodName(constName);
        if (name.isEmpty()) {
          name = constName;
        }
      }
    }
    return name;
  }
  
  protected boolean matchesJavaIdentifier(String checkedString) {
    if (checkedString == null || checkedString.length() == 0) {
      return false;
    }
    char[] stringAsChars = checkedString.toCharArray();
    if (!Character.isJavaIdentifierStart(stringAsChars[0])) {
      return false;
    }
    for (int i = 1; i < stringAsChars.length; i++) {
      if (!Character.isJavaIdentifierPart(stringAsChars[i])) {
        return false;
      }
    }
    return true;
  }

}
