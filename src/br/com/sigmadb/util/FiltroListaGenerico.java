package br.com.sigmadb.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.sigmadb.beans.utilitarios.BeanFilter;
import br.com.sigmadb.enumerations.EnumSortType;
import br.com.sigmadb.exceptions.SigmaDBException;

/**
 * Realiza a triagem dos elementos das listas.
 * 
 * @author Igor Moisés
 * 
 * @param <T>
 */
public class FiltroListaGenerico<T extends Object> implements FiltroLista<T> {

	private List<BeanFilter> listaFiltroVO;
	private BeanFilter filtroVO;

	public FiltroListaGenerico(List<BeanFilter> listaFiltroVO) {
		this.listaFiltroVO = listaFiltroVO;
	}

	public FiltroListaGenerico(BeanFilter filtroVO) {
		this.filtroVO = filtroVO;
	}

	public boolean corresponde(T elementoCandidato) throws Exception {
		boolean deveFiltrar = false;

		if (!SigmaDBUtil.isNullOrEmpty(listaFiltroVO)) {

			deveFiltrar = this
					.verificaSeDeveFiltrarListaFiltroVO(elementoCandidato);

		} else {

			deveFiltrar = this.verificaSeDeveFiltrarFiltroVO(elementoCandidato);
		}

		return deveFiltrar;
	}

	/**
	 * Responsável por verificar se o atributo listaFiltroVO da classe possui
	 * filtros e se os mesmos correspondem ao elemento informado como parâmetro.
	 * 
	 * @param elementoCandidato
	 *            Elemento que deverá ter seus valores comparados aos do filtro.
	 * @return Boleano indicando se o elemento deve ou não ser filtrado. TRUE
	 *         caso deva ser filtrado FALSE caso contrário.
	 * @throws Exception
	 */
	private boolean verificaSeDeveFiltrarListaFiltroVO(T elementoCandidato)
			throws Exception {

		int contagemFiltroAtendido = 0;

		boolean deveContabilizar = false;

		boolean possuiUnicaPropriedade = this
				.verificaSeFiltraUnicaPropriedade();

		for (Iterator listaFiltroIterator = listaFiltroVO.iterator(); listaFiltroIterator
				.hasNext();) {

			BeanFilter filtro = (BeanFilter) listaFiltroIterator.next();

			boolean ehCampoData = this.verificaCampoTipoData(elementoCandidato,
					filtro);

			String valor = null;

			if (ehCampoData) {
				java.sql.Timestamp data = (java.sql.Timestamp) SigmaDBReflectionUtil
						.getValorMetodoGet(elementoCandidato,
								filtro.getNomePropriedade());

				if (data == null) {
					valor = String.valueOf(data);
				} else {
					valor = new Day(data).toString();
				}
			} else {
				valor = String.valueOf(SigmaDBReflectionUtil.getValorMetodoGet(
						elementoCandidato, filtro.getNomePropriedade()));

			}
			deveContabilizar = valor.equals(filtro.getValorPropriedade());

			if (deveContabilizar) {
				contagemFiltroAtendido++;
			}

		}

		if (possuiUnicaPropriedade) {

			deveContabilizar = contagemFiltroAtendido > 0;
		} else {

			deveContabilizar = listaFiltroVO.size() == contagemFiltroAtendido;
		}
		return deveContabilizar;
	}

	/**
	 * Verifica se a lista de filtros informada possui filtros voltados para uma
	 * única propriedade do VO.
	 * 
	 * @return True caso a lista possui filtros voltados para uma mesma
	 *         propriedade. False caso contrário.
	 */
	public boolean verificaSeFiltraUnicaPropriedade() {

		boolean possuiUnicaPropriedade = true;

		if (!SigmaDBUtil.isNullOrEmpty(listaFiltroVO)) {
			String nomeUltimaPropriedade = (String) ((BeanFilter) SigmaDBUtil
					.getFirst(listaFiltroVO)).getNomePropriedade();

			for (Iterator listaFiltroIterator = listaFiltroVO.iterator(); listaFiltroIterator
					.hasNext();) {
				BeanFilter filtro = (BeanFilter) listaFiltroIterator.next();

				possuiUnicaPropriedade = nomeUltimaPropriedade.equals(filtro
						.getNomePropriedade());

				if (!possuiUnicaPropriedade) {
					break;
				}
			}
		}

		return possuiUnicaPropriedade;
	}

