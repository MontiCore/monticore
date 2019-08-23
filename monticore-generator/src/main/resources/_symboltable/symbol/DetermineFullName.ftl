<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeInterface", "artifactScope", "globalScope")}
  if (enclosingScope == null) {
    // There should not be a symbol that is not defined in any scope. This case should only
    // occur while the symbol is built (by the symbol table creator). So, here the full name
    // should not be cached yet.
    return name;
  }

  final Deque<String> nameParts = new ArrayDeque<>();
    nameParts.addFirst(name);

    Optional<? extends ${scopeInterface}> optCurrentScope = Optional.of(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final IAutomataScope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().get().getFullName());
        break;
      }

      if (!(currentScope instanceof ${globalScope})) {
        if (currentScope instanceof ${artifactScope}) {
          // We have reached the artifact scope. Get the package name from the
          // symbol itself, since it might be set manually.
          if (!getPackageName().isEmpty()) {
            nameParts.addFirst(getPackageName());
          }
        } else {
          if (currentScope.getName().isPresent()) {
            nameParts.addFirst(currentScope.getName().get());
          }
          // ...else stop? If one of the enclosing scopes is unnamed,
          //         the full name is same as the simple name.
        }
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }

    return de.se_rwth.commons.Names.getQualifiedName(nameParts);