<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname", "hasName")}
<#-- hasName: true for symbols or symbol-like productions -->
<#-- isSymbol: true for symbols -->

List< String> scopeStack = getScopeDifferences(model.getEnclosingScope(), getArtifactScope(model.getEnclosingScope()));

<#if !hasName>
TravChecker${prodname} travChecker = new TravChecker${prodname}();
</#if>

List< ASTContext> contexts;
if (scopeStack.isEmpty()) {
    if( findTargetsBy(astTagUnit, model<#if hasName>.getName()<#else>, travChecker</#if>).map(x -> x.getTagList().remove(astTag)).filter(x -> x).findFirst()
    .orElse(false)) {
        return true;
    }
} else {
    // within/context must always be on scopes, so we can use name matching instead of pattern matching
    <#if hasName>scopeStack.add(model.getName());</#if>

    contexts = findContextBy(astTagUnit, scopeStack.get(0)).collect(Collectors.toList());

    <#if hasName>
        String joinedNames = Joiners.DOT.join(scopeStack);
        if(findTargetsBy(astTagUnit, joinedNames).map(x -> x.getTagList().remove(astTag)).filter(x -> x).findFirst()
            .orElse(false)) {
            return true;
        }
    </#if>

    scopeStack.remove(0);

    while (scopeStack.size() > 1) {
        List< ASTContext> tempContexts = contexts;
        contexts = new ArrayList<>();
        <#if hasName>joinedNames = Joiners.DOT.join(scopeStack);</#if>
        String name = scopeStack.remove(0);

        for (ASTContext context : tempContexts) {
            findContextBy(context, name).forEach(contexts::add);
            <#if hasName>
                if(findTargetsBy(context, joinedNames).map(x -> x.getTagList().remove(astTag)).filter(x -> x).findFirst()
                .orElse(false)) {
                    return true;
                }
            </#if>
        }
    }
    for (ASTContext context : contexts) {
        if (findTargetsBy(context, <#if hasName>scopeStack.get(0)<#else>model, travChecker</#if>).map(x -> x.getTagList().remove(astTag)).filter(x -> x).findFirst()
        .orElse(false)) {
            return true;
        }
    }

}
return false;
