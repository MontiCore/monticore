/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.conforms;

import de.monticore.tagging.tags._ast.*;
import de.monticore.tagging.tags._visitor.TagsTraverser;
import de.monticore.tagging.tags._visitor.TagsVisitor2;
import de.monticore.tagging.tagschema._symboltable.TagSchemaSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Orchestration of visitors that build a {@link TagData} data-structure
 * Implement me with the concrete traverser and concrete pattern matching visitors
 */
public abstract class TagDataVisitor implements TagsVisitor2 {
  protected Stack<TagData> tagDataStack;

  // the given tags for when traversing the element identifier
  protected List<ASTTag> tagsToAdd;

  protected final TagSchemaSymbol tagSchemaSymbol;
  protected final TagConformanceChecker tagConformanceChecker;

  // Traverser working on de.monticore.tagging.tags._ast.ASTModelElementIdentifier for tag-TargetElements
  protected abstract TagsTraverser getIdentifierTraverser();

  // Traverser working on de.monticore.tagging.tags._ast.ASTModelElementIdentifier for within-contexts
  protected abstract TagsTraverser getContextWithinTraverser();

  protected TagDataVisitor(TagSchemaSymbol tagSchemaSymbol, TagConformanceChecker tagConformanceChecker) {
    this.tagSchemaSymbol = tagSchemaSymbol;
    this.tagConformanceChecker = tagConformanceChecker;
    getIdentifierTraverser().add4Tags(new TagsVisitor2() {
      @Override
      public void visit(ASTDefaultIdent astDefaultIdent) {
        TagData scope = tagDataStack.peek();
        List<ASTTag> tags = scope.getTagsQualified(astDefaultIdent.getMCQualifiedName().getPartsList());
        tags.addAll(tagsToAdd);
      }
    });


    getContextWithinTraverser().add4Tags(new TagsVisitor2() {
      @Override
      public void visit(ASTDefaultIdent astDefaultIdent) {
        TagData scope = tagDataStack.peek();
        TagData newScope = scope.getFromIterator(astDefaultIdent.getMCQualifiedName().getPartsList().iterator());
        tagDataStack.push(newScope);
      }
      // Discuss for next iteration: Are Pattern Matching withins desired?
    });
  }

  public Stack<TagData> getTagDataStack() {
    return tagDataStack;
  }


  @Override
  public void visit(ASTTagUnit node) {
    this.tagDataStack = new Stack<>();
    TagData rootElem = new TagData();
    this.tagDataStack.push(rootElem);
  }

  @Override
  public void visit(final ASTTargetElement node) {
    // "tag" (ModelElementIdentifier || ",")+ "with" (Tag || ",")+ ";";
    tagsToAdd = node.getTagList();
    node.getModelElementIdentifierList().forEach(id -> id.accept(getIdentifierTraverser()));
    tagsToAdd = Collections.emptyList(); // invalidate tagsToAdd by means of an empty list
  }

  @Override
  public void visit(ASTContext node) {
    int prevSize = this.tagDataStack.size();
    node.getModelElementIdentifier().accept(getContextWithinTraverser());
    if (prevSize + 1 != this.tagDataStack.size()) {
      Log.error("0x74682: Invalid grow of ASTContext node to " + tagDataStack.size() + " from " + prevSize);
      throw new IllegalStateException("0x74682: Invalid grow of ASTContext node to " + tagDataStack.size() + " from " + prevSize);
    }
  }

  @Override
  public void endVisit(ASTContext node) {
    this.tagDataStack.pop();
  }
}
