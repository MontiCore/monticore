<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname")}
List< ASTTag> tags = new ArrayList<>();

List< String> scopeStack = getScopeDifferences(symbol.getEnclosingScope(), getArtifactScope(symbol.getEnclosingScope()));


List< ASTContext> contexts;
if (scopeStack.isEmpty()) {
    findTargetsBy(astTagUnit, symbol.getName()).forEach(t -> tags.addAll(t.getTagList()));
} else {
    // within/context must always be on scopes, so we can use name matching instead of pattern matching
    scopeStack.add(symbol.getName());

    contexts = findContextBy(astTagUnit, scopeStack.get(0)).collect(Collectors.toList());

    String joinedNames = Joiners.DOT.join(scopeStack);
    findTargetsBy(astTagUnit, joinedNames).forEach(t -> tags.addAll(t.getTagList()));

    scopeStack.remove(0);

    while (scopeStack.size() > 1) {
        List< ASTContext> tempContexts = contexts;
        contexts = new ArrayList<>();
        joinedNames = Joiners.DOT.join(scopeStack);
        String name = scopeStack.remove(0);

        for (ASTContext context : tempContexts) {
            findContextBy(context, name).forEach(contexts::add);
            findTargetsBy(context, joinedNames).forEach(t -> tags.addAll(t.getTagList()));
        }
    }
    for (ASTContext context : contexts) {
        findTargetsBy(context, scopeStack.get(0)).forEach(t -> tags.addAll(t.getTagList()));
    }

}
return tags;
