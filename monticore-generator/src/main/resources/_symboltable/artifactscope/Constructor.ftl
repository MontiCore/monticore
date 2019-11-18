<#-- (c) https://github.com/MontiCore/monticore -->
  super(true);
  if (enclosingScope.isPresent()) {
    setEnclosingScope(enclosingScope.get());
  }
  setExportingSymbols(true);
  Log.errorIfNull(packageName);
  Log.errorIfNull(imports);

  if (!packageName.isEmpty()) {
    this.packageName = packageName.endsWith(".") ? packageName.substring(0, packageName.length() - 1) : packageName;
  } else {
    // default package
    this.packageName = "";
  }

  this.imports = Collections.unmodifiableList(new ArrayList<>(imports));