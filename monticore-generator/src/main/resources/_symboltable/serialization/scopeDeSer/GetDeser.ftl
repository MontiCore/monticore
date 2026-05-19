<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mill", "debugCode", "scopeDeserName")}
de.monticore.symboltable.serialization.ISymbolDeSer deSer = ${mill}.globalScope().getSymbolDeSer(kind);

String previousKind;
while (deSer == null && this.symbolHierarchiesJsonObjectOpt.isPresent()) {
  // Walk the symbol-hierarchy up until we can find a registered deSer
  previousKind = kind;
  if (!this.symbolHierarchiesJsonObjectOpt.get().hasStringMember(kind)) {
    Log.debug(
      "0xA1236x${debugCode} The artifact scope does not define a super symbol for symbol kind `" + kind + "`.",
      ${scopeDeserName}.class.getName()
    );
    break;
  }
  kind = this.symbolHierarchiesJsonObjectOpt.get().getStringMember(kind);
  Log.debug(
    "0xA1235x${debugCode} No DeSer found to deserialize symbol of kind `" + previousKind
    + "`. Using the super symbol kind `" + kind + "` instead.",
    ${scopeDeserName}.class.getName()
  );
  // in case we find a deser for that type -> exit the loop
  deSer = ${mill}.globalScope().getSymbolDeSer(kind);
  // otherwise: continue to walk up
}

return deSer;
