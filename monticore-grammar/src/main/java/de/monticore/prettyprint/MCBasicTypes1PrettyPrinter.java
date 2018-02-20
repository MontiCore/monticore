/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.mcbasictypes1._ast.ASTBooleanType;
import de.monticore.mcbasictypes1._ast.ASTByteType;
import de.monticore.mcbasictypes1._ast.ASTCharType;
import de.monticore.mcbasictypes1._ast.ASTDoubleType;
import de.monticore.mcbasictypes1._ast.ASTFloatType;
import de.monticore.mcbasictypes1._ast.ASTImportStatement;
import de.monticore.mcbasictypes1._ast.ASTIntType;
import de.monticore.mcbasictypes1._ast.ASTLongType;
import de.monticore.mcbasictypes1._ast.ASTMCBasicTypes1Node;
import de.monticore.mcbasictypes1._ast.ASTNameAsReferenceType;
import de.monticore.mcbasictypes1._ast.ASTQualifiedName;
import de.monticore.mcbasictypes1._ast.ASTShortType;
import de.monticore.mcbasictypes1._visitor.MCBasicTypes1Visitor;

public class MCBasicTypes1PrettyPrinter implements MCBasicTypes1Visitor {
	private IndentPrinter printer = null;

	public MCBasicTypes1PrettyPrinter(IndentPrinter printer) {
		this.printer = printer;
	}

	@Override
	public void handle(ASTQualifiedName node) {
		int count = 1;
		for (String s : node.getPartList()) {
			getPrinter().print(s);
			if (node.getPartList().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}
	}

	@Override
	public void handle(ASTImportStatement node) {
		int count = 1;
		getPrinter().print("import ");
		for (String s : node.getQualifiedName().getPartList()) {
			getPrinter().print(s);
			if (node.getQualifiedName().getPartList().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}
		if (node.isStar()) {
			getPrinter().print(".*");
		}
		getPrinter().print(";");
	}

	@Override
	public void handle(ASTBooleanType node) {
		getPrinter().print("boolean");
	}

	@Override
	public void handle(ASTByteType node) {
		getPrinter().print("byte");
	}

	@Override
	public void handle(ASTCharType node) {
		getPrinter().print("char");
	}

	@Override
	public void handle(ASTShortType node) {
		getPrinter().print("short");
	}

	@Override
	public void handle(ASTIntType node) {
		getPrinter().print("int");
	}

	@Override
	public void handle(ASTFloatType node) {
		getPrinter().print("float");
	}

	@Override
	public void handle(ASTLongType node) {
		getPrinter().print("long");
	}

	@Override
	public void handle(ASTDoubleType node) {
		getPrinter().print("double");
	}

	@Override
	public void handle(ASTNameAsReferenceType node) {
		int count = 1;
		for (String s : node.getNameList()) {
			getPrinter().print(s);
			if (node.getNameList().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}

	}

	public IndentPrinter getPrinter() {
		return this.printer;
	}

	public String prettyprint(ASTMCBasicTypes1Node node) {
		getPrinter().clearBuffer();
		node.accept(getRealThis());
		return getPrinter().getContent();
	}

}
