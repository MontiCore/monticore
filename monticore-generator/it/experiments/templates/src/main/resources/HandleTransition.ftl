${tc.signature("modelName","input","toState")}
    public void handle${input?cap_first}(${modelName?cap_first} model){
        model.setState(new ${toState}State());
    }