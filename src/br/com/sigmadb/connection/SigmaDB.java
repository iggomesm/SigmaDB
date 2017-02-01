package br.com.sigmadb.connection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;

import br.com.sigmadb.beans.utilitarios.CommandQuery;
import br.com.sigmadb.beans.utilitarios.CommandQuery.CommandNull;
import br.com.sigmadb.beans.utilitarios.CommandQuery.Periodo;
import br.com.sigmadb.beans.utilitarios.CommandQuery.RestricaoMaiorMenor;
import br.com.sigmadb.beans.utilitarios.CommandSqlIN;
import br.com.sigmadb.beans.utilitarios.Filtro;
import br.com.sigmadb.beans.utilitarios.Ilog;
import br.com.sigmadb.beans.utilitarios.Ordenacao;
import br.com.sigmadb.enumerations.EnumOperacaoBD;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.utilitarios.ConnectionLog;
import br.com.sigmadb.utilitarios.ReflectionUtil;
import br.com.sigmadb.utilitarios.TableMaster;
import br.com.sigmadb.utilitarios.Util;

public class SigmaDB {

	/**
	 * Cria uma nova instância de conexão com o Banco. Ou seja, abre uma
	 * transação com o Banco.
	 * 
	 * @param idUsuario
	 *            Id da tabela pessoa que referencia uma pessoa no sistema.
	 * @param origem
	 *            Nome do local de origem por onde os dados da transação serão
	 *            gravados.
	 * @return Conexão aberta com o banco.
	 * @throws Exception
	 */
	public ConnectionLog abrirConexaoPersistencia(int idUsuario, String origem)
			throws Exception {

		DataBase dataBase = DataBase.getSingleton();

		Connection con = dataBase.getConnection();

		ConnectionLog connectionLog = new ConnectionLog(con, idUsuario, origem, true);

		return connectionLog;
	}
	
	/**
	 * Cria uma nova instância de conexão com o Banco. Ou seja, abre uma
	 * transação com o Banco.
	 * @return Conexão aberta com o banco.
	 * @throws Exception
	 */
	public ConnectionLog abrirConexaoPersistencia()	throws Exception {

		DataBase dataBase = DataBase.getSingleton();

		Connection con = dataBase.getConnection();

		ConnectionLog connectionLog = new ConnectionLog(con, -1, "", false);

		return connectionLog;
	}

	/**
	 * Cria uma nova instância de conexão com o Banco. Ou seja, abre uma
	 * transação com o Banco. Este tipo de transação servirá apenas para
	 * consultas.
	 * 
	 * @return Conexão aberta com o banco.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws SigmaDBException
	 */
	public ConnectionLog abreConexaoConsulta() throws ClassNotFoundException,
			SQLException, SigmaDBException {

		DataBase dataBase = DataBase.getSingleton();

		Connection con = dataBase.getConnection();

		ConnectionLog connectionLog = new ConnectionLog(con);

		return connectionLog;
	}

	/**
	 * Realiza o commit das operações realizadas na transação, salvando todos os
	 * logs criados.
	 * 
	 * @param connectionLog
	 *            Conexão aberta que deverá ser commitada.
	 * @throws Exception
	 */
	public void concluirConexao(ConnectionLog connectionLog) throws Exception {

		if (connectionLog != null && connectionLog.getConnection() != null) {

			if (connectionLog.isGeraEstruturaIlog()) {
				
				for (Ilog ilog : connectionLog.getListaLogs()) {
					this.applyUpdateTableMaster(ilog, connectionLog,
							EnumOperacaoBD.INSERT);
				}
			}

			DataBase dataBase = DataBase.getSingleton();
			dataBase.grava(connectionLog.getConnection());
		}
	}

