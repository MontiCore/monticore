<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolClassName")}
    ${symbolClassName} symbol = new ${symbolClassName}(name);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setFullName(this.fullName);
    return symbol;
