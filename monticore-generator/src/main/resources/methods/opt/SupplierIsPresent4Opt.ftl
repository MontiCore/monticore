<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute")}
var optional =  this.${attribute.getName()}.get();
if (optional == null){
    throw new IllegalStateException("supplier for ${attribute.getName()} is null");
}
return optional.isPresent();
