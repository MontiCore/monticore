/* (c)  https://github.com/MontiCore/monticore */

package org.moflon;

grammar JMOF extends org.moflon.Common {

	options {
		lexer lookahead = 4
		parser lookahead = 2
		noident
		compilationunit PackageDefinition
	}
	
	external BlockStatement;
	external Value;
	
	/** ASTPackageableElement represents all elements that may be nested
		in a package
	*/
	interface PackageableElement;
	
	/** ASTType is the common interface for all elements that define 
		a type (e.g. classes, enumerations, primitives)
	*/
	interface Type;

	/** ASTPackageDefinition represents a single MOF Package
		@attribute Name the full qualified name of this package
		@attribute NestedElements all PackageableElements that are defined inside this package (classes, associations, ...)
		@attribute Superpackages all Packages merged into this Package
		@attribute Modifier modifier of the specified package
	*/
	PackageDefinition = 
	(Modifier)? (Tag?) "package" Name:QualifiedName ("extends" Superpackages:QualifiedName 
					("," Superpackages:QualifiedName)*)? ";"
	(options {greedy=true;}:(Modifier? "import" )=> ImportStatement)*
	(NestedElements:PackageableElement)*;

	/** ASTMOFClass represents a MOF Class
		@attribute Name the name of this class
		@attribute Superclasses List of Superclasses of this Class
	*/
	MOFClass implements ((Modifier)* (Tag?) "class")=> PackageableElement, ((Modifier)* (Tag?) "class")=> Type =
	(Modifier)* (Tag?) "class" Name:IDENT ("extends" Superclasses:QualifiedName 
					("," Superclasses:QualifiedName)*)?
	"{"
         (options {greedy=true;}: ((Modifier)? "import")=> ImportStatement)*
         ( (ImplementationLanguage (Modifier)* (Tag?) QualifiedName (Multiplicity)? IDENT "(" )=>MOFMethod
            |(MOFProperty))*
	"}";
	
	/** ASTMOFDataType represents a MOF DataType
		@attribute Name the name of this datatype
		@attribute Supertypes List of DataTypes extended by this datatype
	*/
	MOFDataType implements ((Modifier)* (Tag?) "datatype")=> PackageableElement, ((Modifier)* (Tag?) "datatype")=> Type =
	(Modifier)* (Tag?) "datatype" Name:IDENT ("extends" Supertypes:QualifiedName 
					("," Superclasses:QualifiedName)*)?
	"{"
         ( options {greedy=true;}:((Modifier)? "import")=> ImportStatement)*
         ( (ImplementationLanguage (Modifier)* QualifiedName (Multiplicity)? IDENT "(" )=>MOFMethod
            |(MOFProperty))*
	"}";
	
	/**ASTMOFEnum represents a MOF enumeration
	 @attribute Literal the ownedLiterals of this enumeration 
	*/
	MOFEnum implements ((Modifier)* (Tag?) "enum")=> PackageableElement, ((Modifier)* (Tag?) "enum")=> Type =
	(Modifier)? (Tag?) "enum" Name:IDENT 
	"{"
		(Literal:MOFEnumLiteral ("," Literal:MOFEnumLiteral)* (";")?)?
	"}";
	
	/**ASTMOFEnumLiteral represents a MOF enumeration literal **/
	MOFEnumLiteral =
		(Modifier)? Name:IDENT;
	
	/** ASTMOFProperty represents a MOF property
		@attribute Name the name of this property
		@attribute PropertyType the type of this property
	*/
	MOFProperty = 
	// (Modifier)* (Tag?) PropertyType:QualifiedName (Multiplicity)? Name:IDENT (options {greedy=true;}: SubsetRedefines)? ("=" DefaultValue:STRING)? Intrinsic:";"
	// |
	ImplementationLanguage? (Modifier)* (Tag?) PropertyType:QualifiedName (Multiplicity)? Name:IDENT ( ("subsets"|"redefines")=> SubsetRedefines)?  
	( ("="|";")=> ("=" DefaultValue:STRING)? Intrinsic:";")?  ({a.getImplementationLanguage()!=null}? BlockStatement )? 	 
	;
	
	/** ASTMOFMethod represents a method
		@attribute Name the name of this method
		@attribute ReturnType the return type of this method
	*/
	MOFMethod = 
	ImplementationLanguage
	(Modifier)* (Tag?) ReturnType:QualifiedName (Multiplicity)? Name:IDENT "(" (parameters: MOFParameter
		(options {greedy=true;}: "," parameters:MOFParameter)*)? 
		")"
	      ((";")=>Abstract:";"|BlockStatement)			
		;
	
	/** ASTMOFParamater represents a parameter of method
		@attribute Name the name of this parameter
		@attribute ParameterType the type of this parameter
	*/
	MOFParameter = 
	(Modifier)? (ParameterDirection)? (Tag?) ParameterType:QualifiedName (Multiplicity)? Name:IDENT;

	/** ASTMOFPrimtive represents a primitve type
		@attribute Name the name of this primitve
	*/
	MOFPrimitive implements ((Modifier)* (Tag?) "primitive")=> PackageableElement, ((Modifier)* (Tag?) "primitive")=> Type =
	(Modifier)* (Tag?) "primitive" Name:IDENT "{" "}";
	
	/** ASTMOFAssociation represents a MOF Association
		@attribute Name the name of this association
	*/
	MOFAssociation implements ((Modifier)* (Tag?) "association")=> PackageableElement =
	(Modifier)? (Tag?) "association" Name:IDENT
	"{"
		(options{greedy=true;}: ((Modifier)? "import")=> ImportStatement)*
		MOFProperty
		MOFProperty
	"}";
	
	/** ASTImportStatement represents the import of a different element
		@attribute Name the name of the imported element
		@attribute PackageImport true iff this is a package import, i.e. it ends on ".*"
	*/
	ImportStatement = 
	Modifier? "import" Name:QualifiedName (PackageImport:[".*"])? ";";
	
	ImplementationLanguage = 
	"@"(OCL:"OCL"|Java:"Java")
	;
		

}
