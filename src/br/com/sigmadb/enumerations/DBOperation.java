package br.com.sigmadb.enumerations;

public enum DBOperation {	
	INSERT("I"), UPDATE("U"), DELETE("D");
	
	private String value;
	
	private DBOperation(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
