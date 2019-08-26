<#-- (c) https://github.com/MontiCore/monticore -->
  if (this.enclosingScope.isPresent() && enclosingScope.isPresent()) {
    if (this.enclosingScope.get() == enclosingScope.get()) {
      return;
    }
    Log.warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
  }

  // remove this scope from current (old) enclosing scope, if exists.
  if (this.enclosingScope.isPresent()) {
    this.enclosingScope.get().removeSubScope(this);
  }

  // add this scope to new enclosing scope, if exists.
  if (enclosingScope.isPresent()) {
    enclosingScope.get().addSubScope(this);
  }

  // set new enclosing scope (or empty)
  this.enclosingScope = enclosingScope;