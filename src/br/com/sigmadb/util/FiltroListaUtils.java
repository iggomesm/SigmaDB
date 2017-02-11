package br.com.sigmadb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Classe utilit�ria para manipular cole��es
 * 
 * @author Igor Mois�s
 */
public class FiltroListaUtils {
	/**
	 * N�o � possivel instanciar CollectionUtils.
	 */
	private FiltroListaUtils() {
	}

	/**
	 * <b><font color ='green'>Adiciona a uma segunda lista todos os itens da primeira lista que
	 * coincidirem com o filtro.</font></b><br>
	 * <br>
	 * Adciona a cole��o <tt>recipient</tt> todos os objetos <tt>T</tt> da lista
	 * de <tt>candidates</tt> que coincidirem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Adciona o inverso dos itens de <tt>candidates</tt> que
	 * {@link #adicionaElementosNaoCorrespondentes(Collection, FiltroLista, Collection)}
	 * adcionaria.
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser adcionado.
	 * @param candidates
	 *            a cole��o de candidatos a serem adcionados ao
	 *            <tt>recipient</tt>.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>aceitar</u></b> um
	 *            candidato a ser adcionado ao <tt>recipient</tt>.
	 * @param recipient
	 *            cole��o onde os objetos ser�o adicionados.
	 * @see #adicionaElementosNaoCorrespondentes(Collection, FiltroLista, Collection)
	 */
	public static <T> void adicionaElementosCorrespondentes(
			Collection<? extends T> candidates, FiltroLista<T> filter,
			Collection recipient) throws Exception {

		boolean unicaPropriedade = false;

		if(	filter.verificaSeFiltraUnicaPropriedade() && filter.existListaFiltro() ) {
			Object obj = candidates.iterator().next();
			filter.ordenaFiltros(obj);
			unicaPropriedade = true;
		}

			
		boolean flagEncontrouElemento = false;
		boolean abriuFaixaAdicionamento = false;
		for (T object : candidates) {
			if (filter.corresponde(object)) {
				flagEncontrouElemento = true;
				abriuFaixaAdicionamento = true;
				recipient.add(object);
			} else {
				abriuFaixaAdicionamento = false;
			}
			
			if( unicaPropriedade ) {
				if (!filter.continuaFiltrando(object)){
					break;
				}
			} else {
				if (flagEncontrouElemento && !abriuFaixaAdicionamento){
					break;
				}
			}
		}
	}

	/**
	 * <b><font color ='green'>Adiciona a uma segunda lista todos os itens da primeira lista que n�o
	 * coincidirem com o filtro.</font></b><br>
	 * <br>
	 * Adiciona a cole��o. <tt>recipient</tt> todos os objetos <tt>T</tt> da
	 * lista de <tt>candidates</tt> que n�o coincidirem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Adciona o inverso dos itens de <tt>candidates</tt> que
	 * {@link #addTodosElementosCorrespondente(Collection, FiltroLista, Collection)}
	 * adcionaria.
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser adcionado.
	 * @param candidates
	 *            a cole��o de candidatos a serem adcionados ao
	 *            <tt>recipient</tt>.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>recusar</u></b> um
	 *            candidato a ser adcionado ao <tt>recipient</tt>.
	 * @param recipient
	 *            cole��o onde os objetos ser�o adicionados.
	 * @see #adicionaElementosNaoCorrespondentes(Collection, FiltroLista, Collection)
	 */
	public static <T> void adicionaElementosNaoCorrespondentes(
			Collection<? extends T> candidates, FiltroLista<T> filter,
			Collection recipient) throws Exception {
		for (T object : candidates) {
			if (!filter.corresponde(object)) {
				recipient.add(object);
			}
		}
	}

	/**
	 * <b><font color ='green'>Buscar todos os resultados que coincidem com o
	 * filtro.</font></b><br>
	 * <br>
	 * Busca todos os objetos <b>T</b> na lista de <tt>candidates</tt> que
	 * conferem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Tem o resultado inverso a
	 * {@link #buscaTodosNaoCorrespondentes(Collection, FiltroLista)}
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser buscado.
	 * @param candidates
	 *            a cole��o de condidatos aonde deseja-se realizar a busca.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>aceitar</u></b> um
	 *            candidato no resultado da busca.
	 * @return todos os itens que passaram pelo filtro.
	 * @see #buscaTodosNaoCorrespondentes(Collection, FiltroLista)
	 */
	public static <T> List<T> buscaTodosCorrespondentes(
			Collection<? extends T> candidates, FiltroLista<T> filter)
			throws Exception {
		List<T> matchs = new ArrayList<T>(0);
		adicionaElementosCorrespondentes(candidates, filter, matchs);
		return matchs;
	}

