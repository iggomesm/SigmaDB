package br.com.sigmadb.beans.utilitarios;

/**
 * Representa um filtro onde determina-se a descrição de um atributo de algum objeto e um valor para o mesmo.
 * @author Igor Moisés
 */
public class BeanFilter<T> {

	private String nomePropriedade;
	private String valorPropriedade;
	
	public BeanFilter(String nomePropriedade, String valorPropriedade){
		this.nomePropriedade = nomePropriedade;
		this.valorPropriedade = String.valueOf(valorPropriedade);		
	}
	
	public String getNomePropriedade() {
		return nomePropriedade;
	}
	public void setNomePropriedade(String nomePropriedade) {
		this.nomePropriedade = nomePropriedade;
	}
	public String getValorPropriedade() {
		return valorPropriedade;
	}
	public void setValorPropriedade(String valorPropriedade) {
		this.valorPropriedade = valorPropriedade;
	}
}
