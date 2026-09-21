<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
var value = this.${attribute.getName()}.get();
if (value == null) {
  Log.info("Value of '${attribute.getName()}' is not available yet.", "get${attribute.getName()}");
}
return value;
