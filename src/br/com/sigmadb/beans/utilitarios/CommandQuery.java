package br.com.sigmadb.beans.utilitarios;

import java.util.ArrayList;
import java.util.List;

import br.com.sigmadb.enumerations.EnumSortType;
import br.com.sigmadb.enumerations.Join;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.utilitarios.ConnectionLog;
import br.com.sigmadb.utilitarios.Filtro;
import br.com.sigmadb.utilitarios.SigmaDBUtil;
import br.com.sigmadb.utilitarios.TableMaster;

/**
 * Classe responsável por aplicar qualquer tipo de restrição para uma consulta. 
 * @author Igor Moisés
 */
public class CommandQuery {

	private String tabelaFrom;
	
	private ConnectionLog connectionLog;
	
	private List<String> listaRestricaoJoin;
	
	private List<Ordenacao> listaOrderBy;
	
	private List<Filtro> listaRestricaoOR;
	
	private List<Filtro> listaRestricaoAND;
	
	private List<BeanFilter> listaRestricaoLike;
	
	private List<ComandoSqlIN> listaCommandSqlIN;
	
	private String[] atributosExclusaoConsulta;
	
	private List<String> listaColunasRetorno;
	
	public CommandQuery(){
		this.inicializaPropriedades(null);
	}
	
	/**
	 * @param connection Objeto de conexão aberta.
	 */
	private void inicializaPropriedades(ConnectionLog connection){
		this.connectionLog = connection;
		this.listaCommandSqlIN = new ArrayList<ComandoSqlIN>();
		this.listaOrderBy = new ArrayList<Ordenacao>();
		this.listaRestricaoOR = new ArrayList<Filtro>();
		this.listaRestricaoAND = new ArrayList<Filtro>();
		this.atributosExclusaoConsulta = new String[0];
		this.listaRestricaoJoin = new ArrayList<String>();
		this.listaRestricaoLike = new ArrayList<BeanFilter>();
		this.listaColunasRetorno = new ArrayList<String>();
	}
	
	/**
	 * @param connection Objeto que representa uma conexão aberta com o banco. 
	 */
	public CommandQuery(ConnectionLog connection){
		this.inicializaPropriedades(connection);
	}
	
	public ConnectionLog getConnectionLog() {
		return connectionLog;
	}

	/**
	 * @param connection Objeto que representa uma conexão aberta com o banco. 
	 */
	public void setConnectionLog(ConnectionLog connection) {
		this.connectionLog = connectionLog;
	}
	
	/**
	 * Adiciona ordenações a consulta.
	 * @param nomeColuna Nome da coluna que receberá o critério de ordenação.
	 * @param enumSort Critétio de ordenação, ASC ou DESC.
	 * @see EnumSortType
	 * @throws Exception
	 */
	public void addOrdenacao(String nomeColuna, EnumSortType enumSort) throws Exception{
		Ordenacao ordenacaoVO = new Ordenacao(nomeColuna, enumSort);
		listaOrderBy.add(ordenacaoVO);
	}
	
	/**
	 * Insere uma restrição do tipo IN no objeto.<br>
	 * <b>O objeto é capaz de aplicar mais de uma restrição do tipo IN numa consulta. Basta inseri-las através deste método.</b><br>
	 * @param restricaoIn Restrição do tipo IN que deverá ser aplicada a consulta.
	 */
	private void addRestricaoSqlIN(ComandoSqlIN restricaoIn){
		
		boolean insereRestricao = restricaoIn != null &&
						 		  restricaoIn.getArrayIds() != null &&
						 		  restricaoIn.getArrayIds().length > 0 &&
						 		  !SigmaDBUtil.isNullOrEmpty(restricaoIn.getNomePropriedade());
		if (insereRestricao){
			this.listaCommandSqlIN.add(restricaoIn);
		}
	}
	