	/**
	 * Realiza o rollBack na transação, descartando todos os logs criados.
	 * 
	 * @param connectionLog
	 *            Conexão que deverá receber o rollBack
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void abortarConexao(ConnectionLog connectionLog)
			throws ClassNotFoundException, SQLException {
		if (connectionLog != null && connectionLog.getConnection() != null) {
			DataBase dataBase = DataBase.getSingleton();
			dataBase.rollBack(connectionLog.getConnection());
			connectionLog.getListaLogs().clear();
		}
	}

	/**
	 * Aplica a chamada do método close na conexão, caso ela não seja nula, e
	 * limpa a lista de lgos.
	 * 
	 * @param connectionLog
	 *            Conexão que será fechada.
	 * @throws SQLException
	 */
	public void fecharConexao(ConnectionLog connectionLog) throws SQLException {
		if (connectionLog != null && connectionLog.getConnection() != null) {
			connectionLog.getConnection().close();
			connectionLog.getListaLogs().clear();
		}
	}

	/**
	 * Monta a sintaxe sql de persistência de dados e a executa.
	 * 
	 * @param tableMaster
	 *            Objeto que será persistido.
	 * @param connectionLog
	 *            Objeto de conexão, com log, aberta com o banco.
	 * @param operacao
	 *            Tipo de operacação que será realizada.
	 * @return 1 se houve alteração, -1 se não houve.
	 * @throws Exception
	 */
	public void applyUpdateTableMaster(TableMaster tableMaster,
			ConnectionLog connectionLog, EnumOperacaoBD operacao)
			throws Exception {

		boolean ehTabelaIlog = tableMaster instanceof Ilog;

		if (!ehTabelaIlog) {
			connectionLog.validaConexaoPersistencia();
		}

		String sql = null;

		if (operacao == EnumOperacaoBD.INSERT) {
			sql = tableMaster.toInsert();
		} else if (operacao == EnumOperacaoBD.UPDATE) {
			sql = tableMaster.toUpdate();
		} else if (operacao == EnumOperacaoBD.DELETE) {
			sql = tableMaster.toDelete();
		} else {
			throw new SigmaDBException(
					"MODO DE OPERAÇÃO DO BANCO INVÁLIDO NO MOMENTO DE ATUALIZAR.");
		}

		int idRegistro = DataBase.applyUpdates(sql,
				connectionLog.getConnection());

		tableMaster.setId(idRegistro);

		if (!ehTabelaIlog) {
			this.montaIlog(tableMaster, connectionLog, operacao);
		}
	}

	/**
	 * Monta um registro da tabela Ilog e o insere no objeto connectionLog.
	 * 
	 * @param tableMaster
	 *            Objeto que será persistido no banco.
	 * @param connectionLog
	 *            Objeto de conexão com o banco.
	 * @param operacao
	 *            Tipo de operação que será persistida.
	 * @throws Exception
	 */
	private void montaIlog(TableMaster tableMaster,
			ConnectionLog connectionLog, EnumOperacaoBD operacao)
			throws Exception {
		
		if (connectionLog.isGeraEstruturaIlog()) {

			String nomeTabela = Util.pegaNomeTabela(tableMaster);

			String colunaPk = (String) Util.getFirst(ReflectionUtil
					.pegaColunasPK(tableMaster.getClass()));
			int pk_tabela = (Integer) ReflectionUtil.getValorMetodoGet(tableMaster,
					colunaPk);

			Timestamp dataHoraAtual = new Timestamp(new Date().getTime());
			int usuario = connectionLog.getPess_id();
			String origem = connectionLog.getOrigem();

			ObjectMapper mapper = new ObjectMapper();
			String valores = mapper.writeValueAsString(tableMaster);

			Ilog ilog = new Ilog();
			ilog.setIlog_versao(connectionLog.getVersao());
			ilog.setIlog_tabela(nomeTabela);
			ilog.setIlog_pk_tabela(pk_tabela);
			ilog.setIlog_data_hora(dataHoraAtual);
			ilog.setIlog_usuario(usuario);
			ilog.setIlog_origem(origem);
			ilog.setIlog_valores(valores);
			ilog.setIlog_tipo(operacao.getValue());

			connectionLog.getListaLogs().add(ilog);
		}
	}

