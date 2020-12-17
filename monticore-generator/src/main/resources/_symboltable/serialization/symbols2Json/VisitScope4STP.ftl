<#-- (c) https://github.com/MontiCore/monticore -->
  if(getJsonPrinter().toString().isEmpty()){
    getJsonPrinter().beginObject();
  } else{
    getJsonPrinter().beginObject(de.monticore.symboltable.serialization.JsonDeSers.SPANNED_SCOPE);
  }
  scopeDeSer.serialize(node, this);
  getJsonPrinter().beginArray(de.monticore.symboltable.serialization.JsonDeSers.SYMBOLS);

