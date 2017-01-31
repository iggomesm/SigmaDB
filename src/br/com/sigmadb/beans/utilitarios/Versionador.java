package br.com.sigmadb.beans.utilitarios;

import br.com.sigmadb.annotations.PKTableMaster;
import br.com.sigmadb.utilitarios.TableMaster;

public class Versionador extends TableMaster {
	
	@PKTableMaster	
	private String vers_descricao;
	private int vers_valor;
	
	public String getVers_descricao() {
		return vers_descricao;
	}
	public void setVers_descricao(String vers_descricao) {
		this.vers_descricao = vers_descricao;
	}
	public int getVers_valor() {
		return vers_valor;
	}
	public void setVers_valor(int vers_valor) {
		this.vers_valor = vers_valor;
	}
	
	@Override
	public void setId(int id) throws Exception {
				
	}
}
