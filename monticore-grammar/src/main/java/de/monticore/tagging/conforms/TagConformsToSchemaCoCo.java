/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.conforms;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._cocos.TagsASTTagUnitCoCo;
import de.monticore.tagging.tagschema.TagSchemaMill;
import de.monticore.tagging.tagschema._symboltable.TagSchemaSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * CoCo for checking if a TagDefinition conforms to a TagSchema
 *
 * @param <T> the StartRule
 */
public abstract class TagConformsToSchemaCoCo<T extends ASTNode> implements TagsASTTagUnitCoCo {

  protected T model;

  public TagConformsToSchemaCoCo(T model) {
    this.model = model;
  }

  @Override
  public void check(ASTTagUnit node) {
    if (node.isEmptyConformsTo()) return;

    // Check for Schema from the "conforms to FQN;"
    for (ASTMCQualifiedName schemaQN : node.getConformsToList()) {
      Optional<TagSchemaSymbol> tagSchemaOpt = TagSchemaMill.globalScope().resolveTagSchema(schemaQN.toString());
      if (tagSchemaOpt.isPresent()) {
        check(node, tagSchemaOpt.get());
      } else {
        Log.error("0x74680: Unable to resolve for TagSchema " + schemaQN);
      }
    }
  }

  protected abstract void check(ASTTagUnit tagUnit, TagSchemaSymbol tagSchema);

  public static class TagElementVisitor implements IVisitor {
    protected final Stack<String> qualifiedNameStack = new Stack<>();
    protected final TagData rootTagData;
    protected final TagConformanceChecker tagConformanceChecker;

    public TagElementVisitor(TagData rootTagData, TagConformanceChecker tagConformanceChecker) {
      this.rootTagData = rootTagData;
      this.tagConformanceChecker = tagConformanceChecker;
    }

    @Override
    public void visit(ISymbol symbol) {
      String identifierName = symbol.getAstNode().getClass().getSimpleName().substring("AST".length());

      List<String> ll = new ArrayList<>(qualifiedNameStack);
      ll.add(symbol.getName());

      List<ASTTag> tags = rootTagData.getTagsQualified(ll);

      for (ASTTag tag : tags) {
        if (!tagConformanceChecker.verifyFor(tag, identifierName)) {
          // Most likely no tagtype was found for this tag
          Log.error("0x74681: Tag " + getPrintableName(tag)  + " (of " + identifierName + ") violates Schema at " + tag.get_SourcePositionStart());
        }
      }

    }

    protected String getPrintableName(ASTTag tag) {
      return StringUtils.abbreviate(StringUtils.normalizeSpace(TagsMill.prettyPrint(tag, false)), 40);
    }

    @Override
    public void visit(IScope scope) {
      if (scope.isPresentName()) {
        qualifiedNameStack.add(scope.getName());
      }
    }

    @Override
    public void endVisit(IScope scope) {
      if (scope.isPresentName()) {
        qualifiedNameStack.pop();
      }
    }
  }

}
