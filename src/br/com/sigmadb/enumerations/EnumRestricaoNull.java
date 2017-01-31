package br.com.sigmadb.enumerations;

/**
 * Enum que representa um tipo de restri��o de campos nulos para consultas SQL.<br>
 * Valores do Enum: <br>
 * <b>IS NULL</b> <br>
 * <b>IS NOT NULL</b>
 * @author Igor Mois�s
 * @since 21/10/2015
 */
public enum EnumRestricaoNull {

	/**
	 * Representa a restri��o do tipo IS NULL para alguma coluna de uma consulta.
	 * <br>
	 * <table border = '1'>
	 * <th>Descri��o</th> 
	 * <tr><td>is null</td></tr>
	 * </table>
	 * @author Igor Mois�s
	 */
	IS_NULL("is null"),
	
	/**
	 * Representa a restri��o do tipo IS NOT NULL para alguma coluna de uma consulta.
	 * <br>
	 * <table border = '1'>
	 * <th>Descri��o</th> 
	 * <tr><td>is not null</td></tr>
	 * </table>
	 * @author Igor Mois�s
	 */
	IS_NOT_NULL("is not null");
	
	private String descricao;
	
	private EnumRestricaoNull(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
