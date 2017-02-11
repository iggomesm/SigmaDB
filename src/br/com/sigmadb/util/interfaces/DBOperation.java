package br.com.sigmadb.util.interfaces;

import java.util.List;

import br.com.sigmadb.enumerations.TypeOperation;

/**
 * Engloba as operações do banco de dados.
 * @author Igor Moisés
 */
public interface DBOperation {

	/**
	 * Devolve todos os filtros para uma operação de banco de dados contido na própria operação.
	 * @return Lista de filtros.
	 */
	public List<Filter> getFilters();
	
	/**
	 * Devolve o tipo de operação que a interface representa.
	 * @return Tipo de operação que a interface representa.
	 */
	public TypeOperation getTypeOperation();
}
