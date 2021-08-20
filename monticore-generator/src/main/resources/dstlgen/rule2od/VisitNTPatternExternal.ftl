<#-- (c) https://github.com/MontiCore/monticore -->
    @Override
    public void visit(AST${ast.getName()}_Pat node) {
        // create objects
        ASTODObject o_lhs = ODRulesMill.oDObjectBuilder().uncheckedBuild();
        if (isOnLHS()) {
            o_lhs.setName(state.getNameGen().getNameForElement(node));
            List<String> lhs_qType = TypesHelper.createListFromDotSeparatedString(node._getTFElementType().getName());
            ASTReferenceType lhs_t = TypesNodeFactory.createASTSimpleReferenceType(lhs_qType, null);
            o_lhs.setType(lhs_t);

            addObjectToLHS(o_lhs);
        }
        ASTODObject o_rhs = ODRulesMill.oDObjectBuilder().uncheckedBuild();
        if (isOnRHS()) {
            o_rhs.setName(state.getNameGen().getNameForElement(node));
            List<String> rhs_qType = TypesHelper.createListFromDotSeparatedString(node._getTFElementType().getName());
            ASTReferenceType rhs_t = TypesNodeFactory.createASTSimpleReferenceType(rhs_qType, null);
            o_rhs.setType(rhs_t);

            rhs.getODObjects().add(o_rhs);
        }


    }
