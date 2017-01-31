package br.com.sigmadb.utilitarios;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;

import br.com.sigmadb.beans.utilitarios.Ordenacao;
import br.com.sigmadb.enumerations.EnumSortType;


public class GenericComparator<T extends Object> implements Comparator<T> {

	private EnumSortType sortType = null;
	private String methodName = null;

	/**
	 * Constroi um GenericComparator de acordo com o campo e o tipo
	 * 
	 * @param sortField
	 *            Campo para ordenação
	 * @param sortType
	 *            Tipo de ordena��o, ascendente (ASC) ou descendente (DESC)
	 */
	public GenericComparator(String sortField, EnumSortType sortType) {
		this.sortType = sortType;
		this.methodName = ReflectionUtil.buildGetMethodName(sortField);
	}

	public int compare(T o1, T o2) {
		try {
			Method method1 = o1.getClass().getMethod(this.methodName, new Class[] {});
			Comparable comp1 = (Comparable) method1.invoke(o1, new Object[] {});
			
			if (comp1 == null){
				comp1 = (Comparable) "¬" /* CARACTER DE POSICAO 170 DA TABELA ASCII */;
			}
			
			Method method2 = o1.getClass().getMethod(this.methodName, new Class[] {});
			Comparable comp2 = (Comparable) method2.invoke(o2, new Object[] {});
						
			if (comp2 == null){
				comp2 = (Comparable) "¬" /* CARACTER DE POSICAO 170 DA TABELA ASCII */;
			} else if (comp2 instanceof Timestamp && comp1 instanceof String) {
				
				comp2 = (Comparable)new Day((Timestamp)comp2).toString();
				
			}
			
			return comp1.compareTo(comp2) * this.sortType.getIndex();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Organiza um List<T> de acordo com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que ser�o ordenados
	 * @param list
	 *            List<T> a ser ordenada
	 * @param sortField
	 *            Campo para ordena��o
	 * @param sortType
	 *            Tipo da ordena��o (ASC ou DESC)
	 */
	
	public static <T extends Object> void sortList(List<T> list, String sortField, EnumSortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		Collections.sort(list, comparator);
	}
	
	/**
	 * Organiza um List<T> de acordo com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que ser�o ordenados
	 * @param list
	 *            List<T> a ser ordenada
	 * @param sortField
	 *            Campos que ser�o utilizados para ordenar a lista.
	 */
	
	public static <T extends Object> void sortList(List<T> list, List<Ordenacao> sortFields) {
		ComparatorChain chain = new ComparatorChain();
		for (Ordenacao sortField : sortFields) {
			chain.addComparator(new GenericComparator<T>(sortField.getCampo(), sortField.getTipoOrdenacao()));
		}		
		Collections.sort(list, chain);
	}

	/**
	 * Organiza um T[] de acorco com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que ser�o ordenados
	 * @param array
	 *            T[] a ser ordenado
	 * @param sortField
	 *            Campo para ordena��o
	 * @param sortType
	 *            Tipo da ordena��o (ASC ou DESC)
	 */

	public static <T extends Object> void sortArray(T[] array, List<String> sortFields, EnumSortType sortType) {
		ComparatorChain chain = new ComparatorChain();
		for (String sortField : sortFields) {
			chain.addComparator(new GenericComparator<T>(sortField, sortType));
		}	
		Arrays.sort(array, chain);
	}
		
	/**
	 * Organiza um T[] de acorco com o campo e o tipo (ASC ou DESC)
	 * 
	 * @param <T>
	 *            Classe dos objetos que ser�o ordenados
	 * @param array
	 *            T[] a ser ordenado
	 * @param sortField
	 *            Campo para ordena��o
	 * @param sortType
	 *            Tipo da ordena��o (ASC ou DESC)
	 */

	public static <T extends Object> void sortArray(T[] array, String sortField, EnumSortType sortType) {
		GenericComparator<T> comparator = new GenericComparator<T>(sortField, sortType);
		Arrays.sort(array, comparator);
	}
}
