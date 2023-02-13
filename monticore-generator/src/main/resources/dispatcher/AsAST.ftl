<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
<#assign service = glex.getGlobalVar("service")>

reset();
node.accept(this);
if(opt${name}.isEmpty()) {
  Log.error("0x54987${service.getGeneratedErrorCode(name)} Cannot cast node to type ${name}.");
}
return opt${name}.get();