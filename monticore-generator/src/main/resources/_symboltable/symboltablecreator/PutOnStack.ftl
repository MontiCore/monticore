<#-- (c) https://github.com/MontiCore/monticore -->
  Log.errorIfNull(scope);

  if (!scope.isPresentEnclosingScope() && getCurrentScope().isPresent()) {
    scope.setEnclosingScope(getCurrentScope().get());
    getCurrentScope().get().addSubScope(scope);
  } else if (scope.isPresentEnclosingScope() && getCurrentScope().isPresent()) {
    if (scope.getEnclosingScope() != getCurrentScope().get()) {
      Log.warn("0xA1043 The enclosing scope is not the same as the current scope on the stack.");
    }
  }

  if (firstCreatedScope == null) {
    firstCreatedScope = scope;
  }

  scopeStack.addLast(scope);