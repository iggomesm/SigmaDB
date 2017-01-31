package br.com.sigmadb.beans.utilitarios;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sigmadb.enumerations.EnumRestricaoNull;
import br.com.sigmadb.enumerations.EnumSortType;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.utilitarios.ConnectionLog;
import br.com.sigmadb.utilitarios.Day;
import br.com.sigmadb.utilitarios.TableMaster;
import br.com.sigmadb.utilitarios.Util;

/**
 * Classe que agrupa filtros gen�ricos para consultas no sistema.
 * Agrupa restri��es de per�odo para uma ou mais colunas.
 * Agrupa restri��es do tipo IN para uma ou mais colunas.
 * Agrupa restri��es sobre campos nulos.
 * Armazena informa��es de contexto onde a consulta dever� ser realizada.
 * Aplica condi��es de ordena��o para o resultado da consulta. 
 * @author Igor Mois�s
 * @since 26/06/2015
 */
public class CommandQuery {

	private ConnectionLog connectionLog;
	
	private Map<String, Periodo> mapaRestricoesPeriodo;	
	
	private List<CommandSqlIN> listaRestricoesIN;

	private List<Ordenacao> listaOrderBy;

	private List<CommandNull> listaRestricaoNull;

	private List<Filtro> listaRestricaoOR;
	
	private String[] atributosExclusaoConsulta;
	
	private List<Filtro> listaRestricaoDiferenca;
	
	private String tabelaFrom;
	
	private List<String> listaRestricaoJoin;
	
	private List<Filtro> listaRestricaoIgualdade;
	
	private List<Filtro> listaRestricaoLike;
	
	private List<RestricaoMaiorMenor> listaRestricaoMaiorMenor;
	
	public CommandQuery(){
		this.inicializaPropriedades(null);
	}
	
	private void inicializaPropriedades(ConnectionLog connection){
		this.connectionLog = connection;
		this.mapaRestricoesPeriodo = new HashMap<String, Periodo>();
		this.listaRestricoesIN = new ArrayList<CommandSqlIN>();
		this.listaOrderBy = new ArrayList<Ordenacao>();
		this.listaRestricaoNull = new ArrayList<CommandQuery.CommandNull>();
		this.listaRestricaoOR = new ArrayList<Filtro>();
		this.atributosExclusaoConsulta = new String[0];
		this.listaRestricaoDiferenca = new ArrayList<Filtro>();
		this.listaRestricaoJoin = new ArrayList<String>();
		this.listaRestricaoIgualdade = new ArrayList<Filtro>();
		this.listaRestricaoLike = new ArrayList<Filtro>();
		this.listaRestricaoMaiorMenor = new ArrayList<RestricaoMaiorMenor>();
	}
	
	/**
	 * @param contextoConsulta Contexto do banco onde a consulta dever� ser executada.
	 * @see Constantes.CONEXAO_GAIA, Constantes.CONEXAO_TETIS. 
	 */
	public CommandQuery(ConnectionLog connection){
		this.inicializaPropriedades(connection);
	}
	
	public ConnectionLog getConnectionLog() {
		return connectionLog;
	}

	public void setConnectionLog(ConnectionLog connectionLog) {
		this.connectionLog = connectionLog;
	}

	/**
	 * <b>O objeto pode aplicar mais de uma restri��o do tipo per�odo na consulta. Basta inseri-las atrav�s deste m�todo.</b><br>
	 * Cria no objeto uma restri��o do tipo per�odo que poder� ser aplicada numa consulta. 
	 * Ex. nomeColuna >= dataInicialPeriodo and nomeColuna <= dataFinalPeriodo
	 * @param nomeColuna Representa o nome de uma coluna de uma tabela, onde a restri��o de per�odo dever� ser aplicada. 
	 * @param dataInicialPeriodo Representa a data inicial do per�odo da restri��o.
	 * @param dataFinalPeriodo Representa a data final do per�odo da restri��o.
	 * @throws Exception 
	 */
	public void insereRestricaoPeriodo(String nomeColuna, Day dataInicial, Day dataFinal) throws Exception{
		this.insereRestricaoPeriodo(nomeColuna, dataInicial, dataFinal, TipoRestricaoPeriodo.DENTRO_PERIODO);
	}
	
