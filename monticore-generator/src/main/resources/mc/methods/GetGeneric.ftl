<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "astType", "errorCode")}
for(${astType.printType()}  attribute : this.${attribute.getName()} ) {
  if(attribute.getClass() != ${astType.printType()}.class) {
    Log.error(${errorCode} + " Generic attribute " + "${attribute.getName()}" + " is not of type " + "${astType.printType()}");
  }
}
return this.${attribute.getName()};