	/**
	 * <b><font color ='green'>Buscar todos os resultados que n�o coincidem com o filtro.</font></b><br>
	 * <br>
	 * Busca todos os objetos <b>T</b> na lista de <tt>candidates</tt> que
	 * <u>n�o</u> conferem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Tem o resultado inverso a
	 * {@link #buscaTodosCorrespondentes(Collection, FiltroLista)}
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser buscado.
	 * @param candidates
	 *            a cole��o de condidatos aonde deseja-se realizar a busca.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>negar</u></b> um candidato
	 *            no resultado da busca.
	 * @return todos os itens que <u>n�o</u> passaram pelo filtro.
	 * @see #buscaTodosCorrespondentes(Collection, FiltroLista)
	 */
	public static <T> List<T> buscaTodosNaoCorrespondentes(
			Collection<? extends T> candidates, FiltroLista<T> filter)
			throws Exception {
		List<T> notMatchs = new ArrayList<T>(0);
		adicionaElementosNaoCorrespondentes(candidates, filter, notMatchs);
		return notMatchs;
	}

	/**
	 * <b><font color ='green'>Buscar o primeiro resultado que coincidir com o filtro.</font></b><br>
	 * <br>
	 * Busca o primeiro objeto <tt>T</tt> na cole��o <tt>candidates</tt> que
	 * coincide com o <tt>filter</tt>.
	 * 
	 * @param <T>
	 *            o tipo de objeto a procurar.
	 * @param candidates
	 *            a cole��o de candidatos a ser pesquisados.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>aceitar</u></b> o
	 *            candidato.
	 * @return O primeiro objeto de <tt>candidates</tt> que coincidir com o
	 *         <tt>filter</tt>.
	 */
	public static <T> T buscaPrimeiroElementoCorrespondente(
			Collection<? extends T> candidates, FiltroLista<T> filter)
			throws Exception {
		for (T object : candidates) {
			if (filter.corresponde(object)) {
				return object;
			}
		}
		return null;
	}

	/**
	 * <b><font color ='green'>Buscar o primeiro resultado que n�o coincir com o filtro.</font></b><br>
	 * <br>
	 * Busca o primeiro objeto <tt>T</tt> na cole��o <tt>candidates</tt> que
	 * <u>n�o</u> coincide com o <tt>filter</tt>.
	 * 
	 * @param <T>
	 *            o tipo de objeto a procurar.
	 * @param candidates
	 *            a cole��o de candidatos a ser pesquisados.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>recusar</u></b> o
	 *            candidato.
	 * @return O primeiro objeto de <tt>candidates</tt> que <u>n�o</u> coincidir
	 *         com o <tt>filter</tt>.
	 */
	public static <T> T buscaPrimeiroElementoNaoCorrespondente(
			Collection<? extends T> candidates, FiltroLista<T> filter)
			throws Exception {
		for (T object : candidates){
			if (!filter.corresponde(object)){
				return object;
			}
		}	
		return null;
	}

	/**
	 * <b><font color ='green'>Remover da lista total todos os objetos que coincidirem com o filtro.</font></b><br>
	 * <br>
	 * Remove da cole��o de <tt>candidates</tt> todos os objetos <tt>T</tt> que
	 * coincidirem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Tem o resultado inverso a {@link #retemApenasCorrespondentes(Collection, FiltroLista)}.
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser filtrado.
	 * @param candidates
	 *            a cole��o que ser� filtrada.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>remover</u></b> os
	 *            candidato, da cole��o <tt>candidates</tt>.
	 * @throws UnsupportedOperationException
	 *             caso a opera��o {@link Iterator#remove()} n�o seja suportada
	 *             pelo {@link Iterator} gerado pela cole��o <tt>candidates</tt>
	 *             .
	 * @see Collection#iterator()
	 */
	public static <T> void removeTodosCorrespondentes(Collection<? extends T> candidates,
			FiltroLista<T> filter) throws Exception {
		for (Iterator<? extends T> itr = candidates.iterator(); itr.hasNext();){
			if (filter.corresponde(itr.next())){
				itr.remove();
			}
		}	
	}

	/**
	 * <b><font color ='green'>Reter na lista total apenas os objetos que coincidirem com o filtro.</font></b><br>
	 * <br>
	 * Retem na cole��o de <tt>candidates</tt> apenas os objetos <tt>T</tt> que
	 * coincidirem com o <tt>filter</tt>. <BR>
	 * <BR>
	 * Tem o resultado inverso a {@link #removeTodosCorrespondentes(Collection, FiltroLista)}.
	 * 
	 * @param <T>
	 *            o tipo de objeto a ser filtrado.
	 * @param candidates
	 *            a cole��o que ser� filtrada.
	 * @param filter
	 *            o filtro que ser� usado para <b><u>reter</u></b> os candidato,
	 *            da cole��o <tt>candidates</tt>.
	 * @throws UnsupportedOperationException
	 *             caso a opera��o {@link Iterator#remove()} n�o seja suportada
	 *             pelo {@link Iterator} gerado pela cole��o <tt>candidates</tt>
	 *             .
	 * @see Collection#iterator()
	 */
	public static <T> void retemApenasCorrespondentes(Collection<? extends T> candidates,
			FiltroLista<T> filter) throws Exception {
		for (Iterator<? extends T> itr = candidates.iterator(); itr.hasNext();)
			if (!filter.corresponde(itr.next()))
				itr.remove();
	}
}
