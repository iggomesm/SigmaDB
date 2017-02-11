package br.com.sigmadb.beans.utilitarios;

import java.util.ArrayList;
import java.util.List;

import br.com.sigmadb.enumerations.TypeOperation;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.util.SigmaDBUtil;
import br.com.sigmadb.util.interfaces.DBOperation;
import br.com.sigmadb.util.interfaces.Filter;

/**
 * Classe responsável pela criação de filtros adicionais para as operações de Updates e Deletes.
 * @author Igor Moisés
 */
public class DBOperationFilter implements DBOperation {
	
	private List<Filter> listaFiltros;
	private TypeOperation typeOperation;

	/**
	 * Gera um filtro adicional para uma operação de Update ou Delete.
	 * @param typeOperation Tipo de operação que o filtro deve apontar.
	 * @param filter Filtro que deve ser aplicado a cláusula where da operação. 
	 */
	public DBOperationFilter(TypeOperation typeOperation, Filter filter) {
		
		this.validaRelationalOperationFilter(filter);
		this.validaTypeOperation(typeOperation);
		
		this.listaFiltros = new ArrayList<Filter>();
		this.listaFiltros.add(filter);
		
		this.typeOperation = typeOperation;
	}
	
	/**
	 * Gera um filtro adicional para uma operação de Update ou Delete.
	 * @param typeOperation Tipo de operação que o filtro deve apontar.
	 * @param filter Lista de filtros que deverão ser aplicados a cláusula where da operação. 
	 */
	public DBOperationFilter(TypeOperation typeOperation, List<Filter> filters) {
		
		if (SigmaDBUtil.isNullOrEmpty(filters)) {
			new SigmaDBException("Não é permitido a geração de um DBOperationFilter com filtros nulos.");
		}
		
		for (Filter filterList : filters) {
			
			this.validaRelationalOperationFilter(filterList);
		}
		
		this.validaTypeOperation(typeOperation);
		
		this.listaFiltros = new ArrayList<Filter>();
		this.listaFiltros.addAll(filters);
	}
	
	private void validaTypeOperation(TypeOperation typeOperation) {
		
		if (typeOperation == null){
			new SigmaDBException("Não é permitido incluir um tipo de operação nula para a geração de um DBOperationFilter.");
		}
		
		if (typeOperation == TypeOperation.INSERT) {
			new SigmaDBException("Não é permitido a geração de um DBOperationFilter para o tipo de operação INSERT.");
		}
	}
	
	private void validaRelationalOperationFilter(Filter filter) {
		
		if (filter == null) {
			new SigmaDBException("Não é permitido a geração de um DBOperationFilter com filtros nulos.");
		}
	}
	
	
	public List<Filter> getFilters() {
		return listaFiltros;
	}

	public TypeOperation getTypeOperation() {
		return this.typeOperation;
	}

}
