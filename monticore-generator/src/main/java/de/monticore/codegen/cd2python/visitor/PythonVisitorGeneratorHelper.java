/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.codegen.cd2python.visitor;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public class PythonVisitorGeneratorHelper extends GeneratorHelper{


    public PythonVisitorGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
        super(topAst, symbolTable);
    }

    /**
     * Indicates whether the handed over field symbol has a type which is a reference to a different class, i.e., an
     * object. This is required in order to the the information, if the "visit" has to be called.
     * @param cdFieldSymbol a single cdFieldSymbol object
     * @param cdSymbol a single cdSymbol object.
     * @return true if the visit method should be called
     */
    public static boolean hasSubRule(CDFieldSymbol cdFieldSymbol, CDSymbol cdSymbol){
        if (cdFieldSymbol.getType().getActualTypeArguments().size() > 0){
            return cdSymbol.getType(cdFieldSymbol.getType().getActualTypeArguments().get(0).getType().getName()).isPresent();
        }
        return false;
    }

    /**
     * Indicates whether the handed over symbol uses a collection which has to be represented as a list in python.
     * @param cdFieldSymbol a single field symbol
     * @return true if collection, otherwise false.
     */
    public boolean isListNode(CDFieldSymbol cdFieldSymbol){
        return isListAstNode(cdFieldSymbol);
    }

    /**
     * Removes the AST prefix as required to be conform to antlr.
     * @param name the name of a class
     * @return the name without prefix
     */
    public String getAntlrConformName(String name){
        if (name != null){
            return name.replaceFirst("^AST", "");
        }else{
            return "";
        }
    }

    public String getNameSingular(CDFieldSymbol fieldSymbol){
        return fieldSymbol.getName().substring(0,fieldSymbol.getName().length()-1);
    }

}