	/**
	 * <b>O objeto pode aplicar mais de uma restri��o do tipo per�odo na consulta. Basta inseri-las atrav�s deste m�todo.</b><br>
	 * Cria no objeto uma restri��o do tipo per�odo que poder� ser aplicada numa consulta. 
	 * @param nomeColuna Representa o nome de uma coluna de uma tabela, onde a restri��o de per�odo dever� ser aplicada. 
	 * @param dataInicialPeriodo Representa a data inicial do per�odo da restri��o.
	 * @param dataFinalPeriodo Representa a data final do per�odo da restri��o.
	 * @param tipoRestricaoPeriodo Enum que representa o tipo de restri��o de per�odo que ser� aplicado.
	 * @see {@link TipoRestricaoPeriodo}
	 * @throws Exception 
	 */
	public void insereRestricaoPeriodo(String nomeColuna, Day dataInicial, Day dataFinal, TipoRestricaoPeriodo tipoRestricaoPeriodo) throws Exception{
		
		if (Util.isNullOrEmpty(nomeColuna)){
			throw new Exception("N�o � permitido criar uma restri��o de per�odo sem que seja informada o nome de uma coluna.");
		}
		
		if (dataInicial == null && dataFinal == null){
			throw new Exception("N�o � permitido criar uma restri��o de per�odo sem que seja informado pelo menos uma data para o per�odo.");
		}
		
		boolean validaRestricaoForaPeriodo = tipoRestricaoPeriodo == TipoRestricaoPeriodo.FORA_PERIODO &&
				(dataInicial == null || dataFinal == null);
		
		if (validaRestricaoForaPeriodo) {
			throw new Exception("N�o � permitido criar uma restri��o de per�odo do tipo FORA_PERIODO sem que sejam informadas as duas datas para o per�odo.");
		}
		
		Periodo restricaoPeriodo = new Periodo(nomeColuna, dataInicial, dataFinal, tipoRestricaoPeriodo);
		
		this.mapaRestricoesPeriodo.put(nomeColuna, restricaoPeriodo);
	}
	
	/**
	 * Metodo para adicionar ordena��o na consulta.
	 * @param nomeColuna Nome da Coluna que sera ordenada
	 * @param enumSort Tipo da ordena��o podendo ser ASC ou DESC.
	 * @throws Exception
	 */
	public void insereOrdenacao(String nomeColuna, EnumSortType enumSort) throws Exception{
		Ordenacao ordenacaoVO = new Ordenacao(nomeColuna, enumSort);
		listaOrderBy.add(ordenacaoVO);
	}
	
	/**
	 * Insere uma restri��o do tipo IN no objeto.<br>
	 * <b>O objeto � capaz de aplicar mais de uma restri��o do tipo IN numa consulta. Basta inseri-las atrav�s deste m�todo.</b><br>
	 * @param restricaoIn Restri��o do tipo IN que dever� ser aplicada a consulta.
	 */
	public void insereRestricaoSqlIN(CommandSqlIN restricaoIn){
		
		boolean insereRestricao = restricaoIn != null &&
						 		  restricaoIn.getArrayIds() != null &&
						 		  restricaoIn.getArrayIds().length > 0 &&
						 		  !Util.isNullOrEmpty(restricaoIn.getNomePropriedade());
		if (insereRestricao){
			this.listaRestricoesIN.add(restricaoIn);
		}
	}
	
