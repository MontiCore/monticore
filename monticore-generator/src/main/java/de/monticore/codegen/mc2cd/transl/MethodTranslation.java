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

import java.util.function.UnaryOperator;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar._ast.ASTMethodParameter;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;
import de.se_rwth.commons.Names;

/**
 * Translates Methods belonging to ASTRules into CDMethods and attaches them to
 * the corresponding CDClasses.
 * 
 * @author Sebastian Oberhoff
 */
public class MethodTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  private GlobalExtensionManagement glex;
  
  /**
   * Constructor for
   * de.monticore.codegen.mc2cd.transl.MethodTranslation
   * 
   * @param glex
   */
  public MethodTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDClass.class)) {
      for (ASTMethod method : link.source().getMethods()) {
        link.target().getCDMethods().add(translateASTMethodToASTCDMethod(method));
      }
    }
    
    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDInterface.class)) {
      for (ASTMethod method : link.source().getMethods()) {
        link.target().getCDMethods().add(translateASTMethodToASTCDMethod(method));
      }
    }
    
    return rootLink;
  }
  
  private ASTCDMethod translateASTMethodToASTCDMethod(ASTMethod method) {
    ASTCDMethod cdMethod = CD4AnalysisNodeFactory.createASTCDMethod();
    cdMethod.setModifier(TransformationHelper.createPublicModifier());
    cdMethod.setName(method.getName());
    // TODO SO,GV Add  generics
    String dotSeparatedName = Names.getQualifiedName(method.getReturnType()
        .getNames());
    cdMethod.setReturnType(TransformationHelper.createSimpleReference(dotSeparatedName));
    for (ASTMethodParameter param: method.getMethodParameters()) {
      // TODO SO,GV Add  generics
      String typeName = Names.getQualifiedName(param.getType().getNames());
      cdMethod.getCDParameters().add(TransformationHelper.createParameter(typeName, param.getName()));
    }
    if (method.getBody() instanceof ASTAction) {
      StringBuffer code = new StringBuffer();
      for (ASTBlockStatement action: ((ASTAction) method.getBody()).getBlockStatements()) {
        code.append(GeneratorHelper.getJavaPrettyPrinter().prettyprint(action));
      }
      HookPoint methodBody = new StringHookPoint(code.toString());
      glex.replaceTemplate(CdDecorator.EMPTY_BODY_TEMPLATE, cdMethod, methodBody);
    }
    return cdMethod;
  }
}
