package br.com.sigmadb.util;

import java.lang.reflect.InvocationTargetException;

public interface FiltroLista<T> {
	/**
     * Verifica se o objeto candidato passa pelo filtro.
     * @param elementoCandidato Objeto candidato.
     * @return  <tt>true</tt> - caso o candidato passe no filtro.
     *          <br><tt>false</tt> - caso o candidato n�o pesse pelo filtro.
     */
    public boolean corresponde(T elementoCandidato)throws Exception;
    
    /**
	 * Verifica se a lista de filtros informada possui filtros voltados para uma �nica propriedade do VO.
	 * @return True caso a lista possui filtros voltados para uma mesma propriedade. False caso contr�rio.
	 */
    public boolean verificaSeFiltraUnicaPropriedade();
    /**
     * Metodo para ordenar a lista de filtros antes de come�ar a busca.
     * @param obj objeto para checar a propriedade da ordenacao
     * e se for int ou double efetuar� a ordenacao com parse do valor
     * da propriedade. 
     */
    public void ordenaFiltros(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

    /**
     * Metodo para verificar a continua��o da busca de elementos a partir do elemento que
     * foi passado por paramentro. Caso o candidato seja maior que o ultimo elemento do filtro
     * a busca ser� encerrada pois nao h� necessidade de continuar a busca sendo que a lista de filtro
     * est� ordenada assim como a lista a ser filtrada.
     * @param elementoCandidato Utilizado para checagem com o ultimo filtro da lista.
     * @return true caso o elemento candidato seja <= ao ultimo elemento do filtro, false caso contr�rio.
     * @throws Exception
     */
    public boolean continuaFiltrando(T elementoCandidato) throws Exception;
    
    /**
	 * Metodo para checar se h� listaFiltro
	 * @return true caso exista lista filtro. false caso contrario.
	 */
    public boolean existListaFiltro();
    
    
}