	/**
	 * Realiza uma consulta ao banco tomando como base para restrição nos
	 * parâmetros atributos do objeto vo, where e command.
	 * 
	 * @param bean
	 *            VO com os campos preenchidos para cláusulas where
	 * @param command
	 *            Objeto contendo restrições complementares a consulta.
	 * @see {@link CommandQuery}
	 * @return Lista de objetos do tipo do parâmetro vo contendo o resultado da
	 *         consulta.
	 * @throws Exception
	 */
	public <E> List<E> pesquisaTabela(E bean, CommandQuery command) throws Exception {

		Class<?> classe = bean.getClass();

		String name = classe.getSimpleName();
		
		List<E> resultadoConsulta = null;

		if (ReflectionUtil.isGroupTableMaster(bean)) {

			if (command == null) {
				throw new SigmaDBException(
						"Não é possível consultar Beans, que agrupam mais de uma tabela, sem que o CommandQuery seja informado.");
			}

			if (command != null && Util.isNullOrEmpty(command.getTabelaFrom())) {
				throw new SigmaDBException(
						"Não é possível consultar Beans, que agrupam mais de uma tabela, sem que a propriedade \"tabelaFrom\" da classe CommandQuery tenha sido informada.");
			}

			String sqlConsulta = command.getTabelaFrom();

			resultadoConsulta = pesquisaTabela(sqlConsulta, bean, command);

		} else {
			
			String sqlConsulta = command != null ? command.getTabelaFrom() : null;
			
			sqlConsulta =  Util.isNullOrEmpty(sqlConsulta) ? name.toLowerCase() : sqlConsulta; 
			
			resultadoConsulta = this.pesquisaTabela(sqlConsulta, bean, command);
		}
		
		return resultadoConsulta;
	}

	/**
	 * Realiza uma consulta ao banco tomando como base para restrição nos
	 * parâmetros atributos do objeto vo, where e command.
	 * 
	 * @param sqlConsulta
	 *            Sintaxe sql da consulta sem o conteúdo "select * from". Ou
	 *            seja, iniciada pelo nome da tabela seguida dos seus joins.
	 * @param bean
	 *            VO com os campos preenchidos para cláusulas where *
	 * @param command
	 *            Objeto contendo restrições complementares a consulta.
	 * @see {@link CommandQuery}
	 * @return Lista de objetos do tipo do parâmetro vo contendo o resultado da
	 *         consulta.
	 * @throws Exception
	 */
	protected <E> List<E> pesquisaTabela(String sqlConsulta, E bean,
			CommandQuery command) throws Exception {
		//return this.pesquisaTabela(sqlConsulta, bean, null, command);
		ConnectionLog connection = command.getConnectionLog();
		
		boolean abreConexao = connection == null ||
							  connection.getConnection() == null;
		
		connection = abreConexao ? this.abreConexaoConsulta() : connection;

		try {
			
			return pesquisaTabela(connection, bean, sqlConsulta, command);
		} finally {
			this.fecharConexao(connection);
		}
	}	

	/**
	 * Realiza toda a lógica para realização de consulta ao banco de dados.
	 * @param conn Objeto de conexão aberta com o Banco.
	 * @param bean Objeto contendo as restrições da consulta.
	 * @param sqlConsulta String contendo o sql da consulta a ser enviado para o banco.
	 * @param command  Objeto contendo restrições complementares a consulta.
	 * @see {@link CommandQuery}
	 * @return Lista de objetos do tipo do parâmetro Bean, contendo o resultado da consulta.
	 * @throws Exception
	 */
	private <E> List<E> pesquisaTabela(ConnectionLog conn, E bean, String sqlConsulta, CommandQuery command)
			throws Exception {
		
		String[] atributosExclusaoConsulta = command != null ? command
				.getAtributosExclusaoConsulta() : new String[0];

		Map propriedades = gerarMapaPropriedades(atributosExclusaoConsulta, bean);

		StringBuffer query = null;
		
		String where = "";

		if (command != null) {

			List<String> listaJoins = command.getListaRestricaoJoin();
			for (String join : listaJoins) {
				sqlConsulta += join;
				sqlConsulta += " ";
			}
		}

		where += this.preparaRestricaoCommandConsulta(command);		

		if (Util.isNullOrEmpty(sqlConsulta)) {
			query = new StringBuffer("SELECT * FROM " + Util.pegaNomeTabela(bean));
		} else if (!sqlConsulta.toLowerCase().contains("select")){
			query = new StringBuffer("SELECT * FROM " + sqlConsulta);
		} else {
			query = new StringBuffer(sqlConsulta);
		}

		boolean adiciouRestricao = !Util.isNullOrEmpty(where);

		for (Iterator iter = propriedades.keySet().iterator(); iter.hasNext();) {
			String propriedadeNome = (String) iter.next();

			boolean diferenteNulo = verificaAtributoValorNulo(propriedadeNome, propriedades, bean); 
			
			if ((propriedadeNome != null)
					&& !propriedadeNome.equalsIgnoreCase("class")
					&& (propriedades.get(propriedadeNome) != null)
					&& diferenteNulo) {

				String valorFormatado = TableMaster.pegaValorAtributoFormatado(
						bean, propriedadeNome);
				if (adiciouRestricao) {
					where += " AND ";
				}
				where += propriedadeNome + " = " + valorFormatado;
				adiciouRestricao = true;
			}
		}

		if (!Util.isNullOrEmpty(where)) {
			query.append(" WHERE ");
			query.append(where);
			query.append(" ");
		}

		ResultSet resultSet = DataBase.executeQuery(query.toString(),
				conn.getConnection());
		List retorno = preencherResultSet(resultSet, propriedades, bean);
		resultSet.close();

		return retorno;
	}
	