	/**
	 * Adiciona ao objeto uma nova restrição do tipo IN para uma coluna, tendo como referência os valores dos atributos especificados da lista informada.
	 * @param nomeColunaTabela Nome da coluna na qual a restrição IN deverá ser aplicada. 
	 * @param listaObjetos Lista de qualquer Bean que deverá ter um atributo listado.
	 * @param nomeAtributoSelecao Nome de um atributo do bean da lista acima, onde seus respectivos valores serão os valores da restrição do tipo IN.
	 * @throws Exception
	 */
	public void addRestricaoSqlIN(String nomeColunaTabela, List listaObjetos, String nomeAtributoSelecao) throws Exception{

		ComandoSqlIN restricaoIn = new ComandoSqlIN(nomeColunaTabela);
		restricaoIn.populaArrayIds(listaObjetos, nomeAtributoSelecao);
		
		this.addRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Adiciona ao objeto uma nova restrição do tipo NOT IN para uma coluna, tendo como referência os valores dos atributos especificados da lista informada.
	 * @param nomeColunaTabela Nome da coluna na qual a restrição IN deverá ser aplicada. 
	 * @param listaObjetos Lista de qualquer Bean que deverá ter um atributo listado.
	 * @param nomeAtributoSelecao Nome de um atributo do bean da lista acima, onde seus respectivos valores serão os valores da restrição do tipo IN.
	 * @throws Exception
	 */
	public void addRestricaoSqlNOT_IN(String nomeColunaTabela, List listaObjetos, String nomeAtributoSelecao) throws Exception{
		
		ComandoSqlIN restricaoIn = new ComandoSqlIN(nomeColunaTabela);
		restricaoIn.populaArrayIds(listaObjetos, nomeAtributoSelecao);
		restricaoIn.setNotIn(true);
		
		this.addRestricaoSqlIN(restricaoIn);
	}
	
	/**
	 * Adiciona uma nova restrição ao objeto que será precedida pelo operador lógico AND.<br>
	 * Ex. AND nomePropriedade = valorPropriedade
	 * @param filtro Objeto contendo as definições da restrição que será adicionada ao objeto.<br>
	 * Os tipos de filtro são {@link FiltroOperadorRelacional} e {@link FiltroRestricaoNull}
	 * @see FiltroOperadorRelacional
	 * @see FiltroRestricaoNull
	 * @throws SigmaDBException
	 */
	public void addRestricaoAND(Filtro filtro) throws SigmaDBException {
		
		this.validaInclusaoFiltro(filtro);
		
		this.listaRestricaoAND.add(filtro);
	}
	
	/**
	 * Adiciona uma nova restrição ao objeto que será precedida pelo operador lógico AND.<br>
	 * Ex. AND nomePropriedade = valorPropriedade
	 * @param filtro Objeto contendo as definições da restrição que será adicionada ao objeto.<br>
	 * Os tipos de filtro são {@link FiltroOperadorRelacional} e {@link FiltroRestricaoNull}
	 * @see FiltroOperadorRelacional
	 * @see FiltroRestricaoNull
	 * @throws SigmaDBException
	 */
	public void addRestricaoOR(Filtro filtro) throws SigmaDBException {
		
		this.validaInclusaoFiltro(filtro);
		
		this.listaRestricaoOR.add(filtro);
	}
	
	
	/**
	 * Aplica as devidas validações nos filtros adicionados ao objeto.
	 * @param filtro Filtro que deverá ser validado.
	 * @throws SigmaDBException
	 */
	private void validaInclusaoFiltro(Filtro filtro) throws SigmaDBException{
		
		if (filtro == null) {
			throw new SigmaDBException("Não é permitido adicionar filtros nulos.");
		}
		
		if (SigmaDBUtil.isNullOrEmpty(filtro.getNomePropriedade())) {
			throw new SigmaDBException("Não é permitido adicionar filtros com o atributo nomePropriedade nulo.");
		}
		
		if (filtro instanceof FiltroRestricaoNull) {
			
			FiltroRestricaoNull filtroRestricaoNull = (FiltroRestricaoNull) filtro;
			
			if (filtroRestricaoNull.getRestricaoNull() == null) {
				throw new SigmaDBException("Não é permitido adicionar filtros com o atributo operadorRelacional nulo.");
			}
			
		} else {
			
			if (SigmaDBUtil.isNullOrEmpty(filtro.getValorPropriedade())) {
				throw new SigmaDBException("Não é permitido adicionar filtros com o atributo valorPropriedade nulo.");
			}
			
			FiltroOperadorRelacional filtroOperadorRelacional = (FiltroOperadorRelacional) filtro;
			
			if (filtroOperadorRelacional.getOperadorRelacional() == null) {
				throw new SigmaDBException("Não é permitido adicionar filtros com o atributo operadorRelacional nulo.");
			}
		}
	}
	
	/**
	 * Adiciona no objeto a descriçãoo de colunas que não deverão ser retornadas na consulta.
	 * @param classe Classe do Bean que contem os atributos que deverão ser desconsiderados na consulta.
	 * @param prefixosAtributosExclusao Prefixos ou nome dos atributos que deverão ser listados.
	 * @throws Exception
	 */
	public void addAtributosExclusaoConsulta(Class classe, String...prefixosAtributosExclusao) throws Exception{
		
		if (prefixosAtributosExclusao != null && prefixosAtributosExclusao.length > 0) {
			
			Object objetoVO = classe.newInstance();
		
			String [] atributosExclusao = SigmaDBUtil.pegaAtributosExcluirConsulta(objetoVO, prefixosAtributosExclusao);

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
	 * Adiciona uma restrição do tipo like para a consulta.
	 * @param nomeColuna Nome da coluna que recebera esta restrição de Like.
	 * @param valor Valor do conteúdo do like.
	 * @param percentInicio True se o método deve incluir % no início do valor. False se o método não deve incluir % no início do parâmetro valor.
	 * @param percentFinal True se o método deve incluir % no final do parâmetro valor. False se o método não deve incluir % no final do parâmetro valor.
	 */
	public void addRestricaoLike(String nomeColuna, String valor, boolean percentInicio, boolean percentFinal) {
		if (!SigmaDBUtil.isNullOrEmpty(nomeColuna) && valor != null){
			
			if (percentInicio) {
				valor = "%" + valor;
			}
			
			if (percentFinal) {
				valor = valor + "%";
			}
			
			valor = "'" + valor + "'";
			
			BeanFilter filtro = new BeanFilter(nomeColuna, valor);
			this.listaRestricaoLike.add(filtro);
		}
	}
	
	/**
	 * Insere um join na consulta.
	 * @param enumJoin Tipo do Join que será inserido.
	 * @param classeTabelaJoin Classe que representa uma tabela do banco de dados e que será adicionada como Join.
	 * @param restricao_ON Restriçãoo ON do Join ligando uma tabela a outra. Ex. "nome_fk = nome_pk"
	 * @throws SigmaDBException
	 */
	public void addJoin(Join enumJoin, Class<? extends TableMaster> classeTabelaJoin, String restricao_ON) throws SigmaDBException{
		this.addJoin(enumJoin, classeTabelaJoin, null, restricao_ON);
	}
	
	/**
	 * Insere um join na consulta com a possibilidade de aplicar um alias a tabela deste join.
	 * @param enumJoin Tipo do Join que será inserido.
	 * @param classeTabelaJoin Classe que representa uma tabela do banco de dados e que será adicionada como Join.
	 * @param alias Alias para a tabela que ficará na sintaxe sql. <b>Este mesmo alias deverá estar contido no parâmetro restricao_ON.</b>
	 * @param restricao_ON Restriçãoo ON do Join ligando uma tabela a outra. Ex. "z.nome_fk = nome_pk"
	 * @throws SigmaDBException
	 */
	public void addJoin(Join enumJoin, Class<? extends TableMaster> classeTabelaJoin, String alias, String restricao_ON) throws SigmaDBException{

		if (classeTabelaJoin == null){
			throw new SigmaDBException("Informe uma classe que representa uma tabela do Banco de dados.");
		}
		
		if (enumJoin == null) {
			throw new SigmaDBException("Tipo de join não informado.");
		}
		
		String nomeTabela = classeTabelaJoin.getSimpleName();

		if (SigmaDBUtil.isNullOrEmpty(nomeTabela)) {
			throw new SigmaDBException("Nome da tabela para realização do join não informada.");
		}

		if (SigmaDBUtil.isNullOrEmpty(restricao_ON)) {
			throw new SigmaDBException("Restrição ON do join não informado.");
		}
		
		if (SigmaDBUtil.isNullOrEmpty(restricao_ON)) {
			throw new SigmaDBException("Restrição ON do join não informado.");
		}

		alias = SigmaDBUtil.isNullOrEmpty(alias) ? "" : " as " + alias;
		
		String join = enumJoin.getValue() + " " + nomeTabela + alias + " ON " + restricao_ON;

		this.listaRestricaoJoin.add(join);
	}
	
	/**
	 * Insere o nome da tabela para qual o objeto irá apontar como a tabela "FROM" na geração da consulta.
	 * @param classeTabelaFrom Classe que represente qualquer tabela do banco de dados. 
	 * @throws SigmaDBException
	 */
	public void setClausulaFrom(Class<? extends TableMaster> classeTabelaFrom) throws SigmaDBException {
		this.setClausulaFrom(classeTabelaFrom, null);
	}
	
	/**
	 * Insere o nome da tabela para qual o objeto irá apontar como a tabela "FROM" na geração da consulta.
	 * @param classeTabelaFrom Classe que represente qualquer tabela do banco de dados.
	 * @param alias Alias para a tabela que ficará na sintaxe sql.<b>Este mesmo alias deverá estar contido nas restrições da consulta que fizerem mensão a tabela "FROM".</b> 
	 * @throws SigmaDBException
	 */	
	public void setClausulaFrom(Class<? extends TableMaster> classeTabelaFrom, String alias) throws SigmaDBException {

		if (classeTabelaFrom == null){
			throw new SigmaDBException("Informe uma classe que representa uma tabela do Banco de dados.");
		}
		
		String nomeTabelaFrom = classeTabelaFrom.getSimpleName();
		
		if (SigmaDBUtil.isNullOrEmpty(nomeTabelaFrom)) {
			throw new SigmaDBException("Nome da tabela da consulta não informado.");
		}
		
		alias = SigmaDBUtil.isNullOrEmpty(alias) ? "" : " as " + alias;

		this.tabelaFrom = nomeTabelaFrom.toLowerCase() + alias;
	}
			
	/**
	 * Pegas as as colunas que devem ser desconsideradas pela consulta.
	 * @return Array de String contendo o nome das colunas que que devem ser desconsideradas pela consulta.
	 */
	public String[] getAtributosExclusaoConsulta(){
		return this.atributosExclusaoConsulta;
	}	
	
	/**
	 * Pega as restrições do tipo {@link ComandoSqlIN} da consulta.
	 * @return Lista contendo as restrições do tipo {@link ComandoSqlIN} que foram inseridas no objeto.
	 */
	public List<ComandoSqlIN> getListaRestricoesIN() {
		return listaCommandSqlIN;
	}
	
	/**
	 * Devolve a lista de restrições de Joins.
	 * @return Lista de String contendo todas as restrições Joins montadas para a consulta.
	 */
	public List<String> getListaJoin(){
		return this.listaRestricaoJoin;
	}
	
	/**
	 * Adiciona as colunas que deverão ser retornadas na consulta.
	 * @param coluna Nome da coluna da tabela que deverá ser retornada na consulta.
	 * @throws SigmaDBException 
	 */
	public void addColunaRetorno(String coluna) throws SigmaDBException {
		this.addColunaRetorno(coluna, null);
	}
	
	/**
	 * Adiciona as colunas que deverão ser retornadas na consulta.
	 * @param coluna Nome da coluna da tabela que deverá ser retornada na consulta.
	 * @param alias Aplica um alias para a coluna que serrá retornada. Ex. colunas as alias
	 * @throws SigmaDBException 
	 */
	public void addColunaRetorno(String coluna, String alias) throws SigmaDBException {
		
		if (SigmaDBUtil.isNullOrEmpty(coluna)) {
			throw new SigmaDBException("Não é permitido inserir colunas vazias ou nulas para retorno da consulta.");
		}
		
		if (!SigmaDBUtil.isNullOrEmpty(alias)) {
			coluna = coluna + " as " + alias;
		}
		
		this.listaColunasRetorno.add(coluna);
	}
	
	/**
	 * Devolve o nome da tabela para qual este objeto irá apontar na consulta como a tabela "FROM". 
	 * @return String contendo o nome de uma tabela do banco de dados.
	 */
	public String getTabelaFrom(){
		return this.tabelaFrom;
	}
	
	/**
	 * Devolve todas as restrições do tipo Like adicionadas neste objeto.
	 * @return Lista que representa todas as restrições do tipo Like adicionadas neste objeto.
	 */
	public List<BeanFilter> getListaRestricaoLike() {
		return listaRestricaoLike;
	}

	public List<Ordenacao> getListaOrderBy() {
		return listaOrderBy;
	}

	public List<Filtro> getListaRestricaoOR() {
		return listaRestricaoOR;
	}

	public List<Filtro> getListaRestricaoAND() {
		return listaRestricaoAND;
	}

	public List<ComandoSqlIN> getListaCommandSqlIN() {
		return listaCommandSqlIN;
	}

	public List<String> getListaColunasRetorno() {
		return listaColunasRetorno;
	}
}