	/**
	 * Monta um objeto do tipo {@link CommandSqlIN}, baseado nos par�metros informados e o insere neste objeto.
	 * @param nomeColunaTabela Nome da coluna na qual a restri��o IN dever� ser aplicada. 
	 * @param listaObjetos Lista de qualquer VO que dever� ter um atributo listado.
	 * @param nomeAtributoSelecao Nome de um atributo da lista acima, onde seus respectivos valores ser�o os valores da restri��o do objeto {@link CommandSqlIN}.
	 * @throws Exception
	 */
	public void insereRestricaoSqlIN(String nomeColunaTabela, List listaObjetos, String nomeAtributoSelecao) throws Exception{

		CommandSqlIN restricaoIn = new CommandSqlIN(nomeColunaTabela);
		restricaoIn.populaArrayIds(listaObjetos, nomeAtributoSelecao);
		
		this.insereRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Monta um objeto do tipo {@link CommandSqlIN}, baseado nos par�metros informados e o insere neste objeto.
	 * @param nomeColunaTabela Nome da coluna na qual a restri��o IN dever� ser aplicada. 
	 * @param listaObjetos Lista de qualquer VO que dever� ter um atributo listado.
	 * @param nomeAtributoSelecao Nome de um atributo da lista acima, onde seus respectivos valores ser�o os valores da restri��o do objeto {@link CommandSqlIN}.
	 * @param notIn Caso seja true o tipo da consulta sera not in, caso contrario sera in.
	 * @throws Exception
	 */
	public void insereRestricaoSqlIN(String nomeColunaTabela, List listaObjetos, String nomeAtributoSelecao, boolean notIn) throws Exception{
		
		CommandSqlIN restricaoIn = new CommandSqlIN(nomeColunaTabela);
		restricaoIn.populaArrayIds(listaObjetos, nomeAtributoSelecao);
		restricaoIn.setNotIn(notIn);
		
		this.insereRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Monta um objeto do tipo {@link CommandSqlIN}, baseado nos par�metros informados e o insere neste objeto.
	 * @param nomeColunaTabela Nome da coluna na qual a restri��o IN dever� ser aplicada. 
	 * @param listaObjetos Lista contendo os valores que ser�o aplicados na restri��o IN.
	 * @param notIn Caso seja true o tipo da consulta sera not in, caso contrario sera in.
	 * @throws Exception
	 */
	public void insereRestricaoSqlIN(String nomeColunaTabela, List listaPrimitivos, boolean notIn) throws Exception{

		CommandSqlIN restricaoIn = new CommandSqlIN(nomeColunaTabela, notIn);
		restricaoIn.populaArrayIds(listaPrimitivos);
		
		this.insereRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Monta um objeto do tipo {@link CommandSqlIN}, baseado nos par�metros informados e o insere neste objeto.
	 * @param nomeColunaTabela Nome da coluna na qual a restri��o IN dever� ser aplicada. 
	 * @param listaObjetos Lista contendo os valores que ser�o aplicados na restri��o IN.
	 * @throws Exception
	 */
	public void insereRestricaoSqlIN(String nomeColunaTabela, List listaPrimitivos) throws Exception{

		CommandSqlIN restricaoIn = new CommandSqlIN(nomeColunaTabela);
		restricaoIn.populaArrayIds(listaPrimitivos);
		
		this.insereRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Pega as restri��es do tipo IN da consulta.
	 * @return Lista contendo as restri��es do tipo IN que foram inseridas no objeto.
	 */
	public List<CommandSqlIN> getListaRestricoesIN() {
		return listaRestricoesIN;
	}
	
	/**
	 * Pega o mapa que armazena restri��es de per�odo.
	 * @return Mapa contendo restri��es de per�odo inseridas no objeto. 
	 * A chave do mapa � o nome da coluna para qual a restri��o de per�odo ser� aplicada. 
	 */
	public Map<String, Periodo> getMapaRestricoesPeriodo() {
		return mapaRestricoesPeriodo;
	}
	
	public List<Ordenacao> getListaOrderBy() {
		return listaOrderBy;
	}
	
	/**
	 * Adiona no objeto uma restri��o do tipo Null para uma coluna.
	 * Uma ou mais restri��es para colunas diferentes podem ser adicionadas.
	 * @param nomeColuna Nome da coluna onde a restri��o dever� ser aplicada.
	 * @param enumRestricaoNull Tipo da restri��o que dever� ser aplicada.
	 */
	public void insereRestricaoNull(String nomeColuna, EnumRestricaoNull enumRestricaoNull){
		insereRestricaoNull(nomeColuna, enumRestricaoNull, EnumOperadorLogico.AND);
	}
	
	/**
	 * Adiona no objeto uma restri��o do tipo Null para uma coluna.
	 * Uma ou mais restri��es para colunas diferentes podem ser adicionadas.
	 * @param nomeColuna Nome da coluna onde a restri��o dever� ser aplicada.
	 * @param enumRestricaoNull Tipo da restri��o que dever� ser aplicada.
	 * @param operador Tipo de logica que ser� feita na clasula where como 
	 * AND ou OR.
	 */
	public void insereRestricaoNull(String nomeColuna, EnumRestricaoNull enumRestricaoNull, EnumOperadorLogico operador){
		if (!Util.isNullOrEmpty(nomeColuna) && enumRestricaoNull != null && operador != null){
			this.listaRestricaoNull.add(new CommandNull(nomeColuna, enumRestricaoNull, operador));
		}
	}
	
	public List<CommandNull> getListaRestricaoNull() {
		return listaRestricaoNull;
	}
	
	/**
	 * Adiciona uma restri��o do tipo OR para a consulta. Se no objeto existir somente uma restri��o OR el� ser�
	 * transformada num AND automaticamente.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoOR(String nomeColuna, String valor) {
		if (!Util.isNullOrEmpty(nomeColuna) && !Util.isNullOrEmpty(valor)){
			Filtro filtro = new Filtro (nomeColuna, ("'" + valor + "'"));
			this.listaRestricaoOR.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo OR para a consulta. Se no objeto existir somente uma restri��o OR el� ser�
	 * transformada num AND automaticamente.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoOR(String nomeColuna, int valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoOR.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo OR para a consulta. Se no objeto existir somente uma restri��o OR el� ser�
	 * transformada num AND automaticamente.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoOR(String nomeColuna, double valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoOR.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo OR para a consulta. Se no objeto existir somente uma restri��o OR el� ser�
	 * transformada num AND automaticamente.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoOR(String nomeColuna, Timestamp valor) {
		if (!Util.isNullOrEmpty(nomeColuna) && valor != null){			
			Day data = new Day(valor);			
			Filtro filtro = new Filtro(nomeColuna, data.getDataBanco());
			this.listaRestricaoOR.add(filtro);
		}
	}
	
	public List<Filtro> getListaRestricaoOR() {
		return listaRestricaoOR;
	}
	
	/**
	 * Insere no objeto a descri��o das colunas que n�o dever�o ser retornadas na consulta.
	 * @param classe Classe do VO que contem os atributos que n�o dever�o ser retornados na consulta.
	 * @param prefixosAtributosExclusao Prefixos ou nome dos atributos que dever�o ser listados.
	 * @throws Exception
	 */
	public void insereAtributosExclusaoConsulta(Class classe, String...prefixosAtributosExclusao) throws Exception{
		
		if (prefixosAtributosExclusao != null && prefixosAtributosExclusao.length > 0) {
			
			Object objetoVO = classe.newInstance();
		
			String [] atributosExclusao = Util.pegaAtributosExcluirConsulta(objetoVO, prefixosAtributosExclusao);

			List<String> listaAtributosExclusao = new ArrayList<String>();

			for (int i = 0; i < this.atributosExclusaoConsulta.length; i++) {			
				listaAtributosExclusao.add(this.atributosExclusaoConsulta[i]);
			}
			for (int i = 0; i < atributosExclusao.length; i++) {			
				if (!listaAtributosExclusao.contains(atributosExclusao[i])){
					listaAtributosExclusao.add(atributosExclusao[i]);
				}
			}

			this.atributosExclusaoConsulta =  listaAtributosExclusao.toArray(new String[0]);
		}
	}
			
	/**
	 * Pegas as as colunas que n�o devem ser retornadas na consulta.
	 * @return Array de String contendo o nome das colunas que n�o devem ser retornadas na consulta.
	 */
	public String[] getAtributosExclusaoConsulta(){
		return this.atributosExclusaoConsulta;
	}

	/**
	 * Adiciona uma restri��o do tipo igualdade (where a = b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 * @param addAspasSimples True se o m�todo deverer� incluir aspas simples no par�metro valor. False se o m�todo n�o dever� incluir aspas simples no valor.
	 */
	public void insereRestricaoIgualdade(String nomeColuna, String valor, boolean addAspasSimples) {
		if (!Util.isNullOrEmpty(nomeColuna) && !Util.isNullOrEmpty(valor)){
			
			valor = addAspasSimples ? "'" + valor + "'" : valor;
			
			Filtro filtro = new Filtro (nomeColuna, valor);
			this.listaRestricaoIgualdade.add(filtro);
		}
	}

	/**
	 * Adiciona uma restri��o do tipo igualdade (where a = b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoIgualdade(String nomeColuna, int valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoIgualdade.add(filtro);
		}
	}

	/**
	 * Adiciona uma restri��o do tipo igualdade (where a = b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoIgualdade(String nomeColuna, double valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoIgualdade.add(filtro);
		}
	}

	/**
	 * Adiciona uma restri��o do tipo igualdade (where a = b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoIgualdade(String nomeColuna, Timestamp valor) {
		if (!Util.isNullOrEmpty(nomeColuna) && valor != null){			
			Day data = new Day(valor);			
			Filtro filtro = new Filtro(nomeColuna, data.getDataBanco());
			this.listaRestricaoIgualdade.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo like (where a like 'b') para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 * @param percentInicio True se o m�todo deverer� incluir % no inicio do par�metro valor. False se o m�todo n�o dever� incluir % no inicio do parametro valor.
	 * @param percentFinal True se o m�todo deverer� incluir % no final do par�metro valor. False se o m�todo n�o dever� incluir % no final do parametro valor.
	 */
	public void insereRestricaoLike(String nomeColuna, String valor, boolean percentInicio, boolean percentFinal) {
		if (!Util.isNullOrEmpty(nomeColuna) && valor != null){
			
			if (percentInicio) {
				valor = "%" + valor;
			}
			
			if (percentFinal) {
				valor = valor + "%";
			}
			
			valor = "'" + valor + "'";
			
			Filtro filtro = new Filtro(nomeColuna, valor);
			this.listaRestricaoLike.add(filtro);
		}
	}
	
	/**
	 * Insere um join como restri��o para a consulta.
	 * @param enumJoin Tipo do Join que ser� inserido.
	 * @param nomeTabelaJoin Nome da tabela do Join.
	 * @param restricao_ON Restri��o ON do Join
	 * @throws SigmaDBException
	 */
	public void insereRestricaoJoin(EnumJoin enumJoin, Class classeTabelaJoin, String restricao_ON) throws SigmaDBException{
		this.insereRestricaoJoin(enumJoin, classeTabelaJoin, null, restricao_ON);
	}
	
	/**
	 * Insere um join como restri��o para a consulta.
	 * @param enumJoin Tipo do Join que ser� inserido.
	 * @param classeTabelaJoin Classe que representar� a tabela do Join.
	 * @param alias Alias para a tabela que ficar� na sintaxe sql. <b>Este mesmo alias dever� estar contido no par�metro restricao_ON.</b>
	 * @param restricao_ON Restri��o ON do Join
	 * @throws SigmaDBException
	 */
	public void insereRestricaoJoin(EnumJoin enumJoin, Class classeTabelaJoin, String alias, String restricao_ON) throws SigmaDBException{

		if (classeTabelaJoin == null){
			throw new SigmaDBException("Informe uma classe que representa uma tabela do Banco de dados.");
		}
		
		if (enumJoin == null) {
			throw new SigmaDBException("Tipo de join n�o informado.");
		}
		
		String nomeTabela = classeTabelaJoin.getSimpleName();

		if (Util.isNullOrEmpty(nomeTabela)) {
			throw new SigmaDBException("Nome da tabela para realiza��o do join n�o informada.");
		}

		if (Util.isNullOrEmpty(restricao_ON)) {
			throw new SigmaDBException("Restri��o ON do join n�o informado.");
		}
		
		if (Util.isNullOrEmpty(restricao_ON)) {
			throw new SigmaDBException("Restri��o ON do join n�o informado.");
		}

		alias = Util.isNullOrEmpty(alias) ? "" : " as " + alias;
		
		String join = enumJoin.getValue() + " " + nomeTabela + alias + " ON " + restricao_ON;

		this.listaRestricaoJoin.add(join);
	}

	/**
	 * Devolve a lista de restri��es de Joins.
	 * @return Lista de String contendo todas as restri��es Joins montadas para a consulta.
	 */
	public List<String> getListaRestricaoJoin(){
		return this.listaRestricaoJoin;
	}

	/**
	 * Insere o nome da tabela para qual o complemento consulta ir� apontar como a "FROM".
	 * @param classeTabelaFrom Classe que representa qualquer tabela do sistema. 
	 * @throws SigmaDBException
	 */
	public void insereClausulaFrom(Class classeTabelaFrom) throws SigmaDBException {
		this.insereClausulaFrom(classeTabelaFrom, null);
	}
	
	/**
	 * Insere o nome da tabela para qual o complemento consulta ir� apontar como a "FROM".
	 * @param classeTabelaFrom Classe que representa qualquer tabela do sistema.
	 * @param alias Alias para a tabela que ficar� na sintaxe sql.<b>Este mesmo alias dever� estar contido nas restri��es da consulta que fizerem men��o a tabela.</b> 
	 * @throws SigmaDBException
	 */	
	public void insereClausulaFrom(Class classeTabelaFrom, String alias) throws SigmaDBException {

		if (classeTabelaFrom == null){
			throw new SigmaDBException("Informe uma classe que representa uma tabela do Banco de dados.");
		}
		
		String nomeTabelaFrom = classeTabelaFrom.getSimpleName();
		
		if (Util.isNullOrEmpty(nomeTabelaFrom)) {
			throw new SigmaDBException("Nome da tabela da consulta n�o informado.");
		}
		
		alias = Util.isNullOrEmpty(alias) ? "" : " as " + alias;

		this.tabelaFrom = nomeTabelaFrom.toLowerCase() + alias;
	}

	/**
	 * Devolve o nome da tabela paraqual o complemento consulta ir� apontar na consulta como a "FROM". 
	 * @return String contendo o nome de uma tabela do sistema.
	 */
	public String getTabelaFrom(){
		return this.tabelaFrom;
	}
	
	/**
	 * Devolve a listagem das restri��es de igualdade contidas no objeto.
	 * @return Lista de {@link Filtro} contendo as restri��es de igualdade do objeto.
	 */
	public List<Filtro> getListaRestricaoIgualdade() {
		return listaRestricaoIgualdade;
	}

	/**
	 * Adiciona uma restri��o do tipo diferen�a (where a <> b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoDiferenca(String nomeColuna, String valor) {
		if (!Util.isNullOrEmpty(nomeColuna) && !Util.isNullOrEmpty(valor)){
			Filtro filtro = new Filtro (nomeColuna, ("'" + valor + "'"));
			this.listaRestricaoDiferenca.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo diferen�a (where a <> b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoDiferenca(String nomeColuna, int valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoDiferenca.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo diferen�a (where a <> b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoDiferenca(String nomeColuna, double valor) {
		if (!Util.isNullOrEmpty(nomeColuna)){
			Filtro filtro = new Filtro(nomeColuna, String.valueOf(valor));
			this.listaRestricaoDiferenca.add(filtro);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo diferen�a (where a <> b) para a consulta. 
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 */
	public void insereRestricaoDiferenca(String nomeColuna, Timestamp valor) {
		if (!Util.isNullOrEmpty(nomeColuna) && valor != null){			
			Day data = new Day(valor);			
			Filtro filtro = new Filtro(nomeColuna, data.getDataBanco());
			this.listaRestricaoDiferenca.add(filtro);
		}
	}
	
	// Tarefa 3152 - Romulo Santos (Inicio)
	/**
	 * Adiciona uma restri��o do tipo maior/menor/maiorIgual/menorIgual (ex: where a > b) para a consulta.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 * @param enumTipoRestricaoMaiorMenor Tipo da restri��o da consulta (maior/menor/maiorIgual/menorIgual).
	 */
	public void insereRestricaoMaiorMenorQue(String nomeColuna, int valor, EnumTipoRestricaoMaiorMenor enumTipoRestricaoMaiorMenor) throws SigmaDBException  {
		if (!Util.isNullOrEmpty(nomeColuna)) {
			
			if (enumTipoRestricaoMaiorMenor == null) {
				
				throw new SigmaDBException("Enum deve ser informado.");
			}
			
			RestricaoMaiorMenor restricaoMaiorMenor = new RestricaoMaiorMenor(nomeColuna, String.valueOf(valor), enumTipoRestricaoMaiorMenor);
			this.listaRestricaoMaiorMenor.add(restricaoMaiorMenor);
		}
	}
	
	/**
	 * Adiciona uma restri��o do tipo maior/menor/maiorIgual/menorIgual (ex: where a > b) para a consulta.
	 * @param nomeColuna Nome da coluna da restri��o da consulta.
	 * @param valor Valor da restri��o da consulta.
	 * @param enumTipoRestricaoMaiorMenor Tipo da restri��o da consulta (maior/menor/maiorIgual/menorIgual).
	 */
	public void insereRestricaoMaiorMenorQue(String nomeColuna, double valor, EnumTipoRestricaoMaiorMenor enumTipoRestricaoMaiorMenor) {
		if (!Util.isNullOrEmpty(nomeColuna)) {
			RestricaoMaiorMenor restricaoMaiorMenor = new RestricaoMaiorMenor(nomeColuna, String.valueOf(valor), enumTipoRestricaoMaiorMenor);
			this.listaRestricaoMaiorMenor.add(restricaoMaiorMenor);
		}
	}
		
	public List<Filtro> getListaRestricaoDiferenca() {
		return listaRestricaoDiferenca;
	}
	
	public List<Filtro> getListaRestricaoLike() {
		return listaRestricaoLike;
	}
	
	public List<RestricaoMaiorMenor> getListaRestricaoMaiorMenor() {
		return listaRestricaoMaiorMenor;
	}
	
	/**
	 * Enum que representa os tipos de restri��o de per�odo do objeto.
	 * @author Igor Mois�s
	 * @since 21/06/2016
	 */
	public static enum TipoRestricaoPeriodo{
		/**
		 * Indica que a restri��o de per�odo ser� aplicada dentro da faixa de datas informadas.
		 * Ex. nomeColuna >= dataInicialPeriodo and nomeColuna <= dataFinalPeriodo 
		 */
		DENTRO_PERIODO,
		
		/**
		 * Indica que a restri��o de per�odo ser� aplicada fora da faixa de datas informadas. 
		 * Ex. nomeColuna <= dataInicialPeriodo and nomeColuna >= dataFinalPeriodo 
		 */
		FORA_PERIODO;
	}
	
	/**
	 * Representa uma restri��o de per�odo para alguma coluna de alguma consulta em qualquer tabela.<br>
	 * A classe cont�m os atributos abaixo: <br>
	 * nomeColuna - Representa o nome de uma coluna de uma tabela, onde a restri��o de per�odo dever� ser aplicada.
	 * dataInicialPeriodo - Representa a data inicial do per�odo da restri��o.
	 * dataFinalPeriodo - Representa a data final do per�odo da restri��o.
	 * @author Igor Mois�s
	 * @since 26/06/2015
	 */
	public static class Periodo {

		private String nomeColuna;
		private Day dataInicialPeriodo;
		private Day dataFinalPeriodo;
		private TipoRestricaoPeriodo tipoRestricaoPeriodo;

		
		/**
		 * @param nomeColuna Representa o nome de uma coluna de uma tabela, onde a restri��o de per�odo dever� ser aplicada. 
		 * @param dataInicialPeriodo Representa a data inicial do per�odo da restri��o.
		 * @param dataFinalPeriodo Representa a data final do per�odo da restri��o.
		 * @param tipoRestricaoPeriodo Representa qual a forma de restri��o de per�odo ser� aplicada.
		 * @throws SigmaDBException 
		 * @see {@link TipoRestricaoPeriodo}
		 */
		public Periodo(String nomeColuna, Day dataInicialPeriodo, Day dataFinalPeriodo, TipoRestricaoPeriodo tipoRestricaoPeriodo) throws SigmaDBException {
			this.nomeColuna = nomeColuna;
			this.dataInicialPeriodo = dataInicialPeriodo;
			this.dataFinalPeriodo = dataFinalPeriodo;			
			this.tipoRestricaoPeriodo = tipoRestricaoPeriodo;
			
			if (this.tipoRestricaoPeriodo == null) {
				throw new SigmaDBException("N�o � permitido criar RestricaoPeriodo sem um tipoRestricaoPeriodo");
			}

		}
		public String getNomeColuna() {
			return nomeColuna;
		}
		public void setNomeColuna(String nomeColuna) {
			this.nomeColuna = nomeColuna;
		}
		public Day getDataInicialPeriodo() {
			return dataInicialPeriodo;
		}
		public void setDataInicialPeriodo(Day dataInicialPeriodo) {
			this.dataInicialPeriodo = dataInicialPeriodo;
		}
		public Day getDataFinalPeriodo() {
			return dataFinalPeriodo;
		}
		public void setDataFinalPeriodo(Day dataFinalPeriodo) {
			this.dataFinalPeriodo = dataFinalPeriodo;
		}
		
		public TipoRestricaoPeriodo getTipoRestricaoPeriodo() {
			return tipoRestricaoPeriodo;
		}
		public void setTipoRestricaoPeriodo(TipoRestricaoPeriodo tipoRestricaoPeriodo) {
			this.tipoRestricaoPeriodo = tipoRestricaoPeriodo;
		}
		
		
		@Override
		public String toString() {

			String restricao = " ";
					
			boolean ehDentroPeriodo = tipoRestricaoPeriodo == TipoRestricaoPeriodo.DENTRO_PERIODO;
			boolean criaRestricao;
			
			if (ehDentroPeriodo) {			
				criaRestricao = !Util.isNullOrEmpty(this.nomeColuna) && 
			   			    	(dataInicialPeriodo != null || dataFinalPeriodo != null);
			} else {
				criaRestricao = !Util.isNullOrEmpty(this.nomeColuna) && 
		   			    		(dataInicialPeriodo != null && dataFinalPeriodo != null);
			}
			
			if (criaRestricao){
				
				final String comparadorInicioPeriodo = ehDentroPeriodo ? " >= " : " <= ";
				final String comparadorFinalPeriodo = ehDentroPeriodo ? " <= " : " >= ";
				
				boolean inseriuPeriodoInicial = false;

				if (dataInicialPeriodo != null) {

					restricao += this.nomeColuna + comparadorInicioPeriodo + this.dataInicialPeriodo.getDataBanco();

					inseriuPeriodoInicial = true;
				}

				if (dataFinalPeriodo != null) {
					
					if (ehDentroPeriodo) {
					
						restricao += (inseriuPeriodoInicial ? " and " : "") + 
								     this.nomeColuna + comparadorFinalPeriodo + this.dataFinalPeriodo.getDataBanco23h();
					} else {
						restricao += " or " + this.nomeColuna + comparadorFinalPeriodo + this.dataFinalPeriodo.getDataBanco23h();
						restricao = "(" + restricao + ")";
					}
				}
			}
			
			return restricao;
		}
		
	}	
	
	/**
	 * Representa uma restri��o para campos nulos numa consulta SQL. A saber: Is null ou Is Not Null.
	 * @author Igor Mois�s
	 * @since 21/10/2015
	 * @see EnumRestricaoNull
	 */
	public static class CommandNull {
		
		private String nomeColuna;
		private EnumRestricaoNull enumRestricaoNull;
		private EnumOperadorLogico enumOperadorLogico;
		
		/**
		 * @param nomeColuna Nome da coluna onde a restri��o dever� ser aplicada.
		 * @param enumRestricaoNull Tipo da restri��o que dever� ser aplicada.
		 */
		public CommandNull(String nomeColuna,
				EnumRestricaoNull enumRestricaoNull) {
			super();
			this.nomeColuna = nomeColuna;
			this.enumRestricaoNull = enumRestricaoNull;
		}

		/**
		 * @param nomeColuna Nome da coluna onde a restri��o dever� ser aplicada.
		 * @param enumRestricaoNull Tipo da restri��o que dever� ser aplicada.
		 * @param enumOperadorLogico Tipo de logica que ser� feita na clasula where como 
		 * AND ou OR.
		 */
		public CommandNull(String nomeColuna, EnumRestricaoNull enumRestricaoNull, EnumOperadorLogico enumOperadorLogico) {
			super();
			this.nomeColuna = nomeColuna;
			this.enumRestricaoNull = enumRestricaoNull;
			this.enumOperadorLogico = enumOperadorLogico;
		}
		
		public String getNomeColuna() {
			return nomeColuna;
		}
		
		public void setNomeColuna(String nomeColuna) {
			this.nomeColuna = nomeColuna;
		}
		
		public EnumRestricaoNull getEnumRestricaoNull() {
			return enumRestricaoNull;
		}
		
		public void setEnumRestricaoNull(EnumRestricaoNull enumRestricaoNull) {
			this.enumRestricaoNull = enumRestricaoNull;
		}

		public EnumOperadorLogico getEnumOperadorLogico() {
			return enumOperadorLogico;
		}

		public void setEnumOperadorLogico(EnumOperadorLogico enumOperadorLogico) {
			this.enumOperadorLogico = enumOperadorLogico;
		}
		
		public String toString(){
			String sql = this.nomeColuna + " " + this.enumRestricaoNull.getDescricao();
			
			if( enumOperadorLogico == null ) {
				sql += EnumOperadorLogico.AND.getValue();
			} else {
				sql += enumOperadorLogico.getValue();
			}
			
			return sql;
		}

	}
	
	/**
	 * Enum que representa um tipo de Join, que poder� servir de restri��o para alguma consulta.<br>
	 * Tipos:<br>
	 * INNER_JOIN<br>
	 * LEFT_JOIN<br>
	 * RIGHT_JOIN<br>
	 * @author Igor Mois�s
	 * @since 14/07/2016
	 */
	public static enum EnumJoin {
		/**
		 * Indica que na consulta ser� inserido um inner join com alguma tabela.
		 */
		INNER_JOIN(" INNER JOIN "),
		
		/**
		 * Indica que na consulta ser� inserido um left join com alguma tabela.
		 */
		LEFT_JOIN(" LEFT JOIN "),
		
		/**
		 * Indica que na consulta ser� inserido um right join com alguma tabela.
		 */
		RIGHT_JOIN(" RIGTH JOIN ");
		
		private EnumJoin(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}
	}

	/**
	 * Enum que representa o operador logico na clausula where.<br>
	 * Tipos:<br>
	 * AND<br>
	 * OR<br>
	 * @author Fabio Jr.
	 * @since 09/09/2016
	 */
	public static enum EnumOperadorLogico {
		/**
		 * Indica que na consulta ser� inserido um AND na opera��o l�gica.
		 */
		AND(" AND "),
		
		/**
		 * Indica que na consulta ser� inserido um OR na opera��o l�gica.
		 */
		OR(" OR ");
		
		
		private EnumOperadorLogico(String value) {
			this.value = value;
		}
		
		private String value;
		
		public String getValue() {
			return value;
		}
	}
	
	

	public static enum EnumTipoRestricaoMaiorMenor {
		/**
		 * Indica que a restri��o ser� aplicada para coluna menor que o valor.
		 * Ex. nomeColuna < valor 
		 */
		MENOR_QUE(" < "),
		/**
		 * Indica que a restri��o ser� aplicada para coluna menor ou igual ao valor.
		 * Ex. nomeColuna <= valor 
		 */
		MENOR_IGUAL(" <= "),
		/**
		 * Indica que a restri��o ser� aplicada para coluna maior que o valor.
		 * Ex. nomeColuna > valor 
		 */
		MAIOR_QUE(" > "),
		/**
		 * Indica que a restri��o ser� aplicada para coluna maior ou igual ao valor.
		 * Ex. nomeColuna >= valor 
		 */
		MAIOR_IGUAL(" >= ");
		
		private EnumTipoRestricaoMaiorMenor(String value) {
			this.value = value;
		}
		
		private String value;
		
		public String getValue() {
			return value;
		}		
	}
	
	/**
	 * Representa uma restri��o maior/menor/menor igual/maior igual para campos numa consulta SQL. A saber: >, >=, <, <=.
	 * @author Igor Mois�s
	 * @since 21/10/2015
	 * @see EnumRestricaoNull
	 */
	public static class RestricaoMaiorMenor {
		
		private String nomeColuna;
		private String valor;
		private EnumTipoRestricaoMaiorMenor enumTipoRestricaoMaiorMenor;
		
		public RestricaoMaiorMenor(String nomeColuna, String valor, EnumTipoRestricaoMaiorMenor enumTipoRestricaoMaiorMenor) {
			super();
			this.nomeColuna = nomeColuna;
			this.valor = valor;
			this.enumTipoRestricaoMaiorMenor = enumTipoRestricaoMaiorMenor;
		}

		public String getNomeColuna() {
			return nomeColuna;
		}
		
		public void setNomeColuna(String nomeColuna) {
			this.nomeColuna = nomeColuna;
		}
		
		public String getValor() {
			return valor;
		}
		
		public void setValor(String valor) {
			this.valor = valor;
		}
		
		public EnumTipoRestricaoMaiorMenor getEnumTipoRestricaoMaiorMenor() {
			return enumTipoRestricaoMaiorMenor;
		}
		
		public void setEnumTipoRestricaoMaiorMenor(EnumTipoRestricaoMaiorMenor enumTipoRestricaoMaiorMenor) {
			this.enumTipoRestricaoMaiorMenor = enumTipoRestricaoMaiorMenor;
		}

		@Override
		public String toString() {
			return this.nomeColuna + " " + this.enumTipoRestricaoMaiorMenor.getValue() + " " + this.valor;
		}
		
	}	
}
