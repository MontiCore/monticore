/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.conforms;

import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.*;
import de.monticore.tagging.tags._visitor.TagsHandler;
import de.monticore.tagging.tags._visitor.TagsTraverser;
import de.monticore.tagging.tags._visitor.TagsVisitor2;
import de.monticore.tagging.tagschema._ast.*;
import de.monticore.tagging.tagschema._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Visitor that checks if a tag conforms to the schema
 */
public class TagConformanceChecker implements TagsVisitor2 {
  protected TagsTraverser traverser = TagsMill.traverser();
  protected TagSchemaData tagSchemaData;

  protected Optional<String> scopeIdentifier = Optional.empty();
  protected boolean found = false;

  public TagConformanceChecker(TagSchemaSymbol tagSchemaSymbol) {
    traverser.add4Tags(this);

    this.tagSchemaData = new TagSchemaData(tagSchemaSymbol);
  }

  public boolean verifyFor(ASTTag node, String scopeIdentifier) {
    this.scopeIdentifier = Optional.of(scopeIdentifier);
    this.found = false;
    node.accept(this.traverser);
    this.traverser.clearTraversedElements();
    this.scopeIdentifier = Optional.empty();
    return this.found;
  }

  @Override
  public void visit(ASTSimpleTag node) {
    if (tagSchemaData.getSimpleTagTypes().containsKey(scopeIdentifier.get()) && tagSchemaData.getSimpleTagTypes().get(scopeIdentifier.get()).stream().filter(x -> !x.isPrivate()).anyMatch(x -> x.getName().equals(node.getName()))) {
      this.found = true;
    }
    if (tagSchemaData.getSimpleTagTypes().containsKey(TagSchemaData.WILDCARD) && tagSchemaData.getSimpleTagTypes().get(TagSchemaData.WILDCARD).stream().filter(x -> !x.isPrivate()).anyMatch(x -> x.getName().equals(node.getName()))) {
      this.found = true;
    }
  }

  protected boolean checkValuedTag(ASTValuedTag node, ValuedTagTypeSymbol type) {
    if (type.isPrivate()) return false; // Private may only be referenced by complex tags
    if (!node.getName().equals(type.getName())) return false;
    if (type.getType() == ASTConstantsTagSchema.STRING)
      return true;
    else if (type.getType() == ASTConstantsTagSchema.BOOLEAN) {
      // By definition of {@link Boolean#parseBoolean(String)} anything except the string "true" is false
      return true;
    } else if (type.getType() == ASTConstantsTagSchema.INT) {
      try {
        Integer.parseInt(node.getValue());
        return true;
      } catch (NumberFormatException expected) {
        return false;
      }
    }
    return false;
  }

  @Override
  public void visit(ASTValuedTag node) {
    if (tagSchemaData.getValuedTagTypes().containsKey(scopeIdentifier.get()) && tagSchemaData.getValuedTagTypes().get(scopeIdentifier.get())
            .stream().anyMatch(type -> checkValuedTag(node, type))) {
      this.found = true;
    } else if (tagSchemaData.getValuedTagTypes().containsKey(TagSchemaData.WILDCARD) && tagSchemaData.getValuedTagTypes().get(TagSchemaData.WILDCARD)
            .stream().anyMatch(type -> checkValuedTag(node, type))) {
      this.found = true;
    }
    if (tagSchemaData.getEnumeratedTagTypes().containsKey(scopeIdentifier.get())) {
      List<EnumeratedTagTypeSymbol> enumeratedTagTypes = tagSchemaData.getEnumeratedTagTypes().get(scopeIdentifier.get());
      checkEnumeratedTagType(node, enumeratedTagTypes);
    }
    if (tagSchemaData.getEnumeratedTagTypes().containsKey(TagSchemaData.WILDCARD)) {
      List<EnumeratedTagTypeSymbol> enumeratedTagTypes = tagSchemaData.getEnumeratedTagTypes().get(TagSchemaData.WILDCARD);
      checkEnumeratedTagType(node, enumeratedTagTypes);
    }
  }