	/**
	 * Converte o resultado de uma consulta, que está contido num {@link ResultSet} numa lista de objetos do mesmo tipo informado como parâmetro de filtro para consulta.
	 * @param resultSet Objeto contendo o resultado da consulta.
	 * @param propriedades Mapa contendo todas as propriedades que deverão ser preenchidas.
	 * @param bean Instância de objeto que representará o tipo de elementos que irão compor a lista de retorno.
	 * @return Lista de objetos do mesmo tipo da instância informada como parâmetro representando o resultado da consulta.
	 * @throws Exception
	 */
	protected List preencherResultSet(ResultSet resultSet, Map propriedades,
			Object bean) throws Exception {
		
		List resultado = new ArrayList();
		
		List<String> colunasResultSet = this.listaColunasResultSet(resultSet);

		while (resultSet.next()) {

			for (Iterator iter = propriedades.keySet().iterator(); iter
					.hasNext();) {
				
				String propriedadeNome = (String) iter.next();

				if ((propriedadeNome != null)
						&& !propriedadeNome.equalsIgnoreCase("class")
						&& colunasResultSet.contains(propriedadeNome.toLowerCase())) {
					
					String propriedadeTipo = PropertyUtils.getPropertyType(bean,
							propriedadeNome).getName();
					
					Object valor = extraiValorResultSet(resultSet, propriedadeTipo,
							propriedadeNome);
					
					propriedades.put(propriedadeNome, valor);
				}
			}

			Class classeBean = bean.getClass();
			Object instancia = classeBean.newInstance();
			BeanUtils.populate(instancia, propriedades);
			resultado.add(instancia);
		}

		return resultado;
	}
	
