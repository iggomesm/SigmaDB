package br.com.sigmadb.enumerations;

import java.util.List;

import br.com.sigmadb.util.interfaces.DBOperation;
import br.com.sigmadb.util.interfaces.Filter;

public enum TypeOperation implements DBOperation{	
	INSERT("I"), UPDATE("U"), DELETE("D");
	
	private String value;
	
	private TypeOperation(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Filter> getFilters() {
		return null;
	}

	public TypeOperation getTypeOperation() {
		return this;
	}
}
