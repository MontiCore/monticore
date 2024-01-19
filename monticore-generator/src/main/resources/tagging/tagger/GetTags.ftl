<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname", "hasName")}
<#-- hasName: true for symbols or symbol-like productions -->
<#-- isSymbol: true for symbols -->
List< ASTTag> tags = new ArrayList<>();

List< ASTContext> contexts;
for (ASTTagUnit astTagUnit : astTagUnits) {
    List< String> scopeStack = getScopeDifferences(model.getEnclosingScope(), getArtifactScope(model.getEnclosingScope()));
    if (scopeStack.isEmpty()) {
        <#if hasName>findTargetsBy(astTagUnit, model.getName()).forEach(t -> tags.addAll(t.getTagList()));</#if>
        findTargetsBy(astTagUnit, model).forEach(t -> tags.addAll(t.getTagList()));
    } else {
        // within/context must always be on scopes, so we can use name matching instead of pattern matching
        <#if hasName>scopeStack.add(model.getName());</#if>

        contexts = findContextBy(astTagUnit, scopeStack.get(0)).collect(Collectors.toList());

        <#if hasName>
            String joinedNames = Joiners.DOT.join(scopeStack);
            findTargetsBy(astTagUnit, joinedNames).forEach(t -> tags.addAll(t.getTagList()));
        </#if>

        scopeStack.remove(0);

        while (scopeStack.size() > 1) {
            List< ASTContext> tempContexts = contexts;
            contexts = new ArrayList<>();
            <#if hasName>joinedNames = Joiners.DOT.join(scopeStack);</#if>
            String name = scopeStack.remove(0);

            for (ASTContext context : tempContexts) {
                findContextBy(context, name).forEach(contexts::add);
                <#if hasName>findTargetsBy(context, joinedNames).forEach(t -> tags.addAll(t.getTagList()));</#if>
            }
        }
        for (ASTContext context : contexts) {
            <#if hasName>
                findTargetsBy(context, scopeStack.get(0)).forEach(t -> tags.addAll(t.getTagList()));
            <#else>
                findTargetsBy(context, model).forEach(t -> tags.addAll(t.getTagList()));
            </#if>
        }

    }
}
return tags;
