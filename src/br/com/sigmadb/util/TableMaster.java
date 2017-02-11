package br.com.sigmadb.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import br.com.sigmadb.annotations.PKTableMaster;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.util.interfaces.Filter;

public abstract class TableMaster {
	
	/**
	 * Insere um id na Tabela.
	 * @param id Id da tabela.
	 * @throws Exception
	 */
	public abstract void setId(int id) throws Exception;
	
	/**
	 * Montará a sintáxe sql de insert de uma tabela.
	 * 
	 * @return String contendo a sintáxe slq que será enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public String toInsert() throws Exception {
		return toInsert(this);
	}

	/**
	 * Montará a sintáxe sql de upadte de uma tabela.
	 * @param filtros Listagem de filtros que irão compor a cláusula where do Update
	 * @return String contendo a sintáxe slq que será enviada ao banco.
	 */
	public String toUpdate(List<Filter> filtros) throws Exception {
		return toUpdate(this, filtros);
	}

	/**
	 * Montará a sintáxe sql de delete de uma tabela.
	 * @param filtros Listagem de filtros que irão compor a cláusula where do Delete
	 * @return String contendo a sintáxe slq que será enviada ao banco.
	 */
	public String toDelete(List<Filter> filtros) throws Exception {
		return toDelete(this, filtros);
	}

	/**
	 * Montará a sintáxe sql de insert de uma tabela.
	 * 
	 * @return String contendo a sintáxe sql que será enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	protected String toInsert(Object objeto) throws Exception {

		String nomeTabela = SigmaDBUtil.pegaNomeTabela(objeto);
		List<String> listaAtributos = SigmaDBReflectionUtil
				.listaNomeDosAtributosDoObjetoVO(objeto);

		StringBuffer sql = new StringBuffer("INSERT INTO "
				+ nomeTabela.toUpperCase());
		
		StringBuffer colunas = new StringBuffer(" (");
		StringBuffer valores = new StringBuffer(" (");

		String colunaPk = (String)SigmaDBUtil.getFirst(SigmaDBReflectionUtil.pegaColunasPK(objeto.getClass()));
		
		for (Iterator iterator = listaAtributos.iterator(); iterator.hasNext();) {
			String atributo = (String) iterator.next();
			
			if (!atributo.equals(colunaPk)){

				if (iterator.hasNext()) {
					colunas.append(atributo.toUpperCase() + ", ");
				} else {
					colunas.append(atributo.toUpperCase() + ")");
				}
			} else if (!iterator.hasNext()) {
				String subSql = colunas.substring(0, colunas.length() -2);
				colunas = new StringBuffer(subSql + ")");
			}
		}
		sql.append(colunas);
		sql.append(" VALUES ");

		for (Iterator iterator = listaAtributos.iterator(); iterator.hasNext();) {
			String atributo = (String) iterator.next();
			
			if (!atributo.equals(colunaPk)){

				String nomeMetodoGet = SigmaDBReflectionUtil.getNomeMetodoGet(atributo);

				String valorFormatado = this.pegaValorAtributoFormatado(objeto,
						atributo);

				valores.append(valorFormatado);

				if (iterator.hasNext()) {
					valores.append(", ");
				} else {
					valores.append(")");
				}
			} else if (!iterator.hasNext()) {
				String subSql = valores.substring(0, valores.length() -2);
				valores = new StringBuffer(subSql + ")");
			}
		}
		sql.append(valores);

		return sql.toString();
	}

	/**
	 * Montará a sintáxe sql de update de uma tabela.
	 * @param objeto Objeto contendo todos os dados que serão persistidos no banco.
	 * @param filtros Listagem de filtros que irão compor a cláusula where do Update
	 * @return String contendo a sintáxe sql que será enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws RestricaoException 
	 */
	protected String toUpdate(Object objeto, List<Filter> filtros) throws Exception {

		String nomeTabela = SigmaDBUtil.pegaNomeTabela(objeto);
		List<String> listaAtributos = SigmaDBReflectionUtil
				.listaNomeDosAtributosDoObjetoVO(objeto);

		StringBuffer sql = new StringBuffer("UPDATE "
				+ nomeTabela.toUpperCase() + " SET ");

		for (Iterator iterator = listaAtributos.iterator(); iterator.hasNext();) {
			String atributo = (String) iterator.next();

			String valorFomatado = this.pegaValorAtributoFormatado(objeto,
					atributo);

			sql.append(atributo.toUpperCase() + " = " + valorFomatado);

			if (iterator.hasNext()) {
				sql.append(", ");
			}
		}

		String where = this.montaClausulaWhere(objeto, filtros);

		sql.append(where);

		return sql.toString();
	}

