${glex.defineGlobalVar("v1",16)}
V1:${glex.getGlobalVar("v1")}
${glex.defineGlobalVar("v1",glex.getGlobalVar("v1")+22)}
V2:${glex.getGlobalVar("v1")}
${glex.changeGlobalVar("v1","Aha")}
V3:${glex.getGlobalVar("v1")}
