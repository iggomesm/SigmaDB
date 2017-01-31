package br.com.sigmadb.enumerations;

/**
 * Enum que contem as funcionalidades de filtragem de lista no sistema.
 * <br><br>
 * <b>BUSCA_TODOS_CORRESPONDENTES -</b> Buscar todos os resultados que coincidem com o filtro.<br>
 * <b>BUSCA_TODOS_NAO_CORRESPONDENTES -</b> Buscar todos os resultados que n�o coincidem com o filtro.<br>
 * <b>BUSCA_PRIMEIRO_CORRESPONDENTE -</b> Buscar o primeiro resultado que coincidir com o filtro.<br>
 * <b>BUSCA_PRIMEIRO_NAO_CORRESPONDENTE -</b> Buscar o primeiro resultado que n�o coincir com o filtro.<br>
 * <b>REMOVE_TODOS_CORRESPONDENTES -</b> Remover da lista total todos os objetos que coincidirem com o filtro.<br>
 * <b>RETEM_TODOS_CORRESPONDENTES - </b>Reter na lista total apenas os objetos que coincidirem com o filtro.<br>
 * @author Igor Mois�s
 */
public enum EnumFiltroLista {
	BUSCA_TODOS_CORRESPONDENTES(0),
	BUSCA_TODOS_NAO_CORRESPONDENTES(1),	
	BUSCA_PRIMEIRO_CORRESPONDENTE(4),
	BUSCA_PRIMEIRO_NAO_CORRESPONDENTE(5),
	REMOVE_TODOS_CORRESPONDENTES(6),
	RETEM_TODOS_CORRESPONDENTES(7);
	
	private int index = 0;

	private EnumFiltroLista(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
		
}
