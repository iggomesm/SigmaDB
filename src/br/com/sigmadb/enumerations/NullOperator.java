package br.com.sigmadb.enumerations;

/**
 * Enum que representa um tipo de restriçãoo de campos nulos para consultas SQL.<br>
 * Valores do Enum: <br>
 * <b>IS NULL</b> <br>
 * <b>IS NOT NULL</b>
 * @author Igor Moisés
 */
public enum NullOperator {

	/**
	 * Representa a restrição do tipo IS NULL.
	 * <br>
	 * <table border = '1'>
	 * <th>Descrição</th> 
	 * <tr><td>is null</td></tr>
	 * </table>
	 * @author Igor Moisés
	 */
	IS_NULL("is null"),
	
	/**
	 * Representa a restrição do tipo IS NOT NULL.
	 * <br>
	 * <table border = '1'>
	 * <th>Descriçãoo</th> 
	 * <tr><td>is not null</td></tr>
	 * </table>
	 * @author Igor Moisés
	 */
	IS_NOT_NULL("is not null");
	
	private String descricao;
	
	private NullOperator(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
