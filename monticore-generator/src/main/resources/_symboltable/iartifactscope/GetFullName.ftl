<#-- (c) https://github.com/MontiCore/monticore -->
  String packageName = this.getPackageName();
  if(this.isPresentName()){
    if(packageName.isEmpty()){
      return this.getName();
    }else{
      return packageName + "." + this.getName();
    }
  }else{
   return packageName;
  }