	/**
	 * Responsável por verificar se o atributo filtroVO da classe possui filtro
	 * e se o mesmo corresponde ao elemento informado como parâmetro.
	 * 
	 * @param elementoCandidato
	 *            Elemento que deverá ter seus valores comparados aos do filtro.
	 * @return Boleano indicando se o elemento deve ou não ser filtrado. TRUE
	 *         caso deva ser filtrado FALSE caso contrário.
	 * @throws Exception
	 */
	private boolean verificaSeDeveFiltrarFiltroVO(T elementoCandidato)
			throws Exception {
		boolean ehCampoData = this.verificaCampoTipoData(elementoCandidato,
				filtroVO);

		String valor = null;

		if (ehCampoData) {
			java.sql.Timestamp data = (java.sql.Timestamp) SigmaDBReflectionUtil
					.getValorMetodoGet(elementoCandidato,
							filtroVO.getNomePropriedade());

			if (data == null) {
				valor = String.valueOf(data);
			} else {
				valor = new Day(data).toString();
			}

		} else {
			valor = String.valueOf(SigmaDBReflectionUtil.getValorMetodoGet(
					elementoCandidato, filtroVO.getNomePropriedade()));

		}
		return valor.equals(filtroVO.getValorPropriedade());
	}

	/**
	 * Verifica se o campo solicitado como filtro representa uma data.
	 * 
	 * @param elementoCandidato
	 *            Elemento que deverá ter seus valores comparados aos do filtro.
	 * @param filtro
	 *            Elemento que contem a restrição do filtro
	 * @return Boleano indicando se o campo solicitado como filtro é uma data.
	 * @throws Exception
	 */
	private boolean verificaCampoTipoData(T elementoCandidato, BeanFilter filtro)
			throws Exception {

		Method retorno = SigmaDBReflectionUtil.getMetodoGetNaClasse(
				elementoCandidato.getClass(), filtro.getNomePropriedade());

		if (retorno == null) {
			throw new SigmaDBException(
					"Nome da propriedade informada não existe no Bean desta lista.");
		}

		String nomeMetodo = retorno.getName();

		Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(elementoCandidato,
				nomeMetodo);

		return tipo == java.sql.Timestamp.class;
	}

	/**
	 * Metodo para ordenar a lista de filtros antes de começar a busca.
	 * 
	 * @param obj
	 *            objeto para checar a propriedade da ordenacao e se for int ou
	 *            double efetuará a ordenacao com parse do valor da propriedade.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void ordenaFiltros(Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (!SigmaDBUtil.isNullOrEmpty(listaFiltroVO)) {
			BeanFilter filtro = (BeanFilter) SigmaDBUtil.getFirst(listaFiltroVO);
			ordenarPelaPropriedade(filtro, obj);
		}
	}

	/**
	 * Metodo para checar o tipo da propriedade e ordenar convertendo o valor do
	 * FiltroVO para int ou double.
	 * 
	 * @param filtro
	 *            valores a serem ordenados.
	 * @param obj
	 *            objeto para obter o tipo do valor da propriedade do FiltroVO
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void ordenarPelaPropriedade(BeanFilter filtro, Object obj)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(obj,
				SigmaDBReflectionUtil.getNomeMetodoGet(filtro.getNomePropriedade()));

		if (tipo.getName().equalsIgnoreCase(Integer.class.getName())
				|| tipo.getName().equalsIgnoreCase("int")) {

			ordenaListaInteger(filtro.getNomePropriedade());

		} else if (tipo.getName().equalsIgnoreCase(Double.class.getName())
				|| tipo.getName().equalsIgnoreCase("double")) {

			ordenaListaDouble(filtro.getNomePropriedade());
		} else {
			SigmaDBUtil.ordenaLista(listaFiltroVO, "valorPropriedade",
					EnumSortType.ASC);
		}
	}

	/**
	 * Metodo para ordenar uma lista convertendo valores para double e limpando
	 * o listaFiltroVO e criando novamente com a ordenação correta.
	 * 
	 * @param nomePropriedade
	 *            propriedade a ser ordenada
	 */
	public void ordenaListaDouble(String nomePropriedade) {
		List<Double> listaDouble = new ArrayList<Double>();
		for (Iterator iterator = listaFiltroVO.iterator(); iterator.hasNext();) {
			BeanFilter filtro = (BeanFilter) iterator.next();
			listaDouble.add(Double.parseDouble(filtro.getValorPropriedade()));
		}

		Collections.sort(listaDouble);

		listaFiltroVO.clear();
		for (Iterator iterator = listaDouble.iterator(); iterator.hasNext();) {
			Double doubleValor = (Double) iterator.next();
			listaFiltroVO.add(new BeanFilter(nomePropriedade, String
					.valueOf(doubleValor)));
		}
	}

