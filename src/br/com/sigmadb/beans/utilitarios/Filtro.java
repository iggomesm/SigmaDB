package br.com.sigmadb.beans.utilitarios;

/**
 * Classe que representa a restri��o de filtro para um determinado atributo de um objeto de uma lista.
 * @author Igor Mois�s
 *
 */
public class Filtro {

	private String nomePropriedade;
	private String valorPropriedade;
	
	public Filtro(String nomePropriedade, String valorPropriedade){
		this.nomePropriedade = nomePropriedade;
		// Tarefa: 1401 - Igor Mois�s (Inicio)
		/*this.valorPropriedade = valorPropriedade;*/
		this.valorPropriedade = String.valueOf(valorPropriedade);
		// Tarefa: 1401 - Igor Mois�s (Fim)
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