	/**
	 * Lista quais são as colunas existentes num {@link ResultSet}
	 * @param rs Objeto contendo as colunas que deverão ser listadas.
	 * @return Lista de Strings contendo o nome de cada coluna contida no {@link ResultSet}.
	 * @throws SQLException
	 */
	private List<String> listaColunasResultSet(ResultSet rs) throws SQLException {
		
		List<String> colunasResultSet = new ArrayList<String>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
	    
	    for (int x = 1; x <= rsmd.getColumnCount(); x++) {
	    	colunasResultSet.add(rsmd.getColumnName(x).toLowerCase());
	    }
		
		return colunasResultSet;
	}
	

	
	/**
	 * Pega o valor contido no {@link ResultSet} para determinado atributo, com o mesmo nome da coluna do {@link ResultSet}.
 	 * @param resultSet Objeto que contém o resultado da consulta.
	 * @param datatype Tipo do dado que deverá ser extraído do {@link ResultSet}
	 * @param name Nome da coluna do {@link ResultSet}
	 * @return Valor contido no {@link ResultSet} que são respectivos ao tipo e nome informados no parâmetro.
	 * @throws Exception
	 */
	protected static Object extraiValorResultSet(ResultSet resultSet, String datatype, String name)
			throws Exception {
		if (datatype.equals("java.lang.String") || datatype.equals("String")) {
			return resultSet.getString(name);
		} else if (datatype.equals("java.sql.Date")) {
			return resultSet.getDate(name);
		} else if (datatype.equals("java.sql.Timestamp")) {
			return resultSet.getTimestamp(name);
		} else if (datatype.equals("java.sql.Time")) {
			return resultSet.getTime(name);
		} else if (datatype.equals("double")) {
			return new Double(resultSet.getDouble(name));
		} else if (datatype.equals("int")) {
			return new Integer(resultSet.getInt(name));
		} else if (datatype.equals("long")) {
			return new Long(resultSet.getLong(name));
		} else if (datatype.equals("java.lang.Double")) {
			return new Double(resultSet.getDouble(name));
		} else if (datatype.equals("java.lang.Integer")) {
			return new Integer(resultSet.getInt(name));
		} else if (datatype.equals("java.lang.Long")) {
			return new Long(resultSet.getLong(name));
		} else if (datatype.equals("java.lang.Float")) {
			return new Float(resultSet.getFloat(name));
		} else if (datatype.equals("java.lang.Short")) {
			return new Short(resultSet.getShort(name));
		} else if (datatype.equals("java.lang.Byte")) {
			return new Byte(resultSet.getByte(name));
		} else if (datatype.equals("java.math.BigDecimal")) {
			return resultSet.getBigDecimal(name);
		} else if (datatype.equals("java.io.InputStream")) {
			return resultSet.getAsciiStream(name);
		} else if (datatype.equals("boolean")) {
			return new Boolean(resultSet.getBoolean(name));
		} else if (datatype.equals("java.sql.Array")) {
			return resultSet.getArray(name);
		} else if (datatype.equals("java.sql.Blob")) {
			return resultSet.getBlob(name);
		} else if (datatype.equals("java.sql.Clob")) {
			return resultSet.getClob(name);
		} else {
			return resultSet.getObject(name);
		}
	}

	
	/**
	 * Verifica se o valor do atributo é nulo ou zero.
	 * @param nomeAtributo Nome do atributo a ser verificado.
	 * @param propriedades Mapa contendo todos os atributos do objeto com seus respectivos valores.
	 * @param bean Instância do objeto Bean a ser verificado.
	 * @return False caso o atributo seja nulo ou zerado. True caso possua algum valor.
	 * @throws Exception
	 */
	private boolean verificaAtributoValorNulo(String nomeAtributo, Map propriedades,
			Object bean) throws Exception {
		String propriedadeTipo = PropertyUtils.getPropertyType(bean,
				nomeAtributo).getName();

		if ("int".equals(propriedadeTipo)
				&& "0".equals(propriedades.get(nomeAtributo))) {
			return false;
		}

		if ("double".equals(propriedadeTipo)
				&& "0.0".equals(propriedades.get(nomeAtributo))) {
			return false;
		}

		if ("java.lang.String".equals(propriedadeTipo)
				&& ("".equals(propriedades.get(nomeAtributo)) || "null"
						.equalsIgnoreCase(String.valueOf(propriedades
								.get(nomeAtributo))))) {
			return false;
		}

		if ("long".equals(propriedadeTipo)
				&& "0".equals(propriedades.get(nomeAtributo))) {
			return false;
		}

		if ("java.sql.Timestamp".equals(propriedadeTipo)
				&& "null".equalsIgnoreCase(String.valueOf(propriedades
						.get(nomeAtributo)))) {
			return false;
		}

		return true;
	}

	protected Map gerarMapaPropriedades(String[] propriedadesExcluidas,
			Object vo) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Map propriedades = new HashMap();

		List<String> listaAtributos = ReflectionUtil
				.listaNomeDosAtributosDoObjetoVO(vo);

		for (String atributo : listaAtributos) {
			Object valor = ReflectionUtil.getValorMetodoGet(vo, atributo);
			propriedades.put(atributo, String.valueOf(valor));
		}
		List remover = new ArrayList();

		for (Iterator iter = propriedades.keySet().iterator(); iter.hasNext();) {
			String propName = (String) iter.next();

			if (propriedadesExcluidas != null) {
				for (int i = 0; i < propriedadesExcluidas.length; i++) {
					if (propName.equalsIgnoreCase(propriedadesExcluidas[i])) {
						remover.add(propName);
					}
				}
			}
		}