  protected void checkEnumeratedTagType(ASTValuedTag node, List<EnumeratedTagTypeSymbol> enumeratedTagTypes) {
    if (enumeratedTagTypes.stream().filter(t -> !t.isPrivate()).filter(t -> t.getName().equals(node.getName())).anyMatch(tt -> tt.getValuesList().contains(node.getValue()))) {
      this.found = true;
    } else {
      List<String> allowedValues = enumeratedTagTypes.stream().filter(t -> t.getName().equals(node.getName())).map(
              EnumeratedTagTypeSymbol::getValuesList).flatMap(Collection::stream).collect(Collectors.toList());
      Log.error("0x74683: Valued tag " + node.getName() + " is not in the set of allowed values: " + allowedValues + ", but " + node.getValue() + " present");
    }
  }

  @Override
  public void visit(ASTComplexTag node) {
    if (tagSchemaData.getComplexTagTypes().containsKey(scopeIdentifier.get())) {
      List<ComplexTagTypeSymbol> tagTypes = tagSchemaData.getComplexTagTypes().get(scopeIdentifier.get());
      checkComplexTagType(node, tagTypes);
    }
    if (tagSchemaData.getComplexTagTypes().containsKey(TagSchemaData.WILDCARD)) {
      List<ComplexTagTypeSymbol> tagTypes = tagSchemaData.getComplexTagTypes().get(TagSchemaData.WILDCARD);
      checkComplexTagType(node, tagTypes);
    }
  }

  protected void checkComplexTagType(ASTComplexTag node, List<ComplexTagTypeSymbol> complexTagTypes) {
    Optional<ComplexTagTypeSymbol> complexTagTypesCandidate = complexTagTypes.stream().filter(t -> !t.isPrivate()).filter(t -> t.getName().equals(node.getName())).findFirst();
    if (complexTagTypesCandidate.isPresent()) {
      checkComplexTag(node, complexTagTypesCandidate.get());
      this.found = true;
    } else {
      Log.error("0x74684: Complex tag " + node.getName() + " is not in the set of available complex tags");
    }
  }

  protected void checkComplexTag(ASTComplexTag node, ComplexTagTypeSymbol schemaType) {
    List<ReferenceSymbol> referenceSymbols = schemaType.getSpannedScope().getLocalReferenceSymbols();
    if (node.getTagList().size() != referenceSymbols.size()) {
      Log.error("0x74685: Complex tag " + node.getName() + " does not have the correct inner tag count");
      return;
    }

    // use the visitor-pattern to check the inner elements
    TagsTraverser tagsTraverser = TagsMill.traverser();
    tagsTraverser.setTagsHandler(new TagsHandler() {
      TagsTraverser traverser;

      @Override
      public TagsTraverser getTraverser() {
        return traverser;
      }

      @Override
      public void setTraverser(TagsTraverser traverser) {
        this.traverser = traverser;
      }

      @Override
      public void handle(ASTSimpleTag node) {
        Log.error("0x74686: Simple tag used within complex tag");
      }

      @Override
      public void handle(ASTValuedTag node) {
        Optional<ReferenceSymbol> reference = referenceSymbols.stream().filter(x -> x.getName().equals(node.getName())).findFirst();
        if (reference.isEmpty()) {
          Log.error("0x74687: Complex tag " + schemaType.getName() + " does not allow inner tag of type " + node.getName());
          return;
        }
        if (reference.get().isPresentReferencedTag()) {
          Log.error("0x74688: Complex tag " + schemaType.getName() + " requires a complex tag instead of simple tag " + node.getName());
          return;
        }
        checkSimpleRefType(reference.get(), node.getValue());
      }

      @Override
      public void handle(ASTComplexTag node) {
        // Check complex tags using the other traverser
      }
    });
    tagsTraverser.traverse(node); // traverse only inner elements

    found = true;
  }

  protected void checkSimpleRefType(ReferenceSymbol referenceSymbol, String value) {
    if (referenceSymbol.getReferenceType() == ASTConstantsTagSchema.STRING) {
      // allow any value
    } else if (referenceSymbol.getReferenceType() == ASTConstantsTagSchema.INT) {
      try {
        Integer.parseInt(value);
      } catch (NumberFormatException ex) {
        Log.error("0x74689: Complex tag requires integer reference");
      }
    } else if (referenceSymbol.getReferenceType() == ASTConstantsTagSchema.BOOLEAN) {
      if (!value.equals("true") && !value.equals("false"))
        Log.error("0x74690: Complex tag requires boolean reference");
    }
  }
}