	/**
	 * Montará a sintáxe sql de delete de uma tabela.
	 * @param objeto Objeto contendo todos os dados que serão deletados no banco.
	 * @param filtros Listagem de filtros que irão compor a cláusula where do Delete
	 * @return String contendo a sintáxe sql que será enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws RestricaoException 
	 */
	protected String toDelete(Object objeto, List<Filter> filtros) throws Exception {

		String nomeTabela = SigmaDBUtil.pegaNomeTabela(objeto);

		StringBuffer sql = new StringBuffer("DELETE FROM "
				+ nomeTabela.toUpperCase());

		String where = this.montaClausulaWhere(objeto, filtros);

		sql.append(where);

		return sql.toString();
	}

	/**
	 * Monta a restrição para updates e deletes baseado nas PK selecioandas para
	 * o objeto.
	 * 
	 * @param objeto
	 *            Objeto que contenha uma {@link PKTableMaster} indicando uma ou
	 *            mais PrimaryKeys.
	 * @param filtros Listagem de filtros que irão compor a cláusula where da operação.           
	 * @return Cláusula where montada.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws RestricaoException 
	 */
	private String montaClausulaWhere(Object objeto, List<Filter> filtros)
			throws Exception {

		final String where = " WHERE ";
		
		StringBuffer clausulaWhere = new StringBuffer(where);

		List<String> listaPk = SigmaDBReflectionUtil.pegaColunasPK(objeto.getClass());
		
		if (SigmaDBUtil.isNullOrEmpty(listaPk) && SigmaDBUtil.isNullOrEmpty(filtros)) {
			throw new SigmaDBException("Não é possível realizar UPDATE ou DELETE sem que esteja informada uma PK ou um filtro válido para a cláusula WHERE.");
		}

		for (Iterator iterator = listaPk.iterator(); iterator.hasNext();) {
			String atributo = (String) iterator.next();

			String valorFormatado = this.pegaValorAtributoFormatado(objeto,
					atributo);

			clausulaWhere.append(atributo.toUpperCase() + " = " + valorFormatado);

			if (iterator.hasNext()) {
				clausulaWhere.append(" AND ");
			}
		}
		
		if (!SigmaDBUtil.isNullOrEmpty(filtros)){
			
			if (!SigmaDBUtil.isNullOrEmpty(listaPk)) {
				clausulaWhere.append(" AND ");
			}
			
			for (Iterator iterator = filtros.iterator(); iterator.hasNext();) {
				Filter filter = (Filter) iterator.next();
				
				clausulaWhere.append(filter.getSQL());
				
				if (iterator.hasNext()) {
					clausulaWhere.append(" AND ");
				}
				
			}
			
		}

		if (clausulaWhere.toString().equals(where)){
			throw new SigmaDBException("Não é possível realizar UPDATE ou DELETE sem que esteja informada uma PK.");
		}
		
		return clausulaWhere.toString();
	}

	/**
	 * Formata uma string que representa o valor de um atributo que deverá ser
	 * persistido no banco de Dados.
	 * 
	 * @param objeto
	 *            Objeto que contem os valores que serão persistidos no Banco de
	 *            Dados.
	 * @param nomeAtributo
	 *            Nome do atributo do objeto que deverá ter o valor formatado.
	 * @return String contendo o valor formatado para ser persistido no banco de
	 *         dados.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String pegaValorAtributoFormatado(Object objeto, String nomeAtributo)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String retorno = "";

		if (!SigmaDBUtil.isNullOrEmpty(nomeAtributo)) {

			String nomeMetodoGet = SigmaDBReflectionUtil
					.getNomeMetodoGet(nomeAtributo);

			Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(objeto,
					nomeMetodoGet);
			
			if (tipo == null) {
				nomeMetodoGet = SigmaDBReflectionUtil.getNomeMetodoIs(nomeAtributo);
				tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(objeto,
						nomeMetodoGet);
			}
			

			boolean ehNumerico = tipo.equals(Double.class)
					|| tipo.equals(Float.class)
					|| tipo.equals(Integer.class)
					|| tipo.equals(Long.class)
					|| tipo.equals(Byte.class)
					|| tipo.equals(int.class)
					|| tipo.equals(double.class)
					|| tipo.equals(long.class)
					|| tipo.equals(byte.class)
					|| tipo.equals(float.class);

			boolean aplicaAspasSimples = tipo.equals(String.class) || 
										 tipo.equals(java.sql.Timestamp.class) ||
										 tipo.equals(java.sql.Date.class)||
										 tipo.equals(java.sql.Time.class);

			Object valor = SigmaDBReflectionUtil.pegaValorDoMetodoGet(objeto,
					nomeMetodoGet);

			if (valor != null) {
				if (aplicaAspasSimples) {

					retorno = SigmaDBUtil.aspasSimples(String.valueOf(valor));

				} else if (ehNumerico) {

					retorno = String.valueOf(valor);

				} else if (tipo.equals(boolean.class) || tipo.equals(Boolean.class)) {

					retorno =String.valueOf(valor);

				} 
				
			} else {
				retorno = "NULL";
			}
		}

		return retorno;
	}
}
