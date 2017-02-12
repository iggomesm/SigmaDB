package br.com.sigmadb.beans.utilitarios;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import br.com.sigmadb.enumerations.RelationalOperator;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.util.Day;
import br.com.sigmadb.util.SigmaDBUtil;
import br.com.sigmadb.util.interfaces.Filter;

/**
 * Representa uma restrição para uma consulta com algum operador Relacional.
 * 
 * @author Igor Moisés
 * @param <E>
 * @see RelationalOperator
 */
public class RelationalOperatorFilter extends BeanFilter implements Filter {

	private RelationalOperator operadorRelacional;
	
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, String valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Integer valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, int valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Double valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, double valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Float valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, float valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Short valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, short valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Long valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, long valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Byte valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, byte valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Boolean valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, boolean valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Date valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Timestamp valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Time valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Blob valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Clob valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public RelationalOperatorFilter(String nomeColuna, RelationalOperator operadorRelacional, Day valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	
	private <E> void preparaObjeto(String nomeColuna, RelationalOperator operadorRelacional, E valor) throws SigmaDBException{
		
		if (SigmaDBUtil.isNullOrEmpty(nomeColuna)){
			throw new SigmaDBException("Informe o nome de uma coluna para o filtro.");
		}
		
		if (valor == null){
			throw new SigmaDBException("O Filtro não aceita valores nulos.");
		}
		
		this.validaTipo(valor.getClass());
		
		this.setValor(valor, operadorRelacional);
		
		this.operadorRelacional = operadorRelacional;
	}
	
	private <E>void setValor(E valor, RelationalOperator operadorRelacional) {
		
		Map<Class, ClassesPermitidas> mapaClasse = mapeiaClassesPermitidas();
		
		String valorSql = mapaClasse.get(valor.getClass()).geraValorSQL(valor, operadorRelacional);
		
		this.setValorPropriedade(valorSql);
	}
	
	
	private void validaTipo(Class classe) throws SigmaDBException{
		
		Map<Class, ClassesPermitidas> mapaClasse = mapeiaClassesPermitidas();
		
		if (!mapaClasse.containsKey(classe)){
			throw new SigmaDBException("Classe não é suportada pelo filtro.");
		}
		
	}

	private Map<Class, ClassesPermitidas> mapeiaClassesPermitidas() {
		
		Map<Class, ClassesPermitidas> mapaClasse = new HashMap<Class, RelationalOperatorFilter.ClassesPermitidas>();
		
		mapaClasse.put(String.class, ClassesPermitidas.STRING);
		mapaClasse.put(Integer.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(int.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Double.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(double.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Float.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(float.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Short.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(short.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Long.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(long.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Byte.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(byte.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(Boolean.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(boolean.class, ClassesPermitidas.NUMBER_BOOLEANO);
		mapaClasse.put(java.sql.Timestamp.class, ClassesPermitidas.TIMESTAMP);
		mapaClasse.put(java.sql.Date.class, ClassesPermitidas.DATES);
		mapaClasse.put(Day.class, ClassesPermitidas.DAY);
		
		return mapaClasse;
	}

	public RelationalOperator getOperadorRelacional() {
		return operadorRelacional;
	}

	public void setOperadorRelacional(RelationalOperator operadorRelacional) {
		this.operadorRelacional = operadorRelacional;
	}

	@Override
	public String toString() {
		return this.getNomePropriedade() + " " + this.operadorRelacional.getValue() + " " + this.getValorPropriedade();
	}

	public String getSQL() {
		return toString();
	}
	
	private enum ClassesPermitidas {
		
		STRING {
			protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
				return "'" + valor + "'";
			}
		},
		NUMBER_BOOLEANO {
			protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
				return String.valueOf(valor);
			}
		},
		DATES {
			protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
				return "'" + String.valueOf(valor) + "'";
			}
		},
		TIMESTAMP {
			protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
				return "'" + String.valueOf(valor) + "'";
			}
		},
		DAY {
			protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
				
				String valorRetorno = "";
				
				Day data = (Day) valor;
				
				if (operador == RelationalOperator.MAIOR_IGUAL || operador == RelationalOperator.MAIOR_QUE) {
					valorRetorno = data.getDataBanco();
				} else if (operador == RelationalOperator.MENOR_IGUAL || operador == RelationalOperator.MENOR_QUE) {
					valorRetorno = data.getDataBanco23h();
				} else {
					return "'" + String.valueOf(data.toTimestamp()) + "'";
				}
				
				return valorRetorno;
			}
		};
		
		
		protected <E> String geraValorSQL(E valor, RelationalOperator operador) {
			return null;
		}
	}
}
