package br.com.sigmadb.beans.utilitarios;

import java.sql.Timestamp;

import br.com.sigmadb.annotations.PKTableMaster;
import br.com.sigmadb.util.TableMaster;

public class Ilog extends TableMaster {
	@PKTableMaster
	private int ilog_id;
	private int ilog_versao;
	private String ilog_tipo;
	private String ilog_tabela;
	private int ilog_pk_tabela;
	private Timestamp ilog_data_hora;
	private String ilog_origem;
	private String ilog_usuario;
	private String ilog_valores;
	
	public int getIlog_id() {
		return ilog_id;
	}

	public void setIlog_id(int ilog_id) {
		this.ilog_id = ilog_id;
	}

	public int getIlog_versao() {
		return ilog_versao;
	}

	public void setIlog_versao(int ilog_versao) {
		this.ilog_versao = ilog_versao;
	}

	public String getIlog_tipo() {
		return ilog_tipo;
	}

	public void setIlog_tipo(String ilog_tipo) {
		this.ilog_tipo = ilog_tipo;
	}

	public String getIlog_tabela() {
		return ilog_tabela;
	}

	public void setIlog_tabela(String ilog_tabela) {
		this.ilog_tabela = ilog_tabela;
	}

	public int getIlog_pk_tabela() {
		return ilog_pk_tabela;
	}

	public void setIlog_pk_tabela(int ilog_pk_tabela) {
		this.ilog_pk_tabela = ilog_pk_tabela;
	}

	public Timestamp getIlog_data_hora() {
		return ilog_data_hora;
	}

	public void setIlog_data_hora(Timestamp ilog_data_hora) {
		this.ilog_data_hora = ilog_data_hora;
	}

	public String getIlog_origem() {
		return ilog_origem;
	}

	public void setIlog_origem(String ilog_origem) {
		this.ilog_origem = ilog_origem;
	}

	public String getIlog_usuario() {
		return ilog_usuario;
	}

	public void setIlog_usuario(String ilog_usuario) {
		this.ilog_usuario = ilog_usuario;
	}

	public String getIlog_valores() {
		return ilog_valores;
	}

	public void setIlog_valores(String ilog_valores) {
		this.ilog_valores = ilog_valores;
	}
	
	@Override
	public void setId(int id) throws Exception {
		this.ilog_id = id;		
	}
}
