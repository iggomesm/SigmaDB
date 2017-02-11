package br.com.sigmadb.beans.utilitarios;

import br.com.sigmadb.enumerations.RestricaoNull;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.util.SigmaDBUtil;
import br.com.sigmadb.util.interfaces.Filter;

/**
 * epresenta uma restrição para campos nulos numa consulta SQL. A saber: Is null ou Is Not Null.
 * @author Igor Moisés
 * @see RestricaoNull
 */
public class NullOperationFilter extends BeanFilter implements Filter {
	
	private RestricaoNull restricaoNull;
	

	/**
	 * @param nomeColuna
	 *            Nome da coluna onde a restriçãoo deverá ser aplicada.
	 * @param restricaoNull
	 *            Tipo da restrição que deverá ser aplicada.
	 * @throws SigmaDBException 
	 */
	public NullOperationFilter(String nomeColuna, RestricaoNull restricaoNull) throws SigmaDBException {
		super(nomeColuna, null);
		
		if (SigmaDBUtil.isNullOrEmpty(nomeColuna)) {
			throw new SigmaDBException("Informe o nome de uma coluna para o filtro.");
		}
		
		if (restricaoNull == null) {
			throw new SigmaDBException("Não é permitido criar um filtro sem um tipo de restrição do tipo Nulo.");
		}
		
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
