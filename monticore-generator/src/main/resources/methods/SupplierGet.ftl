<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
var __value = this.${attribute.getName()}.get();
if (__value == null) {
  throw new IllegalStateException("Value of '${attribute.getName()}' is not available yet.");
}
return __value;
