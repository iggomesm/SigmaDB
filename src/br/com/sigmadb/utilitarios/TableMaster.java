package br.com.sigmadb.utilitarios;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import br.com.sigmadb.annotations.PKTableMaster;
import br.com.sigmadb.exceptions.SigmaDBException;

public abstract class TableMaster {
	
	/**
	 * Insere um id na Tabela.
	 * @param id Id da tabela.
	 * @throws Exception
	 */
	public abstract void setId(int id) throws Exception;
	
	/**
	 * Montar� a sint�xe sql de insert de uma tabela.
	 * 
	 * @return String contendo a sint�xe slq que ser� enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public String toInsert() throws Exception {
		return toInsert(this);
	}

	/**
	 * Montar� a sint�xe sql de upadte de uma tabela.
	 * 
	 * @return String contendo a sint�xe slq que ser� enviada ao banco.
	 */
	public String toUpdate() throws Exception {
		return toUpdate(this);
	}

	/**
	 * Montar� a sint�xe sql de delete de uma tabela.
	 * 
	 * @return String contendo a sint�xe slq que ser� enviada ao banco.
	 */
	public String toDelete() throws Exception {
		return toDelete(this);
	}

	/**
	 * Montar� a sint�xe sql de insert de uma tabela.
	 * 
	 * @return String contendo a sint�xe sql que ser� enviada ao banco.
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
	 * Montar� a sint�xe sql de update de uma tabela.
	 * 
	 * @return String contendo a sint�xe sql que ser� enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws RestricaoException 
	 */
	protected String toUpdate(Object objeto) throws Exception {

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

		String where = this.montaClausulaWhere(objeto);

		sql.append(where);

		return sql.toString();
	}

	/**
	 * Montar� a sint�xe sql de delete de uma tabela.
	 * 
	 * @return String contendo a sint�xe sql que ser� enviada ao banco.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws RestricaoException 
	 */
	protected String toDelete(Object objeto) throws Exception {

		String nomeTabela = SigmaDBUtil.pegaNomeTabela(objeto);

		StringBuffer sql = new StringBuffer("DELETE FROM "
				+ nomeTabela.toUpperCase());

		String where = this.montaClausulaWhere(objeto);

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
	 * @return Cláusula where montada.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws RestricaoException 
	 */
	private String montaClausulaWhere(Object objeto)
			throws Exception {

		final String where = " WHERE ";
		
		StringBuffer clausulaWhere = new StringBuffer(where);

		List<String> listaPk = SigmaDBReflectionUtil.pegaColunasPK(objeto.getClass());

		for (Iterator iterator = listaPk.iterator(); iterator.hasNext();) {
			String atributo = (String) iterator.next();

			String valorFormatado = this.pegaValorAtributoFormatado(objeto,
					atributo);

			clausulaWhere.append(atributo.toUpperCase() + " = " + valorFormatado);

			if (iterator.hasNext()) {
				clausulaWhere.append(" AND ");
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

			boolean ehNumerico = tipo.getName().equalsIgnoreCase(
					Double.class.getName())
					|| tipo.getName().equalsIgnoreCase(Float.class.getName())
					|| tipo.getName().equalsIgnoreCase(Integer.class.getName())
					|| tipo.getName().equalsIgnoreCase(Long.class.getName())
					|| tipo.getName().equalsIgnoreCase(Byte.class.getName())
					|| tipo.getName().equalsIgnoreCase(int.class.getName())
					|| tipo.getName().equalsIgnoreCase(double.class.getName())
					|| tipo.getName().equalsIgnoreCase(long.class.getName())
					|| tipo.getName().equalsIgnoreCase(byte.class.getName())
					|| tipo.getName().equalsIgnoreCase(float.class.getName());

			boolean aplicaAspasSimples = tipo.getName().equalsIgnoreCase(
					String.class.getName())
					|| tipo.getName().equalsIgnoreCase(
							java.sql.Timestamp.class.getName());

			Object valor = SigmaDBReflectionUtil.pegaValorDoMetodoGet(objeto,
					nomeMetodoGet);

			if (valor != null) {
				if (aplicaAspasSimples) {

					retorno = SigmaDBUtil.aspasSimples(String.valueOf(valor));

				} else if (ehNumerico) {

					retorno = String.valueOf(valor);

				} else if (tipo.getName().equalsIgnoreCase(
						java.sql.Date.class.getName())) {

					retorno = valor.toString();

				} else if (tipo.getName().equalsIgnoreCase(
						java.sql.Time.class.getName())) {

					retorno = valor.toString();
				}
			} else {
				retorno = "NULL";
			}
		}

		return retorno;
	}
}
