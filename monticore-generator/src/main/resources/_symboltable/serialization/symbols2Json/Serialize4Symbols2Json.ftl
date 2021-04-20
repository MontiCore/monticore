<#-- (c) https://github.com/MontiCore/monticore -->
this.getJsonPrinter().clearBuffer();
toSerialize.accept(this.getTraverser());
String serialized = this.getJsonPrinter().getContent();
return serialized;
