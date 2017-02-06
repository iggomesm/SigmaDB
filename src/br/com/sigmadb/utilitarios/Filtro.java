package br.com.sigmadb.utilitarios;

public interface Filtro<E> {

	/**
	 * Seta o nome da proprieade do filtro.
	 * @param nomeColuna String contendo o nome da propriedade do filtro.
	 */
	public void setNomePropriedade(String nomeColuna);
	
	/**
	 * Pega o nome da propriedade do filtro.
	 * @return String contendo o nome da propriedade do filtro.
	 */
	public String getNomePropriedade();
	
	/**
	 * Seta o valor da propriedade do filtro.
	 * @param valorPropriedade String que representará o valor da propriedade do filtro.
	 */
	public void setValorPropriedade(String valorPropriedade);
	
	/**
	 * Pega o valor da propriedade do filtro.
	 * @return String que representa o valor da propriedade do filtro.
	 */
	public String getValorPropriedade();
	
	/**
	 * Devolve a sintaxe sql do filtro.
	 * @return String contendo uma sintáxe sql respectiva ao filtro.
	 */
	public String getSQL();
	 
	
}
