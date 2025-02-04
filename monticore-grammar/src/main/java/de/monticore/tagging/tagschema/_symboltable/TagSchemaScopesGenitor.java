/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.tagschema._symboltable;

import de.monticore.tagging.tagschema._ast.*;


public class TagSchemaScopesGenitor extends TagSchemaScopesGenitorTOP {

  @Override
  public void visit(ASTTagSchema node) {
    super.visit(node);
  }

  @Override
  public void endVisit(ASTTagSchema node) {
    super.endVisit(node);
  }


  @Override
  public void endVisit(ASTSimpleTagType node) {
    super.endVisit(node);
    node.getSymbol().setPrivate(node.isPrivate());
    node.getSymbol().setScopeWildcard(node.getTagScope().isWildcard());
    node.getTagScope().getScopeIdentifierList().forEach(si -> node.getSymbol().addScopes(getScopeIdentifier(si)));
  }

  @Override
  public void endVisit(ASTValuedTagType node) {
    super.endVisit(node);
    node.getSymbol().setPrivate(node.isPrivate());
    node.getSymbol().setScopeWildcard(node.getTagScope().isWildcard());
    node.getTagScope().getScopeIdentifierList().forEach(si -> node.getSymbol().addScopes(getScopeIdentifier(si)));
    node.getSymbol().setType(node.getType());
  }

  @Override
  public void endVisit(ASTEnumeratedTagType node) {
    super.endVisit(node);
    node.getSymbol().setPrivate(node.isPrivate());
    node.getSymbol().setScopeWildcard(node.getTagScope().isWildcard());
    node.getTagScope().getScopeIdentifierList().forEach(si -> node.getSymbol().addScopes(getScopeIdentifier(si)));
    node.getSymbol().setValuesList(node.getStringList());
  }

  @Override
  public void endVisit(ASTComplexTagType node) {
    super.endVisit(node);
    node.getSymbol().setPrivate(node.isPrivate());
    node.getSymbol().setScopeWildcard(node.getTagScope().isWildcard());
    node.getTagScope().getScopeIdentifierList().forEach(si -> node.getSymbol().addScopes(getScopeIdentifier(si)));
  }

  @Override
  public void endVisit(ASTReference node){
    super.endVisit(node);
    node.getSymbol().setReferenceType(node.getReferenceTyp().getType());
    if (node.getReferenceTyp().isPresentName())
      node.getSymbol().setReferencedTag(node.getReferenceTyp().getName());
    else
      node.getSymbol().setReferencedTagAbsent();
  }


  /**
   * Returns the concrete syntax represented by a SchemaIdentifier
   */
  protected String getScopeIdentifier(ASTScopeIdentifier scopeIdentifier) {
    String n = scopeIdentifier.getClass().getSimpleName().substring("AST".length());

    return n.substring(0, n.length() - "SchemaIdentifier".length());
  }
}
