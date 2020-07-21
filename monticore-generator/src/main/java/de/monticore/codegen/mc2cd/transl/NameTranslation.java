/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.Link;
import de.se_rwth.commons.StringTransformations;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.getClassProdName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;

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
                MCGrammarSymbolTableHelper.getConstantName(link.source().getSymbol());
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
            // TODO: This is a workaround because the semicolons surrounding string productions are
            // currently being kept by the parser
            String nameToUse = usageName.isPresent() ? usageName.get() : link.source().getName()
                    .replaceAll("\"", "");
            link.target().setName(nameToUse);
        }

        return rootLink;
    }

}
