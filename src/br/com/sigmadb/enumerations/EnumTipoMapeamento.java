/**
 * 
 */
package br.com.sigmadb.enumerations;

/**
 * Enum que indica os tipos de m�todos de gera��o de mapas din�micos do sistema.<br><br>
 * <b>MAPEIA_VALORES_EM_LISTA_DE_OBJETOS</b> - Indica que o mapa que ser� criado utilizando este crit�rio ser� do tipo <font color ='blue'><b>{@code Map<Object, List>}</b></font>.
 * <br><br>
 * <b>MAPEIA_VALORES_EM_OBJETO</b> - Indica que o mapa que ser� criado utilizando este crit�rio ser� do tipo <font color ='blue'><b>{@code Map<Object, Object>}</b></font>.
 * @author Igor Mois�s
 * @since 18/02/2014
 */
public enum EnumTipoMapeamento {
	MAPEIA_VALORES_EM_LISTA_DE_OBJETOS(1),
	MAPEIA_VALORES_EM_OBJETO(2);
	
	private int indice;
	
	/**
	 * @param indice
	 */
	private EnumTipoMapeamento(int indice) {
		this.indice = indice;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
	
	
	
}
