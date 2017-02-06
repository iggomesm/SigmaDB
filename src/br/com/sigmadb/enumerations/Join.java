package br.com.sigmadb.enumerations;

/**
 * Enum que representa os tipos de Join da SigmaDB, que poderão ser aplicados para consutas.<br>
 * Valores do Enum: <br>
 * <b>INNER_JOIN</b><br>
 * <b>LEFT_JOIN</b><br>
 * <b>RIGHT_JOIN</b><br>
 * @author Igor Moisés
 */
public enum Join {
	/**
	 * Indica que na consulta será inserido um inner join com alguma tabela.
	 */
	INNER_JOIN(" INNER JOIN "),
	
	/**
	 * Indica que na consulta será inserido um left join com alguma tabela.
	 */
	LEFT_JOIN(" LEFT JOIN "),
	
	/**
	 * Indica que na consulta será inserido um right join com alguma tabela.
	 */
	RIGHT_JOIN(" RIGTH JOIN ");
	
	private Join(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}
}
