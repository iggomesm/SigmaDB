package br.com.sigmadb.beans.utilitarios;

import java.util.List;

import br.com.sigmadb.util.SigmaDBUtil;

/**
 * Classe utilizada para a aplicação de restrições do tipo IN nas consultas SQL.
 * <br><b>nomePropriedade - </b> Nome da coluna na qual a restrição IN deverá ser aplicada.
 * <br><b>arrayIds -</b>  Todos os IDS que serão utilizados na restrição IN a ser aplicada na coluna definida no atributo nomePropriedade.
 * @author Igor Moisés
 */
public class ComandoSqlIN {
	/**
	 * Guardará o nome da coluna na qual a restrição IN deverá ser aplicada.
	 */
	private String nomePropriedade;
	
	/**
	 * Caso seja true o tipo da consulta sera not in, caso contrario sera in.
	 */
	private boolean notIn;	
	
	/**
	 * Guardará todos os IDS que serão utilizados na restrição IN a ser aplicada na coluna definida no atributo nomePropriedade.
	 */
	private String [] arrayIds;
	
	/**
	 * @param nomePropriedade Nome da coluna na qual a restrição IN deverá ser aplicada.
	 * @param arrayIds Todos os IDS que serão utilizados na restrição IN a ser aplicada na coluna definida no atributo nomePropriedade.
	 */
	public ComandoSqlIN(String nomePropriedade, String[] arrayIds) {
		this.nomePropriedade = nomePropriedade;
		this.arrayIds = arrayIds;
	}
	
	/**
	 * @param nomePropriedade Nome da coluna na qual a restrição IN deverá ser aplicada.
	 */
	public ComandoSqlIN(String nomePropriedade) {
		this.nomePropriedade = nomePropriedade;
	}
	
	/**
	 * @param nomePropriedade Nome da coluna na qual a restrição IN deverá ser aplicada.
	 * @param arrayIds Todos os IDS que serão utilizados na restrição IN a ser aplicada na coluna definida no atributo nomePropriedade.
	 * @param notIn para indicar se será usado a propriedade in ou not in. Caso true será
	 * usado not in, false para usar in. 
	 */
	public ComandoSqlIN(String nomePropriedade, String[] arrayIds, boolean notIn) {
		this.nomePropriedade = nomePropriedade;
		this.arrayIds = arrayIds;
		this.notIn = notIn;
	}

	/**
	 * @param nomePropriedade Nome da coluna na qual a restrição IN deverá ser aplicada.
	 * @param notIn para indicar se será usado a propriedade in ou not in. Caso true será
	 * usado not in, false para usar in. 
	 */
	public ComandoSqlIN(String nomePropriedade, boolean notIn) {
		this.nomePropriedade = nomePropriedade;
		this.notIn = notIn;
	}
	
	public ComandoSqlIN() {}
	
	public String getNomePropriedade() {
		return nomePropriedade;
	}
	public void setNomePropriedade(String nomePropriedade) {
		this.nomePropriedade = nomePropriedade;
	}
	public String[] getArrayIds() {
		return arrayIds;
	}
	public void setArrayIds(String[] arrayIds) {
		this.arrayIds = arrayIds;
	}	
	
	/**
	 * Popula o array de Ids deste objeto com valores de uma propriedade do VO contido numa lista.<br>
	 * <b>Ex.<br>
	 * <font color='green'>
	 * {@code List<Pessoa> listaPessoa = listaPessoas();// Método fictício.}<br>
	 * {@code RestricaoSqlIN restricaoIN = new RestricaoSqlIN();}<br>
	 * {@code restricaoIN.populaArrayIds(listaPessoa, "pess_id"); // Irá inserir na propriedade arrayIds deste objeto, todos os pess_id contidos na listaPessoa.}<br>
	 * </font></b>
	 * @param listaPrincipalVO Lista de qualquer VO que deverá ter um atributo listado.
	 * @param nomePropriedadeListada Nome do atributo que deverá ser listado cujos seus respectivos valores serão os valores da restrição do objeto.
	 * @throws Exception
	 */
	public void populaArrayIds(List listaPrincipalVO, String nomePropriedadeListada) throws Exception{
		this.populaArrayIds(listaPrincipalVO, nomePropriedadeListada, true, true, false);
	}
	
