<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("lang")}
TagConformanceChecker tagConformanceChecker = new TagConformanceChecker(tagSchemaSymbol);

TagDataVisitor dataVisitor = new ${lang}TagDataVisitor(tagSchemaSymbol, tagConformanceChecker);
TagsTraverser dataVisitorTraverser = TagsMill.traverser();
dataVisitorTraverser.add4Tags(dataVisitor);

// Build the TagData object
tagUnit.accept(dataVisitorTraverser);

// Check Default Identified

TagElementVisitor tagElementVisitor = new TagElementVisitor(dataVisitor.getTagDataStack().peek(), tagConformanceChecker);
${lang}Traverser modelTraverser = ${lang}Mill.inheritanceTraverser();
modelTraverser.add4IVisitor(tagElementVisitor);

model.getEnclosingScope().accept(modelTraverser);