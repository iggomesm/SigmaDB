package br.com.sigmadb.beans.utilitarios;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import br.com.sigmadb.enumerations.OperadorRelacional;
import br.com.sigmadb.exceptions.SigmaDBException;
import br.com.sigmadb.utilitarios.Day;
import br.com.sigmadb.utilitarios.Filtro;

/**
 * Representa uma restrição para uma consulta com algum operador Relacional.
 * 
 * @author Igor Moisés
 * @param <E>
 * @see OperadorRelacional
 */
public class FiltroOperadorRelacional extends BeanFilter implements Filtro {

	private OperadorRelacional operadorRelacional;
	
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, String valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Integer valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, int valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Double valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, double valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Float valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, float valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Short valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, short valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Long valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, long valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Byte valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, byte valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Boolean valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, boolean valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Date valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Timestamp valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Time valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Blob valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Clob valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	public FiltroOperadorRelacional(String nomeColuna, OperadorRelacional operadorRelacional, Day valor) throws SigmaDBException{
		super(nomeColuna, null);
		this.preparaObjeto(nomeColuna, operadorRelacional, valor);
	}
	
	private <E> void preparaObjeto(String nomeColuna, OperadorRelacional operadorRelacional, E valor) throws SigmaDBException{
		
		if (valor == null){
			throw new SigmaDBException("O Filtro não aceita valores nulos.");
		}
		
		this.validaTipo(valor.getClass());
		
		this.setValor(valor, operadorRelacional);
		
		this.operadorRelacional = operadorRelacional;
	}
	
	private <E>void setValor(E valor, OperadorRelacional operadorRelacional) {
		
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
		
		Map<Class, ClassesPermitidas> mapaClasse = new HashMap<Class, FiltroOperadorRelacional.ClassesPermitidas>();
		
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

	public OperadorRelacional getOperadorRelacional() {
		return operadorRelacional;
	}

	public void setOperadorRelacional(OperadorRelacional operadorRelacional) {
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
			protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
				return "'" + valor + "'";
			}
		},
		NUMBER_BOOLEANO {
			protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
				return String.valueOf(valor);
			}
		},
		DATES {
			protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
				return "'" + String.valueOf(valor) + "'";
			}
		},
		TIMESTAMP {
			protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
				return "'" + String.valueOf(valor) + "'";
			}
		},
		DAY {
			protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
				
				String valorRetorno = "";
				
				Day data = (Day) valor;
				
				if (operador == OperadorRelacional.MAIOR_IGUAL || operador == OperadorRelacional.MAIOR_QUE) {
					valorRetorno = data.getDataBanco();
				} else if (operador == OperadorRelacional.MENOR_IGUAL || operador == OperadorRelacional.MENOR_QUE) {
					valorRetorno = data.getDataBanco23h();
				} else {
					return "'" + String.valueOf(data.toTimestamp()) + "'";
				}
				
				return valorRetorno;
			}
		};
		
		
		protected <E> String geraValorSQL(E valor, OperadorRelacional operador) {
			return null;
		}
	}
}
