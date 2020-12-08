<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
  printer.beginObject(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE);
  scopeDeSer.serialize(node, this);
  printer.beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

