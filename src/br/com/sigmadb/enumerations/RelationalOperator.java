package br.com.sigmadb.enumerations;

/**
 * Enum que representa todos os operadores relacionais da estrutura SQL.
 * Valores do Enum:<br>
 * <b>IGUAL ( = )</b><br>
 * <b>DIFERENTE ( <> )</b><br>
 * <b>MENOR_QUE ( < )</b><br>
 * <b>MENOR_IGUAL ( <= )</b><br>
 * <b>MAIOR_QUE ( > )</b><br>
 * <b>MAIOR_IGUAL ( >= )</b><br>
 * @author Igor Moisés
 */
public enum RelationalOperator {
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Igual (=).
	 * Ex. nomeColuna = valor 
	 */
	IGUAL(" = "),
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Diferente (<>).
	 * Ex. nomeColuna <> valor 
	 */
	DIFERENTE(" <> "),
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Menor (<).
	 * Ex. nomeColuna < valor 
	 */
	MENOR_QUE(" < "),
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Menor igual(<=).
	 * Ex. nomeColuna <= valor 
	 */
	MENOR_IGUAL(" <= "),
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Maior (>).
	 * Ex. nomeColuna > valor 
	 */
	MAIOR_QUE(" > "),
	
	/**
	 * Indica que na restrição da consulta será aplicada o tipo de operador relacional Maior igual (>=).
	 * Ex. nomeColuna >= valor 
	 */
	MAIOR_IGUAL(" >= ");
	
	private RelationalOperator(String value) {
		this.value = value;
	}
	
	private String value;
	
	public String getValue() {
		return value;
	}		
}
