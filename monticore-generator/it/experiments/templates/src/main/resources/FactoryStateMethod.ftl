<#-- (c) https://github.com/MontiCore/monticore -->
    public static ${ast.getName()?cap_first}State get${ast.getName()?cap_first}State(){
        return new ${ast.getName()?cap_first}State();
    }