	/**
	 * Popula o array de Ids deste objeto com valores de uma propriedade do VO contido numa lista.<br>
	 * <b>Ex.<br>
	 * <font color='green'>
	 * {@code List<Pessoa> listaPessoa = listaPessoas();// Método fictício.}<br>
	 * {@code RestricaoSqlIN restricaoIN = new RestricaoSqlIN();}<br>
	 * {@code restricaoIN.populaArrayIds(listaPessoa, "pess_id"); // Irá inserir na propriedade arrayIds deste objeto, todos os pess_id contidos na listaPessoa.}<br>
	 * </font></b>
	 * @param listaPrincipalVO Lista de qualquer VO que deverá ter um atributo listado.
	 * @param nomePropriedadeListada Nome do atributo que deverá ser listado cujos seus respectivos valores serão os valores da restrição do objeto.
	* @param removeValoresZerados Indica se os valores nulos ou iguais a zero deverão ser incluídos na nova lista. Caso true os valores nulos ou iguais
	 * a zero serão removidos. Caso false não serão.
	 * @param removeValoresRepetidos Caso True remove os valores repeditos da lista, caso false permite que a lista contenha valores repetidos.
	 * @param valorEntreAspas Caso True adiciona as apas simples nos valores da lista, caso false nao inclui as aspas simples.
	 * @throws Exception
	 */
	public void populaArrayIds(List listaPrincipalVO, String nomePropriedadeListada, boolean removeValoresZerados, boolean removeValoresRepetidos, boolean valorEntreAspas) throws Exception{
		
		if (!SigmaDBUtil.isNullOrEmpty(listaPrincipalVO)){
			
			List listaPropriedades = SigmaDBUtil.listaPropriedadeObjeto(listaPrincipalVO, nomePropriedadeListada, removeValoresZerados, removeValoresRepetidos, valorEntreAspas);
			
			if (!SigmaDBUtil.isNullOrEmpty(listaPropriedades)){
				
				this.arrayIds = SigmaDBUtil.concatenaValoresDaLista(listaPropriedades, null).split(",");
			}
			
		}
	}

	/**
	 * Popula o array de Ids deste objeto com valores de uma lista de tipos primitivos.<br>
	 * <b>Ex.<br>
	 * <font color='green'>
	 * {@code List<Integer> listaIdPessoa = listaIdPessoas();// Método fictício.}<br>
	 * {@code RestricaoSqlIN restricaoIN = new RestricaoSqlIN();}<br>
	 * {@code restricaoIN.populaArrayIds(listaIdPessoa); // Irá inserir na propriedade arrayIds deste objeto, todos os valores contidos na lista.}<br>
	 * </font></b>
	 * @param listaPrincipal Lista de qualquer tipo primitivo que deverá ter seus valores listados.
	 * @throws Exception
	 */
	
	public void populaArrayIds(List listaPrincipal) throws Exception {
		this.populaArrayIds(listaPrincipal, false);
	}	
	
	/**
	 * Popula o array de Ids deste objeto com valores de uma lista de tipos primitivos.<br>
	 * <b>Ex.<br>
	 * <font color='green'>
	 * {@code List<String> listaNomePessoa = listaNomePessoas();// Método fictício.}<br>
	 * {@code RestricaoSqlIN restricaoIN = new RestricaoSqlIN();}<br>
	 * {@code restricaoIN.populaArrayIds(listaIdPessoa, true); // Irá inserir na propriedade arrayIds deste objeto, todos os valores contidos na lista, 
	 * onde cada valor estará entre aspas simples.}<br>
	 * </font></b>
	 * @param listaPrincipal Lista de qualquer tipo primitivo que deverá ter seus valores listados.
	 * @param valorEntreAspas Caso True adiciona as apas simples nos valores da lista, caso false nao inclui as aspas simples.
	 * @throws Exception
	 */
	public void populaArrayIds(List listaPrincipal, boolean valorEntreAspas) throws Exception{
		
		if (!SigmaDBUtil.isNullOrEmpty(listaPrincipal)){
			
			String ids = SigmaDBUtil.concatenaValoresDaLista(listaPrincipal, null, valorEntreAspas);
			
			if (!SigmaDBUtil.isNullOrEmpty(ids)){
				
				String [] arrayIds = ids.split(",");
				
				this.setArrayIds(arrayIds);				
			}
		}
	}	

	/**
	 * Indicará ao objeto se ele deverá ser um IN ou um NOT IN.
	 * @return True se o objeto for respectivo a um NOT IN. False se o objeto for respectivo a um IN.
	 */
	public boolean isNotIn() {
		return notIn;
	}

	/**
	 * Indicará ao objeto se ele deverá ser um IN ou um NOT IN.
	 * @param notIn Se true o objeto fará um NOT IN. Se false fará um IN.
	 */
	public void setNotIn(boolean notIn) {
		this.notIn = notIn;
	}	
}
