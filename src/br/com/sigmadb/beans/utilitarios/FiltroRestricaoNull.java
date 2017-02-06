package br.com.sigmadb.beans.utilitarios;

import br.com.sigmadb.enumerations.RestricaoNull;
import br.com.sigmadb.utilitarios.Filtro;

/**
 * epresenta uma restrição para campos nulos numa consulta SQL. A saber: Is null ou Is Not Null.
 * @author Igor Moisés
 * @see RestricaoNull
 */
public class FiltroRestricaoNull extends BeanFilter implements Filtro {
	
	private RestricaoNull restricaoNull;
	

	/**
	 * @param nomeColuna
	 *            Nome da coluna onde a restriçãoo deverá ser aplicada.
	 * @param restricaoNull
	 *            Tipo da restrição que deverá ser aplicada.
	 */
	public FiltroRestricaoNull(String nomeColuna, RestricaoNull restricaoNull) {
		super(nomeColuna, null);
		this.restricaoNull = restricaoNull;
	}

	public RestricaoNull getRestricaoNull() {
		return restricaoNull;
	}

	public void setRestricaoNull(RestricaoNull RestricaoNull) {
		this.restricaoNull = RestricaoNull;
	}

	public String toString() {
		String sql = this.getNomePropriedade() + " " + this.restricaoNull.getDescricao();

		return sql;
	}

	public String getSQL() {
		return toString();
	}
}
