/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.EssentialTransformationHelper.*;

/**
 * This function copies over names from source to target nodes.
 *
 * @author Sebastian Oberhoff
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
            String nameToUse = usageName.isPresent() ? usageName.get() : link.source().getName();
            link.target().setName(nameToUse);
        }

        for (Link<ASTTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTTerminal.class,
                ASTCDAttribute.class)) {
            Optional<String> usageName = getUsageName(rootLink.source(), link.source());
            String nameToUse = usageName.isPresent() ? usageName.get() : link.source().getName();
            link.target().setName(nameToUse);
        }

        for (Link<ASTAttributeInAST, ASTCDAttribute> link : rootLink.getLinks(ASTAttributeInAST.class,
                ASTCDAttribute.class)) {
            String name = link.source().getName().orElse(null);
            String alternativeName = link.source().getGenericType().getTypeName();
            String nameToUse = name != null ? name : alternativeName;
            link.target().setName(nameToUse);
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
