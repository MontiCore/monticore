${tc.signature("states")}
public class ${glex.getGlobalVar("modelName")?cap_first}Factory {

    ${tc.include("FactoryStateMethod.ftl",states)}

}