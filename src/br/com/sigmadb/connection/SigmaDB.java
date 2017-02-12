package br.com.sigmadb.connection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import br.com.sigmadb.beans.utilitarios.BeanFilter;
import br.com.sigmadb.beans.utilitarios.ComandoSqlIN;
import br.com.sigmadb.beans.utilitarios.CommandQuery;
import br.com.sigmadb.beans.utilitarios.DBOperationFilter;
import br.com.sigmadb.beans.utilitarios.Ilog;
import br.com.sigmadb.beans.utilitarios.Ordenacao;
import br.com.sigmadb.enumerations.TypeOperation;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.util.ConnectionLog;
import br.com.sigmadb.util.SigmaDBReflectionUtil;
import br.com.sigmadb.util.SigmaDBUtil;
import br.com.sigmadb.util.TableMaster;
import br.com.sigmadb.util.interfaces.DBOperation;
import br.com.sigmadb.util.interfaces.Filter;

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
	private ConnectionLog abreConexaoConsulta() throws ClassNotFoundException,
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

			if (connectionLog.isGeraEstruturaIlog() && Boolean.parseBoolean(DataBase.getUseIlog())) {
				
				for (Ilog ilog : connectionLog.getListaLogs()) {
					this.applyUpdateTableMaster(ilog, connectionLog,
							TypeOperation.INSERT);
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
	 * @see {@link TypeOperation}
	 * @see DBOperationFilter
	 */
	public void applyUpdateTableMaster(TableMaster tableMaster,
			ConnectionLog connectionLog, DBOperation dbOperation)
			throws Exception {

		TypeOperation operacao = dbOperation.getTypeOperation();
		
		boolean ehTabelaIlog = tableMaster instanceof Ilog;

		if (!ehTabelaIlog) {
			connectionLog.validaConexaoPersistencia();
		}

		String sql = null;

		if (operacao == TypeOperation.INSERT) {
			sql = tableMaster.toInsert();
		} else if (operacao == TypeOperation.UPDATE) {
			sql = tableMaster.toUpdate(dbOperation.getFilters());
		} else if (operacao == TypeOperation.DELETE) {
			sql = tableMaster.toDelete(dbOperation.getFilters());
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
			ConnectionLog connectionLog, TypeOperation operacao)
			throws Exception {
		
		if (connectionLog.isGeraEstruturaIlog()) {

			String nomeTabela = SigmaDBUtil.pegaNomeTabela(tableMaster);

			String colunaPk = (String) SigmaDBUtil.getFirst(SigmaDBReflectionUtil
					.pegaColunasPK(tableMaster.getClass()));
			int pk_tabela = (Integer) SigmaDBReflectionUtil.getValorMetodoGet(tableMaster,
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
	 * parâmetros atributos do bean informado.
	 * 
	 * @param bean
	 *            Bean cujo os valores dos seus atributos serão a restrição para a consulta.
	 *            
	 * @return Lista de objetos do tipo do parâmetro bean contendo o resultado da
	 *         consulta.
	 * @throws Exception
	 */
	public <E> List<E> pesquisaTabela(E bean) throws Exception {
		return this.pesquisaTabela(bean, null);
	}
	
	/**
	 * Realiza uma consulta ao banco tomando como base para restrição nos
	 * parâmetros atributos do objeto vo, where e command.
	 * 
	 * @param bean
	 *            Bean cujo os valores dos seus atributos serão a restrição para a consulta.
	 * @param command
	 *            Objeto contendo restrições complementares a consulta.
	 * @see {@link CommandQuery}
	 * @return Lista de objetos do tipo do parâmetro bean contendo o resultado da
	 *         consulta.
	 * @throws Exception
	 */
	public <E> List<E> pesquisaTabela(E bean, CommandQuery command) throws Exception {

		Class<?> classe = bean.getClass();

		String name = classe.getSimpleName();
		
		List<E> resultadoConsulta = null;

		if (SigmaDBReflectionUtil.isGroupTableMaster(bean)) {

			if (command == null) {
				throw new SigmaDBException(
						"Não é possível consultar Beans, que agrupam mais de uma tabela, sem que o CommandQuery seja informado.");
			}

			if (command != null && SigmaDBUtil.isNullOrEmpty(command.getTabelaFrom())) {
				throw new SigmaDBException(
						"Não é possível consultar Beans, que agrupam mais de uma tabela, sem que a propriedade \"tabelaFrom\" da classe CommandQuery tenha sido informada.");
			}

			String sqlConsulta = command.getTabelaFrom();

			resultadoConsulta = pesquisaTabela(sqlConsulta, bean, command);

		} else {
			
			String sqlConsulta = command != null ? command.getTabelaFrom() : null;
			
			sqlConsulta =  SigmaDBUtil.isNullOrEmpty(sqlConsulta) ? name.toLowerCase() : sqlConsulta; 
			
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
	 *            Bean cujo os valores dos seus atributos serão a restrição para a consulta.
	 * @param command
	 *            Objeto contendo restrições complementares a consulta.
	 * @see {@link CommandQuery}
	 * @return Lista de objetos do tipo do parâmetro bean contendo o resultado da
	 *         consulta.
	 * @throws Exception
	 */
	protected <E> List<E> pesquisaTabela(String sqlConsulta, E bean,
			CommandQuery command) throws Exception {
		
		boolean abreConexao = true;
		ConnectionLog connection = null;
		
		if (command != null) {
		
			connection = command.getConnectionLog();
		
			abreConexao = connection == null ||
							  connection.getConnection() == null;
		}
		
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

			List<String> listaJoins = command.getListaJoin();
			for (String join : listaJoins) {
				sqlConsulta += join;
				sqlConsulta += " ";
			}
		}

		where += this.preparaClausulaWhereCommandQuery(command);		

		String colunasRetorno = this.pegaColunasRetornoConsulta(command);
		
		if (SigmaDBUtil.isNullOrEmpty(sqlConsulta)) {
			query = new StringBuffer("SELECT " + colunasRetorno + " FROM " + SigmaDBUtil.pegaNomeTabela(bean));
		} else if (!sqlConsulta.toLowerCase().contains("select")){
			query = new StringBuffer("SELECT " + colunasRetorno + " FROM " + sqlConsulta);
		} else {
			query = new StringBuffer(sqlConsulta);
		}

		boolean adiciouRestricao = !SigmaDBUtil.isNullOrEmpty(where);

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

		if (!SigmaDBUtil.isNullOrEmpty(where)) {
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
	 * Monta em sintaxe sql quais são as colunas que o usuário adicionou no CommandQuery como retorno da consulta.
	 * @param command Objeto contendo restrições complementares a consulta.
	 * @return String contendo as colunas que deverão ser retornadas pela consulta.
	 */
	private String pegaColunasRetornoConsulta(CommandQuery command) {

		String colunasConsulta = "*";
		
		if (command != null) {
		
			List<String> listaColunasRetorno = command.getListaColunasRetorno();

			if (!SigmaDBUtil.isNullOrEmpty(listaColunasRetorno)){

				StringBuffer colunasRetornoConsulta = new StringBuffer();

				for (String nomeColuna : listaColunasRetorno) {

					colunasRetornoConsulta.append(nomeColuna);
					colunasRetornoConsulta.append(", ");
				}

				colunasConsulta = colunasRetornoConsulta.substring(0, colunasRetornoConsulta.length() - 2);
			} 
		}
		
		return colunasConsulta;
	}
	
	/**
	 * Converte o resultado de uma consulta, que está contido num {@link ResultSet} numa lista de objetos do mesmo tipo informado como parâmetro de filtro para consulta.
	 * @param resultSet Objeto contendo o resultado da consulta.
	 * @param propriedades Mapa contendo todas as propriedades que deverão ser preenchidas.
	 * @param bean Instância de objeto que representará o tipo de elementos que irão compor a lista de retorno.
	 * @return Lista de objetos do mesmo tipo da instância informada como parâmetro representando o resultado da consulta.
	 * @throws Exception
	 */
	private List preencherResultSet(ResultSet resultSet, Map<String, Object> propriedades,
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
					
					Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(bean, SigmaDBReflectionUtil.getNomeMetodoGet(propriedadeNome));
					
					if (tipo == null) {
						tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(bean, SigmaDBReflectionUtil.getNomeMetodoIs(propriedadeNome));
					}
					
					Object valor = extraiValorResultSet(resultSet, tipo, propriedadeNome);
					
					propriedades.put(propriedadeNome, valor);
				}
			}

			Class classeBean = bean.getClass();
			Object instancia = classeBean.newInstance();
			//BeanUtils.populate(instancia, propriedades);
			for (Map.Entry<String, Object> entry : propriedades.entrySet()) {
			  
				String nome = entry.getKey();
				Object valor = entry.getValue();
				
				Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(instancia, SigmaDBReflectionUtil.getNomeMetodoGet(nome));
				
				if (tipo == null) {
					tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(bean, SigmaDBReflectionUtil.getNomeMetodoIs(nome));
				}
				
				if (valor == null || valor.equals("0") || valor.equals("null")){
					
					valor = (tipo.isPrimitive() && !tipo.equals(char.class) ? 0 : null);
				}
				
				if (char.class.equals(tipo)) {
					
					Character characterValue = new Character(String.valueOf(valor).charAt(0));
					
					valor = characterValue.charValue();
				}
				
				SigmaDBReflectionUtil.setValorMetodoSetDaPropriedade(instancia, nome, valor, tipo);
				
			}
			
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
	private static Object extraiValorResultSet(ResultSet resultSet, Class datatype, String name)
			throws Exception {
		
		if (datatype.equals(String.class)) {
			return resultSet.getString(name);
		} else if (datatype.equals(java.sql.Date.class)) {
			return resultSet.getDate(name);
		} else if (datatype.equals(java.sql.Timestamp.class)) {
			return resultSet.getTimestamp(name);
		} else if (datatype.equals(java.sql.Time.class)) {
			return resultSet.getTime(name);
		} else if (datatype.equals(double.class)) {
			return resultSet.getDouble(name);
		} else if (datatype.equals(Double.class)) {
			return new Double(resultSet.getDouble(name));
		} else if (datatype.equals(int.class)) {
			return resultSet.getInt(name);
		} else if (datatype.equals(Integer.class)) {
			return new Integer(resultSet.getInt(name));
		} else if (datatype.equals(long.class)) {
			return resultSet.getLong(name);
		} else if (datatype.equals(Long.class)) {
			return new Long(resultSet.getLong(name));
		} else if (datatype.equals(float.class)) {
			return resultSet.getFloat(name);
		} else if (datatype.equals(Float.class)) {
			return new Float(resultSet.getFloat(name));
		} else if (datatype.equals(short.class)) {
			return resultSet.getShort(name);
		} else if (datatype.equals(Short.class)) {
			return new Short(resultSet.getShort(name));
		} else if (datatype.equals(byte.class)) {
			return resultSet.getByte(name);
		} else if (datatype.equals(Byte.class)) {
			return new Byte(resultSet.getByte(name));
		}  else if (datatype.equals(java.io.InputStream.class)) {
			return resultSet.getAsciiStream(name);
		} else if (datatype.equals(boolean.class)) {
			return resultSet.getBoolean(name);
		} else if (datatype.equals(Boolean.class)) {
			return new Boolean(resultSet.getBoolean(name));
		} else if (datatype.equals(java.sql.Array.class)) {
			return resultSet.getArray(name);
		} else if (datatype.equals(java.sql.Blob.class)) {
			return resultSet.getBlob(name);
		} else if (datatype.equals(java.sql.Clob.class)) {
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
		
		Class tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(bean, SigmaDBReflectionUtil.getNomeMetodoGet(nomeAtributo));
		
		if (tipo == null) {
			tipo = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(bean, SigmaDBReflectionUtil.getNomeMetodoIs(nomeAtributo));
		}
		
		
		if (String.class.equals(tipo)
				&& ("".equals(propriedades.get(nomeAtributo)) || "null"
						.equalsIgnoreCase(String.valueOf(propriedades
								.get(nomeAtributo))))) {
			return false;
		}		
				
		if (Character.class.equals(tipo)) {
			
			Character characterValue = (Character) propriedades.get(nomeAtributo);
			
			return characterValue != '\u0000';
		}
		
		if (char.class.equals(tipo)) {
			
			Character characterValue = new Character(String.valueOf(propriedades.get(nomeAtributo)).charAt(0));
			
			return characterValue != '\u0000';
		}
		
		if ((int.class.equals(tipo) || Integer.class.equals(tipo))
				&& ("0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}

		if ((double.class.equals(tipo) || Double.class.equals(tipo))
				&& ("0.0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}
		
		if ((long.class.equals(tipo) || Long.class.equals(tipo))
				&& ("0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}
		
		if ((float.class.equals(tipo) || Float.class.equals(tipo))
				&& ("0.0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}
		
		if ((short.class.equals(tipo) || Short.class.equals(tipo))
				&& ("0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}
		
		if ((byte.class.equals(tipo) || Byte.class.equals(tipo))
				&& ("0".equals(propriedades.get(nomeAtributo)) || "null".equals(propriedades.get(nomeAtributo)))) {
			return false;
		}
		
		if (Boolean.class.equals(tipo)
				&& "null".equals(propriedades.get(nomeAtributo))) {
			return false;
		}

		if (Timestamp.class.equals(tipo)
				&& "null".equalsIgnoreCase(String.valueOf(propriedades
						.get(nomeAtributo)))) {
			return false;
		}
		
		if (java.sql.Date.class.equals(tipo)
				&& "null".equalsIgnoreCase(String.valueOf(propriedades
						.get(nomeAtributo)))) {
			return false;
		}
		
		if (Time.class.equals(tipo)
				&& "null".equalsIgnoreCase(String.valueOf(propriedades
						.get(nomeAtributo)))) {
			return false;
		}

		return true;
	}

	private Map gerarMapaPropriedades(String[] propriedadesExcluidas,
			Object bean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		Map propriedades = new HashMap();

		List<String> listaAtributos = SigmaDBReflectionUtil
				.listaNomeDosAtributosDoObjetoVO(bean);

		for (String atributo : listaAtributos) {
			Object valor = SigmaDBReflectionUtil.getValorMetodoGet(bean, atributo);
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
	 * Monta uma restriçãoo do tipo IN para uma determinada coluna.
	 * 
	 * @param restricao
	 *            Objeto que conterá o nome de uma coluna da consulta e um
	 *            conjunto de Ids onde será aplicada a restrição IN para a
	 *            coluna.
	 * @return String contendo a cláusula IN. Caso não seja informado uma
	 *         restriçãoo, ou não seja informado um nome de propriedade na
	 *         restrição, ou não sejam informados Ids na restrição, será
	 *         retornada uma String em branco.
	 * @see {@link ComandoSqlIN}
	 */
	private String whereIn(ComandoSqlIN restricao) {

		String where = "";

		if (restricao != null) {

			boolean aplicaFiltro = restricao.getArrayIds() != null
					&& restricao.getArrayIds().length > 0
					&& !SigmaDBUtil.isNullOrEmpty(restricao.getNomePropriedade());

			if (aplicaFiltro) {
				where += restricao.getNomePropriedade() + " "
						+ whereIn(restricao.getArrayIds(), false);
			}
		}

		return where;
	}
	
	/**
	 * Monta uma String contendo uma cláusula IN.
	 * @param valores Valores que irão compor a cláusula.
	 * @param aspasSimples Se true inclui aspas simples nos valores, se false não inclui.
	 * @return String conteno a cláusula in.
	 */
	private String whereIn(String[] valores, boolean aspasSimples) {
		if (valores.length == 0) {
			return "";
		}

		StringBuffer retorno = new StringBuffer();

		for (int i = 0; i < valores.length; i++) {
			String valor = valores[i];

			if (aspasSimples) {
				valor = SigmaDBUtil.aspasSimples(valor);
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
	private String preparaClausulaWhereCommandQuery(
			CommandQuery commandConsultaVO) {

		StringBuilder where = new StringBuilder("");

		if (commandConsultaVO != null) {

			boolean adiciouClausula = false;

			boolean adicionaVirgula = false;

			List<ComandoSqlIN> listaRestricaoIN = commandConsultaVO
					.getListaRestricoesIN();

			for (ComandoSqlIN restricaoSqlIN : listaRestricaoIN) {
				where.append(this.whereIn(restricaoSqlIN));
			}
			
			adiciouClausula = !SigmaDBUtil.isNullOrEmpty(where.toString());

			String restricaoAND = this.montaRestricaoANDcommandConsulta(commandConsultaVO, adiciouClausula);
			where.append(restricaoAND);
			
			adiciouClausula = !SigmaDBUtil.isNullOrEmpty(restricaoAND);
			
			String restricaoOR = this
					.montaRestricaoORcommandConsulta(commandConsultaVO, adiciouClausula);
			where.append(restricaoOR);
			
			adiciouClausula = !SigmaDBUtil.isNullOrEmpty(restricaoOR);
			
			String restricaoLike = this
					.montaRestricaoLikeCommandConsulta(commandConsultaVO, adiciouClausula);
			where.append(restricaoLike);
			
			adiciouClausula = !SigmaDBUtil.isNullOrEmpty(restricaoLike);
			
			List<Ordenacao> orderBy = commandConsultaVO.getListaOrderBy();

			if (!SigmaDBUtil.isNullOrEmpty(orderBy)) {
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
	 * Inclui na consulta as restrições OR contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @param adicionouClasula True caso já exista uma clausula na consulta. False caso não exista nenhuma cláusula Where montada ainda.           
	 * @return String contendo a restrição do tipo OR montada entre parênteses
	 *         iniciada com um and. Ex. and (pess_id = 1 OR pess_id = 2 OR ...).
	 *         Caso a lista de restrição OR esteja vazia o método retornará uma
	 *         String em branco.
	 */
	private String montaRestricaoANDcommandConsulta(CommandQuery command, boolean adicionouClausula) {

		StringBuffer restricao = new StringBuffer();
		List<Filter> listaRestricao= command.getListaRestricaoAND();

		String restricaoRetorno = "";
		
		if (!SigmaDBUtil.isNullOrEmpty(listaRestricao)) {
			
			if (adicionouClausula) {
				restricao.append(" and ");
			}

			for (Filter filtro : listaRestricao) {
				
				restricao.append(filtro.getSQL() + " and ");
			}
			
			restricaoRetorno = restricao.substring(0, restricao.length() - 5);
		}

		return restricaoRetorno;		
	}
	

	/**
	 * Inclui na consulta as restrições OR contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição OR que deverá ser
	 *            aplicada a consulta.
	 * @param adicionouClasula True caso já exista uma clausula na consulta. False caso não exista nenhuma cláusula Where montada ainda.
	 * @return String contendo a restrição do tipo OR montada entre parênteses
	 *         iniciada com um and. Ex. and (pess_id = 1 OR pess_id = 2 OR ...).
	 *         Caso a lista de restrição OR esteja vazia o método retornará uma
	 *         String em branco.
	 */
	private String montaRestricaoORcommandConsulta(CommandQuery command, boolean adicionouClausula) {

		StringBuffer restricaoOR = new StringBuffer();
		List<Filter> listaRestricaoOR = command.getListaRestricaoOR();

		String restricaoRetorno = "";
		
		if (!SigmaDBUtil.isNullOrEmpty(listaRestricaoOR)) {
			
			if (adicionouClausula) {
				restricaoOR.append(" and ");
			}
			
			restricaoOR.append("(");
			for (Filter filtro : listaRestricaoOR) {
				
				restricaoOR.append(filtro.getSQL() + " OR ");
			}
			restricaoRetorno = restricaoOR.substring(0, restricaoOR.length() - 4);
			restricaoRetorno += ") ";
		}

		return restricaoRetorno;
	}

	/**
	 * Inclui na consulta as restrições de LIKE contidas no objeto de command da
	 * consulta.
	 * 
	 * @param command
	 *            Objeto contendo a lista de restrição LIKE que deverá ser
	 *            aplicada a consulta.
	 * @param adicionouClasula True caso já exista uma clausula na consulta. False caso não exista nenhuma cláusula Where montada ainda.
	 * @return String contendo a restrições de LIKE montada um and. Ex. and
	 *         pess_nome LIKE 'T1' and pess_nome LIKE 'T2'and ... Caso a lista
	 *         de restrição de LIKE esteja vazia, o método retornará uma String
	 *         em branco.
	 */
	private String montaRestricaoLikeCommandConsulta(CommandQuery command, boolean adicionouClausula) {
		StringBuffer restricaoLike = new StringBuffer();
		List<BeanFilter> listaRestricaoLike = command.getListaRestricaoLike();

		String restricaoRetorno = "";
		
		if (!SigmaDBUtil.isNullOrEmpty(listaRestricaoLike)) {
			
			if (adicionouClausula) {
				restricaoLike.append(" and ");
			}
			
			for (BeanFilter filtroVO : listaRestricaoLike) {
				restricaoLike.append(filtroVO.getNomePropriedade() + " like "
						+ filtroVO.getValorPropriedade() + " and ");
			}
			restricaoRetorno = restricaoLike.substring(0,
					restricaoLike.length() - 5);

		}

		return restricaoRetorno;
	}	

}