	/**
	 * Metodo para ordenar uma lista convertendo valores para int e limpando o
	 * listaFiltroVO e criando novamente com a ordenação correta.
	 * 
	 * @param nomePropriedade
	 *            propriedade a ser usada no {@link BeanFilter} para recriação da
	 *            listaFiltroVO
	 */
	public void ordenaListaInteger(String nomePropriedade) {
		List<Integer> listaInteger = new ArrayList<Integer>();
		for (Iterator iterator = listaFiltroVO.iterator(); iterator.hasNext();) {
			BeanFilter filtro = (BeanFilter) iterator.next();
			listaInteger.add(Integer.parseInt(filtro.getValorPropriedade()));
		}

		Collections.sort(listaInteger);

		listaFiltroVO.clear();

		for (Iterator iterator = listaInteger.iterator(); iterator.hasNext();) {
			Integer inteiroValor = (Integer) iterator.next();
			listaFiltroVO.add(new BeanFilter(nomePropriedade, String
					.valueOf(inteiroValor)));
		}
	}

	/**
	 * Metodo para verificar a continuação da busca de elementos a partir do
	 * elemento que foi passado por paramentro. Caso o candidato seja maior que
	 * o ultimo elemento do filtro a busca será encerrada pois nao há
	 * necessidade de continuar a busca sendo que a lista de filtro está
	 * ordenada assim como a lista a ser filtrada.
	 * 
	 * @param elementoCandidato
	 *            Utilizado para checagem com o ultimo filtro da lista.
	 * @return true caso o elemento candidato seja <= ao ultimo elemento do
	 *         filtro, false caso contrário.
	 * @throws Exception
	 */
	public boolean continuaFiltrando(T elementoCandidato) throws Exception {

		boolean continuaFiltrando = false;

		Class tipo = null;

		if (!SigmaDBUtil.isNullOrEmpty(listaFiltroVO)) {
			BeanFilter filtro = (BeanFilter) SigmaDBUtil.getLast(listaFiltroVO);
			tipo = SigmaDBReflectionUtil
					.pegaTipoDoMetodoGet(elementoCandidato, SigmaDBReflectionUtil
							.getNomeMetodoGet(filtro.getNomePropriedade()));
		} else {
			tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(elementoCandidato,
					SigmaDBReflectionUtil.getNomeMetodoGet(filtroVO
							.getNomePropriedade()));
		}

		if (tipo.getName().equalsIgnoreCase(Integer.class.getName())
				|| tipo.getName().equalsIgnoreCase("int")) {

			continuaFiltrando = deveFiltrarValorAtributoInteiro(elementoCandidato);

		} else if (tipo.getName().equalsIgnoreCase(Double.class.getName())
				|| tipo.getName().equalsIgnoreCase("double")) {
			continuaFiltrando = deveFiltrarValorAtributoDouble(elementoCandidato);

		} else if (tipo.getName().equalsIgnoreCase(
				java.sql.Timestamp.class.getName())) {
			continuaFiltrando = deveFiltrarValorAtributoTimestamp(elementoCandidato);

		} else if (tipo.getName().equalsIgnoreCase(String.class.getName())) {
			continuaFiltrando = deveFiltrarValorAtributoString(elementoCandidato);
		}

		return continuaFiltrando;
	}

	/**
	 * Metodo para checar se há listaFiltro
	 * 
	 * @return true caso exista lista filtro. false caso contrario.
	 */
	public boolean existListaFiltro() {
		return !SigmaDBUtil.isNullOrEmpty(listaFiltroVO);
	}

