package br.com.sigmadb.enumerations;

/**
 * Enum que representa o operador logico na cláusula where.<br>
 * Valores do Enum:<br>
 * <b>AND</b><br>
 * <b>OR</b><br>
 * @author Igor Moisés
 */
public enum OperadorLogico {
	/**
	 * Indica que na consulta será inserido um AND na operação lógica.
	 */
	AND(" AND "),
	
	/**
	 * Indica que na consulta será inserido um OR na operação lógica.
	 */
	OR(" OR ");
	
	private OperadorLogico(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return value;
	}
}
