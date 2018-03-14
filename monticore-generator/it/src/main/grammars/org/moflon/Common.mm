/* (c)  https://github.com/MontiCore/monticore */

package org.moflon;

grammar Common {
		
	options {
		lexer lookahead = 7
		parser lookahead = 4
		noident
	}
	
	ident NUMBER =
        ('0'..'9')+;
        
    ident IDENT
		options {testLiterals=true;} = //check Literals first
		('a'..'z' | 'A'..'Z' | '_') 
		('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
		
	/** ASTQualifiedName represents a group of Names seperated by a dot
		@attribute Names List of Names
	*/
	QualifiedName =
		Names:IDENT (options {greedy=true;}: "." Names:IDENT)*;
    
    
	/** ASTModifier represents a Modifier (needs semantic check)
	*/
	Modifier =
		(	Public:["public"]		| Public:[PUBLIC:"+"]		//type modifier
		  | Private:["private"]		| Private:[PRIVATE:"-"]
          | Ordered:["ordered"]
		  | Unique:["unique"]
		  | ReadOnly:["readonly"]
          | Navigable:["navigable"]
		  | NonNavigable: ["non-navigable" ]
		  | DerivedUnion:["derivedunion"] 
		  | Composite:["composite"]
		  | Abstract:["abstract"]
		);

	/** ASTMultiplicity represents Multiplicities in JMOF
		@attribute Many True if "*" is set as Multiplicity
		@attribute LowerBound Minimum number of associated Classes/Ojects
		@attribute UpperBound Maximum number of associated Classes/Ojects
		@attribute NoUpperLimit True if no upper bound exists
	*/
	Multiplicity =
		"["
		( Many:["*"]
		  | (LowerBound:NUMBER {a.setUpperBound(a.getLowerBound());})
		  | (LowerBound:NUMBER ".." (UpperBound:NUMBER | NoUpperLimit:["*"]))
		)
		"]";
	
	/** ParameterDirection represents the direction of an operation parameter 
		@attribute In true if direction is "in"
		@attribute Out true if direction is "out"
		@attribute InOut true if direction is "inout"
	*/
	ParameterDirection =
		( In:["in"]
		  | Out:["out"]
		  |InOut:["inout"]
		);
	
	/** SubsetRedefines represents the definition of a subsets or redefines
		relation between properties
		@attribute Subsets: true if this is a subset relation
		@attribute Redefines: true if this is a redefinition
		@attribute SubsettedProperty: the subsetted properties 
		@attribute RedefinedProperty: the redefined properties
	*/
	SubsetRedefines = 
	  (Subsets:["subsets"] SubsettedProperty:QualifiedName (options {greedy=true;}: "," SubsettedProperty:QualifiedName)*)
	  (("redefines")=> Redefines:["redefines"] RedefinedProperty:QualifiedName (options {greedy=true;}: "," RedefinedProperty:QualifiedName)*)?
	  |
	  (Redefines:["redefines"] RedefinedProperty:QualifiedName (options {greedy=true;}: "," RedefinedProperty:QualifiedName)*)
	  ;
	  
	/** ASTTag represents a MOF Tag
		@attribute Values List of Values of this Stereotype
	*/
	Tag =
		"<<" TagValues:TagValue ("," TagValues:TagValue)* ">>";

	/** ASTStereoValue represents a Name-Value-Pair of a MOFTag
		@attribute Name Name of the Tag
		@attribute Value Value of the Tag
	*/
	TagValue =
		Name:IDENT ("=" Value:STRING)?;

	  

}
