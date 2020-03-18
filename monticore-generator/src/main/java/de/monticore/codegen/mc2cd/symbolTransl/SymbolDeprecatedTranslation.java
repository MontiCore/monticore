package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.transl.DeprecatedTranslation;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

public class SymbolDeprecatedTranslation extends DeprecatedTranslation {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(
        ASTAbstractProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTExternalProd, ASTCDClass> link : rootLink.getLinks(
        ASTExternalProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    // because interfaces map to classes as well
    for (Link<ASTInterfaceProd, ASTCDClass> link : rootLink.getLinks(
        ASTInterfaceProd.class, ASTCDClass.class)) {
      translateProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }
}
