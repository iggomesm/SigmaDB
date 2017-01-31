package br.com.sigmadb.enumerations;

public enum EnumOperacaoBD {	
	INSERT("I"), UPDATE("U"), DELETE("D");
	
	private String value;
	
	private EnumOperacaoBD(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
