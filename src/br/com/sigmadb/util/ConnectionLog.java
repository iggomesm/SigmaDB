package br.com.sigmadb.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import br.com.sigmadb.beans.utilitarios.Ilog;
import br.com.sigmadb.connection.DataBase;
import br.com.sigmadb.exceptions.SigmaDBException;

/**
 * Classe que agrupa uma conexão com seus respectivos Logs.
 * @author Igor Moisés
 * @since 13/07/2015
 */
public class ConnectionLog {

	private Connection connection;
	private List<Ilog> listaLogs;
	private String usuario;
	private String origem;
	private int versao;
	private boolean geraEstruturaIlog;
	
	/**
	 * Construtor
	 * @param connection Objeto instanciado de conexão aberta com o banco.
	 * @param usuario usuario de uma pessoa para qual o log será destinado.
	 * @param origem Nome do local de origem que solicitou a abertura da conexão.
	 * @throws Exception 
	 */
	public ConnectionLog(Connection connection, String usuario, String origem, boolean geraLog) throws Exception{
		
		this.connection = connection;
		this.listaLogs = new ArrayList<Ilog>();
		this.usuario = usuario;
		this.origem = origem;
		
		this.geraEstruturaIlog = geraLog;
		
		if (geraLog) {
			this.versao = DataBase.pegaVersaoConexao();	
			this.validaConexaoPersistencia();		
		}
	}
	
	/**
	 * Construtor
	 * @param connection Objeto instanciado de conexão aberta com o banco.
	 * @param usuario id de uma pessoa para qual o log será destinado.
	 * @throws SigmaDBException 
	 */
	public ConnectionLog(Connection connection) throws SigmaDBException{
		
		if (connection == null) {
			throw new SigmaDBException("Não é possível criar conexão. A conexão informada é nula.");
		}
		
		this.connection = connection;
		this.listaLogs = new ArrayList<Ilog>();		
	}
	
	/**
	 * Realiza as validações para uma conexão correta de persistencia de dados.
	 * @throws SigmaDBException 
	 */
	public void validaConexaoPersistencia() throws SigmaDBException{
		if (connection == null) {
			throw new SigmaDBException("Não é possível criar conexão. A conexão informada é nula.");
		}
		
		if (geraEstruturaIlog) {
		
			if (SigmaDBUtil.isNullOrEmpty(usuario)){
				throw new SigmaDBException("Não é possível criar conexão sem um usuário válido.");
			}

			if (SigmaDBUtil.isNullOrEmpty(origem)){
				throw new SigmaDBException("Não é possível criar conexão sem que seja informado um local que originou a solicitação de abertura de conexão.");
			}

			if (versao == 0) {
				throw new SigmaDBException("Não é possível criar conexão sem que haja uma versão para ela.");
			}
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public List<Ilog> getListaLogs() {
		return listaLogs;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getOrigem() {
		return origem;
	}

	public int getVersao() {
		return versao;
	}

	public boolean isGeraEstruturaIlog() {
		return geraEstruturaIlog;
	}
	
	
}
