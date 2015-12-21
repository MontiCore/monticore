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

import de.monticore.codegen.mc2cd.transl.creation.CDASTCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * The term translation applies to transformations that transfer information from one AST to
 * another.
 * This translation is a composite of multiple atomic translations. It takes in a single root Link,
 * builds up the entire CD AST from there and then transfers various pieces of information from the
 * MC AST to the CD AST.
 *
 * @author Sebastian Oberhoff
 */
public class MC2CDTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  private GlobalExtensionManagement glex;

  public MC2CDTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new CDASTCreator()
        .andThen(new InheritedAttributesTranslation())
        .andThen(new PackageTranslation())
        .andThen(new StarImportSuperGrammarTranslation())
        .andThen(new NameTranslation())
        .andThen(new ExtendsTranslation())
        .andThen(new ImplementsTranslation())
        .andThen(new ASTRuleInheritanceTranslation())
        .andThen(new MethodTranslation(glex))
        .andThen(new OverridingClassProdTranslation())
        .andThen(new RemoveOverriddenAttributesTranslation())
        .andThen(new AbstractProdModifierTranslation())
        .andThen(new ReferenceTypeTranslation())
        .andThen(new EnumTranslation())
        .andThen(new ExternalImplementationTranslation())
        .andThen(new ConstantTypeTranslation())
        .andThen(new CreateConstantAttributeTranslation())
        .andThen(new MultiplicityTranslation())
        .andThen(new ConstantsTranslation())
        .andThen(new NonTerminalsWithSymbolReferenceToCDAttributeStereotypes())
        .apply(rootLink);
  }
}
