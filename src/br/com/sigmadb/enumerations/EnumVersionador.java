package br.com.sigmadb.enumerations;

import br.com.sigmadb.beans.utilitarios.Versionador;


public enum EnumVersionador {

	VERSAO_ILOG("VERSAO_ILOG");
	
	private String valor;
	
	private EnumVersionador(String valor){
		this.valor = valor;
	}
	
	public Versionador getInstance(){
		
		Versionador versionador = new Versionador();
		versionador.setVers_descricao(this.valor);
		
		return versionador;
	}

	public String getValor() {
		return valor;
	}	
	
}