	/**
	 * Metodo para checar se deve continuar a filtrar. Checando aqui pelo tipo
	 * Inteiro.
	 * 
	 * @param elementoCandidato
	 *            Utilizado para checagem com o ultimo filtro da lista.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private boolean deveFiltrarValorAtributoInteiro(T elementoCandidato)
			throws IllegalAccessException, InvocationTargetException {

		boolean continuaFiltrando;

		BeanFilter filtro = SigmaDBUtil.isNullOrEmpty(listaFiltroVO) ? filtroVO
				: (BeanFilter) SigmaDBUtil.getLast(listaFiltroVO);

		try {
			int valorUltimoFiltro = Integer.parseInt(filtro
					.getValorPropriedade());

			String valorAtributo = String.valueOf(SigmaDBReflectionUtil
					.getValorMetodoGet(elementoCandidato,
							filtro.getNomePropriedade()));

			int valorInteiroCandidato = Integer.parseInt(valorAtributo);

			continuaFiltrando = valorUltimoFiltro >= valorInteiroCandidato;

		} catch (NumberFormatException e) {
			continuaFiltrando = false;
		}
		return continuaFiltrando;
	}

	/**
	 * Metodo para checar se deve continuar a filtrar. Checando aqui pelo tipo
	 * Double.
	 * 
	 * @param elementoCandidato
	 *            Utilizado para checagem com o ultimo filtro da lista.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private boolean deveFiltrarValorAtributoDouble(T elementoCandidato)
			throws IllegalAccessException, InvocationTargetException {

		boolean continuaFiltrando;

		BeanFilter filtro = SigmaDBUtil.isNullOrEmpty(listaFiltroVO) ? filtroVO
				: (BeanFilter) SigmaDBUtil.getLast(listaFiltroVO);

		try {
			double valorUltimoFiltro = Double.parseDouble(filtro
					.getValorPropriedade());

			String valorAtributo = String.valueOf(SigmaDBReflectionUtil
					.getValorMetodoGet(elementoCandidato,
							filtro.getNomePropriedade()));

			double valorDoubleCandidato = Double.parseDouble(valorAtributo);

			continuaFiltrando = valorUltimoFiltro >= valorDoubleCandidato;

		} catch (NumberFormatException e) {
			continuaFiltrando = false;
		}
		return continuaFiltrando;
	}

	/**
	 * Metodo para checar se deve continuar a filtrar. Checando aqui pelo tipo
	 * Timestamp.
	 * 
	 * @param elementoCandidato
	 *            Utilizado para checagem com o ultimo filtro da lista.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private boolean deveFiltrarValorAtributoTimestamp(T elementoCandidato)
			throws IllegalAccessException, InvocationTargetException {

		boolean continuaFiltrando;

		BeanFilter filtro = SigmaDBUtil.isNullOrEmpty(listaFiltroVO) ? filtroVO
				: (BeanFilter) SigmaDBUtil.getLast(listaFiltroVO);

		try {

			Day valorUltimoFiltro = new Day(filtro.getValorPropriedade());

			java.sql.Timestamp dataAtributo = (java.sql.Timestamp) SigmaDBReflectionUtil
					.getValorMetodoGet(elementoCandidato,
							filtro.getNomePropriedade());

			Day valorCandidato = new Day(dataAtributo);

			continuaFiltrando = valorUltimoFiltro.maiorIgualA(valorCandidato);

		} catch (Exception e) {
			continuaFiltrando = false;
		}
		return continuaFiltrando;
	}

	/**
	 * Metodo para checar se deve continuar a filtrar. Checando aqui pelo tipo
	 * String.
	 * 
	 * @param elementoCandidato
	 *            Utilizado para checagem com o ultimo filtro da lista.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private boolean deveFiltrarValorAtributoString(T elementoCandidato)
			throws IllegalAccessException, InvocationTargetException {

		boolean continuaFiltrando;

		BeanFilter filtro = SigmaDBUtil.isNullOrEmpty(listaFiltroVO) ? filtroVO
				: (BeanFilter) SigmaDBUtil.getLast(listaFiltroVO);
		try {

			String valorUltimoFiltro = filtro.getValorPropriedade();

			String valorCandidato = String.valueOf(SigmaDBReflectionUtil
					.getValorMetodoGet(elementoCandidato,
							filtro.getNomePropriedade()));

			continuaFiltrando = valorUltimoFiltro
					.compareToIgnoreCase(valorCandidato) >= 0;

		} catch (Exception e) {
			continuaFiltrando = false;
		}
		return continuaFiltrando;
	}
}
