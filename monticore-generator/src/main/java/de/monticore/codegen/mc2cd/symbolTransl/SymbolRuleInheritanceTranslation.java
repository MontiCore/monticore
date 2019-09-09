package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class SymbolRuleInheritanceTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTSymbolRule, ASTCDClass> link : rootLink.getLinks(ASTSymbolRule.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTSymbolRule, ASTCDInterface> link : rootLink.getLinks(ASTSymbolRule.class, ASTCDInterface.class)) {
      translateInterfaceProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private void translateInterfaceProd(ASTSymbolRule rule, ASTCDInterface cdInterface,
                                      ASTMCGrammar astGrammar) {
    for (ASTMCType superInterface : rule.getSuperClassList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdInterface);

      cdInterface.getInterfaceList().add(
          TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }

  private void translateClassProd(ASTSymbolRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    for (ASTMCType superClass : rule.getSuperClassList()) {
      String qualifiedSuperClass = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createObjectType(qualifiedSuperClass));
    }

    for (ASTMCType superInterface : rule.getSuperClassList()) {
      String qualifiedSuperInterface = TransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getInterfaceList()
          .add(TransformationHelper.createObjectType(qualifiedSuperInterface));
    }
  }
}
