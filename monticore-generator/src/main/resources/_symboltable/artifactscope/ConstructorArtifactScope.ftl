<#-- (c) https://github.com/MontiCore/monticore -->
  super(true);
  enclosingScope.ifPresent(this::setEnclosingScope);
  setExportingSymbols(true);
  Preconditions.checkNotNull(packageName);
  Preconditions.checkNotNull(imports);

  if (!packageName.isEmpty()) {
    this.packageName = packageName.endsWith(".") ? packageName.substring(0, packageName.length() - 1) : packageName;
  } else {
    // default package
    this.packageName = "";
  }

  this.imports = java.util.List.copyOf(imports);