		propriedades.keySet().removeAll(remover);

		return propriedades;
	}

	/**
	 * Monta uma restri��o do tipo IN para uma determinada coluna.
	 * 
	 * @param restricao
	 *            Objeto que conter� o nome de uma coluna da consulta e um
	 *            conjunto de Ids onde ser� aplicada a restri��o IN para a
	 *            coluna.
	 * @return String contendo a cl�usula IN. Caso n�o seja informado uma
	 *         restri��o, ou n�o seja informado um nome de propriedade na
	 *         restri��o, ou n�o sejam informados Ids na restri��o, ser�
	 *         retornada uma String em branco.
	 * @see {@link CommandSqlIN}
	 */
	protected String whereIn(CommandSqlIN restricao) {

		String where = "";

		if (restricao != null) {

			boolean aplicaFiltro = restricao.getArrayIds() != null
					&& restricao.getArrayIds().length > 0
					&& !Util.isNullOrEmpty(restricao.getNomePropriedade());

			if (aplicaFiltro) {
				where += restricao.getNomePropriedade() + " "
						+ whereIn(restricao.getArrayIds(), false);
			}
		}

		return where;
	}

	/**
	 * M�TODO QUE TEM COMO FUNCAO MONTAR UM CLAUSULA IN PA DETERMINADA PESQUISA
	 * DE CAMPO
	 * 
	 * @param String
	 *            [] valores - Valores a serem preenchidos na clausula in Ex:
	 *            String [] valores = {"10","12"} == in(10, 12)
	 * @param aspasSimples
	 *            - Utilizado para colocar aspas quando for fazer a pesquisa de
	 *            valores que sao caractere Ex: String [] valores = {"10","12"}
	 *            == in('10', '12') - true coloca aspas, false n�o coloca aspas
	 */
	protected String whereIn(String[] valores, boolean aspasSimples) {
		if (valores.length == 0) {
			return "";
		}

		StringBuffer retorno = new StringBuffer();

		for (int i = 0; i < valores.length; i++) {
			String valor = valores[i];

			if (aspasSimples) {
				valor = Util.aspasSimples(valor);
			}

			if (i < valores.length - 1) {
				valor += ", ";
			}

			retorno.append(valor);
		}

		return "in (" + retorno.toString() + ")";
	}

	/**
	 * Agrupa numa única String todas as restrições da consulta definidas no
	 * objeto {@link CommandQuery}.
	 * 
	 * @param commandConsultaVO
	 *            Objeto que agrupa restrições que deverão ser inseridas na
	 *            consulta.
	 * @return String sql contendo todas as restrições que foram inseridas no
	 *         objeto do parâmetro.<br>
	 *         <b>ATENÇÃO:Caso nenhuma restrição tenha sido inserida no objeto,
	 *         será retornada uma String em branco.</b>
	 */
	private String preparaRestricaoCommandConsulta(
			CommandQuery commandConsultaVO) {

		StringBuilder where = new StringBuilder("");

		if (commandConsultaVO != null) {

			boolean adiciouClausula = false;

			boolean adicionaVirgula = false;

			List<Periodo> listaRestricoesPeriodo = this
					.montaListaRestricaoPeriodos(commandConsultaVO);

			for (Periodo restricaoPeriodo : listaRestricoesPeriodo) {
				where.append(" and ");
				where.append(restricaoPeriodo.toString());
				adiciouClausula = true;
			}

			List<CommandSqlIN> listaRestricaoIN = commandConsultaVO
					.getListaRestricoesIN();

			for (CommandSqlIN restricaoSqlIN : listaRestricaoIN) {
				where.append(this.whereIn(restricaoSqlIN));
			}

			List<CommandNull> listaRestricaoNull = commandConsultaVO
					.getListaRestricaoNull();

			String restricaoNullQuery = montaRestricaoNullcommandConsulta(commandConsultaVO);
			where.append(restricaoNullQuery);

			String restricaoOR = this
					.montaRestricaoORcommandConsulta(commandConsultaVO);
			where.append(restricaoOR);

			String restricaoIgualdade = this
					.montaRestricaoIgualdadecommandConsulta(commandConsultaVO);
			where.append(restricaoIgualdade);

			String restricaoDiferenca = this
					.montaRestricaoDiferencacommandConsulta(commandConsultaVO);
			where.append(restricaoDiferenca);

			String restricaoLike = this
					.montaRestricaoLikecommandConsulta(commandConsultaVO);
			where.append(restricaoLike);

			String restricaoMaiorMenor = this
					.montaRestricaoMaiorMenorcommandConsulta(commandConsultaVO);
			where.append(restricaoMaiorMenor);

			List<Ordenacao> orderBy = commandConsultaVO.getListaOrderBy();

			if (!Util.isNullOrEmpty(orderBy)) {
				where.append(" order by ");
				for (Ordenacao ordenacaoVO : orderBy) {
					if (adicionaVirgula) {
						where.append(", ");
					}
					where.append(ordenacaoVO.getCampo() + " ");
					where.append(ordenacaoVO.getTipoOrdenacao().name());
					adicionaVirgula = true;
				}

			}
		}
		return where.toString();
	}

	/**
	 * Monta uma lista com as restriÁıes de perÌodo que est„o contidas no objeto
	 * do par‚metro.
	 * 
	 * @param commandWhereVO
	 *            Objeto que agrupa restriÁıes do tipo perÌodo.
	 * @return Lista com as restriÁıes de perÌodo que est„o contidas no objeto
	 *         do par‚metro.
	 */
	private List montaListaRestricaoPeriodos(CommandQuery commandWhereVO) {

		List<Periodo> listaRestricoesPeriodo = new ArrayList<Periodo>();

		Map<String, Periodo> mapaRestricaoPeriodos = commandWhereVO
				.getMapaRestricoesPeriodo();

		Set<String> campos = mapaRestricaoPeriodos.keySet();

		for (String nomeColuna : campos) {

			Periodo restricaoPeriodo = mapaRestricaoPeriodos.get(nomeColuna);

			listaRestricoesPeriodo.add(restricaoPeriodo);
		}

		return listaRestricoesPeriodo;
	}

	/**
	 * Inclui na consulta as restrições OR contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @return String contendo a restrição do tipo OR montada entre parênteses
	 *         iniciada com um and. Ex. and (pess_id = 1 OR pess_id = 2 OR ...).
	 *         Caso a lista de restrição OR esteja vazia o método retornará uma
	 *         String em branco.
	 */
	private String montaRestricaoORcommandConsulta(CommandQuery command) {

		String restricaoOR = "";
		List<Filtro> listaRestricaoOR = command.getListaRestricaoOR();

		if (!Util.isNullOrEmpty(listaRestricaoOR)) {
			restricaoOR += " and ";
			restricaoOR += "(";
			for (Filtro filtroVO : listaRestricaoOR) {
				restricaoOR += filtroVO.getNomePropriedade() + " = "
						+ filtroVO.getValorPropriedade() + " OR ";
			}
			restricaoOR = restricaoOR.substring(0, restricaoOR.length() - 4);
			restricaoOR += ") ";
		}

		return restricaoOR;
	}

	/**
	 * Inclui na consulta as restrições OR contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @return String contendo a restrição do tipo OR montada entre parênteses
	 *         iniciada com um and. Ex. and (loag_documento is not null OR
	 *         lote_documento is not null ...). Caso a lista de restrição OR
	 *         esteja vazia o método retornará uma String em branco.
	 */
	private String montaRestricaoNullcommandConsulta(CommandQuery command) {

		String restricaoNullQuery = "";
		List<CommandNull> listaRestricaoNull = command.getListaRestricaoNull();

		if (!Util.isNullOrEmpty(listaRestricaoNull)) {
			restricaoNullQuery += " and ";
			restricaoNullQuery += "(";
			for (CommandNull restricaoNull : listaRestricaoNull) {
				restricaoNullQuery += restricaoNull.toString();
			}
			restricaoNullQuery = restricaoNullQuery.substring(0,
					restricaoNullQuery.length() - 4);
			restricaoNullQuery += ") ";
		}

		return restricaoNullQuery;
	}

	/**
	 * Inclui na consulta as restrições de diferença contidas no objeto de
	 * command da consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @return String contendo a restrições de diferença montada um and. Ex. and
	 *         pess_id <> 1 and pess_id <> 2 and ... Caso a lista de restrição
	 *         de diferenças esteja vazia, o método retornará uma String em
	 *         branco.
	 */
	private String montaRestricaoDiferencacommandConsulta(CommandQuery command) {

		String restricaoOR = "";
		List<Filtro> listaRestricaoDiferenca = command
				.getListaRestricaoDiferenca();

		if (!Util.isNullOrEmpty(listaRestricaoDiferenca)) {
			restricaoOR += " and ";

			for (Filtro filtroVO : listaRestricaoDiferenca) {
				restricaoOR += filtroVO.getNomePropriedade() + " <> "
						+ filtroVO.getValorPropriedade() + " and ";
			}
			restricaoOR = restricaoOR.substring(0, restricaoOR.length() - 5);

		}

		return restricaoOR;
	}

	/**
	 * Inclui na consulta as restrições de igualdade contidas no objeto de
	 * command da consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @return String contendo a restrições de igualdade montada um and. Ex. and
	 *         pess_id = 1 and pess_id = 2 and ... Caso a lista de restrição de
	 *         diferenças esteja vazia, o método retornará uma String em branco.
	 */
	private String montaRestricaoIgualdadecommandConsulta(CommandQuery command) {

		String restricaoIgualdade = "";
		List<Filtro> listaRestricaoIgualdade = command
				.getListaRestricaoIgualdade();

		if (!Util.isNullOrEmpty(listaRestricaoIgualdade)) {
			restricaoIgualdade += " and ";

			for (Filtro filtroVO : listaRestricaoIgualdade) {
				restricaoIgualdade += filtroVO.getNomePropriedade() + " = "
						+ filtroVO.getValorPropriedade() + " and ";
			}
			restricaoIgualdade = restricaoIgualdade.substring(0,
					restricaoIgualdade.length() - 5);

		}

		return restricaoIgualdade;
	}

	/**
	 * Inclui na consulta as restrições de LIKE contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição LIKE que deverá ser
	 *            aplicada a consulta.
	 * @return String contendo a restrições de LIKE montada um and. Ex. and
	 *         pess_nome LIKE 'T1' and pess_nome LIKE 'T2'and ... Caso a lista
	 *         de restrição de LIKE esteja vazia, o método retornará uma String
	 *         em branco.
	 */
	private String montaRestricaoLikecommandConsulta(CommandQuery command) {
		String restricaoLike = "";
		List<Filtro> listaRestricaoLike = command.getListaRestricaoLike();

		if (!Util.isNullOrEmpty(listaRestricaoLike)) {
			restricaoLike += " and ";

			for (Filtro filtroVO : listaRestricaoLike) {
				restricaoLike += filtroVO.getNomePropriedade() + " like "
						+ filtroVO.getValorPropriedade() + " and ";
			}
			restricaoLike = restricaoLike.substring(0,
					restricaoLike.length() - 5);

		}

		return restricaoLike;
	}

	/**
	 * Inclui na consulta as restrições de Menor Que/Maior Que/Maior Igual/Menor
	 * Igual contidas no objeto de command da consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição que deverá ser aplicada a
	 *            consulta.
	 * @return String contendo as restrições montada em and. Ex. and a < 2 and p
	 *         > 0 ... Caso a lista esteja vazia, o método retornará uma String
	 *         em branco.
	 */
	private String montaRestricaoMaiorMenorcommandConsulta(CommandQuery command) {
		String restricaoMaiorMenor = "";
		List<RestricaoMaiorMenor> listaRestricaoMaiorMenor = command
				.getListaRestricaoMaiorMenor();

		if (!Util.isNullOrEmpty(listaRestricaoMaiorMenor)) {
			restricaoMaiorMenor += " and ";

			for (RestricaoMaiorMenor restricaoMaiorMenorVO : listaRestricaoMaiorMenor) {
				restricaoMaiorMenor += restricaoMaiorMenorVO.toString()
						+ " and ";
			}
			restricaoMaiorMenor = restricaoMaiorMenor.substring(0,
					restricaoMaiorMenor.length() - 5);

		}

		return restricaoMaiorMenor;
	}

}
