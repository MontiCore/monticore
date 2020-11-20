<#-- (c) https://github.com/MontiCore/monticore -->
  if (getCurrentScope().isPresent()) {
    node.setEnclosingScope(getCurrentScope().get());
  }
  else {
    Log.error("Could not set enclosing scope of ASTNode \"" + node
              + "\", because no scope is set yet!");
  }