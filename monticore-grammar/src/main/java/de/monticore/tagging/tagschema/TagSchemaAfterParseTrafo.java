/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.tagschema;

import de.monticore.tagging.tagschema._ast.*;
import de.monticore.tagging.tagschema._visitor.TagSchemaTraverser;
import de.monticore.tagging.tagschema._visitor.TagSchemaVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Convert
// tagtype A for P1; tagtype A for P2;
// to a single
// tagtype A for P1, P2;
public class TagSchemaAfterParseTrafo {
  protected TagSchemaTraverser traverser;

  // TagType => Scope
  protected Map<String, ASTTagScope> tagScopeMap = new HashMap<>();
  // Ensure all tagtypes of the same name are of the same kind (simple, valued, enumerated, ...)
  protected Map<String, Class<?>> tagKindMap = new HashMap<>();

  protected Set<ASTTagType> markedForDeletion = new HashSet<>();

  public TagSchemaAfterParseTrafo() {
    this.traverser = TagSchemaMill.traverser();

    this.traverser.add4TagSchema(new TagSchemaVisitor2() {

      @Override
      public void visit(ASTTagSchema node) {
        markedForDeletion.clear();
      }

      void ensureUniqueKind(ASTTagType node){
        if (tagKindMap.computeIfAbsent(node.getName(), s -> node.getClass()) != node.getClass()) {
          Log.error("0x74691: Tagtype " + node.getName() + " is re-defined with a different kind at " + node.get_SourcePositionStart());
        }
      }

      @Override
      public void visit(ASTSimpleTagType node) {
        ensureUniqueKind(node);
        if (tagScopeMap.containsKey(node.getName())) {
          addToTagScope(tagScopeMap.get(node.getName()), node.getTagScope());
          markedForDeletion.add(node);
        } else {
          tagScopeMap.put(node.getName(), node.getTagScope());
        }
      }

      @Override
      public void visit(ASTValuedTagType node) {
        ensureUniqueKind(node);
        if (tagScopeMap.containsKey(node.getName())) {
          addToTagScope(tagScopeMap.get(node.getName()), node.getTagScope());
          markedForDeletion.add(node);
        } else {
          tagScopeMap.put(node.getName(), node.getTagScope());
        }
      }

      @Override
      public void visit(ASTEnumeratedTagType node) {
        ensureUniqueKind(node);
        if (tagScopeMap.containsKey(node.getName())) {
          addToTagScope(tagScopeMap.get(node.getName()), node.getTagScope());
          markedForDeletion.add(node);
        } else {
          tagScopeMap.put(node.getName(), node.getTagScope());
        }
      }

      @Override
      public void visit(ASTComplexTagType node) {
        ensureUniqueKind(node);
        if (tagScopeMap.containsKey(node.getName())) {
          addToTagScope(tagScopeMap.get(node.getName()), node.getTagScope());
          markedForDeletion.add(node);
        } else {
          tagScopeMap.put(node.getName(), node.getTagScope());
        }
      }

      @Override
      public void endVisit(ASTTagSchema node) {
        node.removeAllTagTypes(markedForDeletion);
      }
    });
  }

  protected void addToTagScope(ASTTagScope target, ASTTagScope source) {
    if (target.isWildcard()) {
      return;
    }
    if (source.isWildcard()) {
      target.setWildcard(true);
      target.clearScopeIdentifiers();
      return;
    }
    target.addAllScopeIdentifiers(source.getScopeIdentifierList());
  }

  protected TagSchemaTraverser getTraverser() {
    return this.traverser;
  }

  public void transform(ASTTagSchema tagSchema) {
    this.getTraverser().clearTraversedElements();
    tagSchema.accept(this.getTraverser());
  }

}
