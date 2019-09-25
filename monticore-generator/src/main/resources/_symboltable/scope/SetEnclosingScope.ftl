<#-- (c) https://github.com/MontiCore/monticore -->
if (this.enclosingScope != null && enclosingScope != null) {
if (this.enclosingScope == enclosingScope) {
      return;
    }
    Log.warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
  }

  // remove this scope from current (old) enclosing scope, if exists.
if (this.enclosingScope != null) {
this.enclosingScope.removeSubScope(this);
  }

  // add this scope to new enclosing scope, if exists.
if (enclosingScope != null) {
enclosingScope.addSubScope(this);
  }

  // set new enclosing scope (or empty)
  this.enclosingScope = enclosingScope;