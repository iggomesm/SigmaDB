package br.com.sigmadb.beans.utilitarios;

import br.com.sigmadb.enumerations.EnumSortType;

public class Ordenacao {
	public String campo;
	public EnumSortType tipoOrdenacao;
	public Ordenacao (String campo, EnumSortType tipoOrdenacao){
		this.campo = campo;
		this.tipoOrdenacao = tipoOrdenacao;
	}	
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public EnumSortType getTipoOrdenacao() {
		return tipoOrdenacao;
	}
	public void setTipoOrdenacao(EnumSortType tipoOrdenacao) {
		this.tipoOrdenacao = tipoOrdenacao;
	}
}
