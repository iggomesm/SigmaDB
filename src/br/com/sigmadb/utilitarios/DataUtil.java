package br.com.sigmadb.utilitarios;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Clase utilit�ria para trabalhar com datas. <br>
 * <br>
 * Fun��es:<br>
 * Utilizada para calcular o intervalo entre duas datas.<br>
 * <br>
 * Diferen�as em: MiliSegundos, Segundos, Minutos Total, Minutos, Horas e Dias.<br>
 * <br>
 * Utlizada para retornar: dia do m�s, m�s e ano de uma determinada data.<br>
 * <br>
 * Nome do Arquivo: DataUtil.
 */
public class DataUtil {

	private Long diferencaMiliSegundos;

	private Long diferencaSegundos;

	private Long diferencaMinutosTotal;

	private Long diferencaMinutos;

	private Long diferencaHoras;

	private Long diferencaDias;

	private final Date dataInicial;

	private final Date dataFinal;

	private final GregorianCalendar gcInicial;

	private final GregorianCalendar gcFinal;

	private final Calendar cDataInicial;

	private final Calendar cDataFinal;

	private final Integer diaDataInicial;

	private final Integer mesDataInicial;

	private final Integer anoDataInicial;

	private final Integer diaDataFinal;

	private final Integer mesDataFinal;

	private final Integer anoDataFinal;

	/**
	 * 
	 * Construtor da Classe quando passado como parametro dois objetos Date.
	 * @param dDataInicial -
	 *          {@link Date}
	 * @param dDataFinal -
	 *          {@link Date}
	 */
	public DataUtil(Date dDataInicial, Date dDataFinal) {
		super();
		dDataInicial = this.validaDataInicial(dDataInicial);
		dDataFinal = this.validaDataFinal(dDataFinal);

		this.gcInicial = new GregorianCalendar();
		this.gcFinal = new GregorianCalendar();

		this.gcInicial.setTime(dDataInicial);
		this.gcFinal.setTime(dDataFinal);

		this.cDataInicial = this.gcInicial;
		this.cDataFinal = this.gcFinal;

		this.diaDataInicial = this.gcInicial.get(Calendar.DAY_OF_MONTH);
		this.mesDataInicial = this.gcInicial.get(Calendar.MONTH);
		this.anoDataInicial = this.gcInicial.get(Calendar.YEAR);

		this.diaDataFinal = this.gcFinal.get(Calendar.DAY_OF_MONTH);
		this.mesDataFinal = this.gcFinal.get(Calendar.MONTH);
		this.anoDataFinal = this.gcFinal.get(Calendar.YEAR);

		boolean b = this.cDataInicial.after(this.cDataFinal);
		b = this.cDataInicial.before(this.cDataFinal);

		this.dataInicial = dDataInicial;
		this.dataFinal = dDataFinal;

		this.criaObjeto();
	}

	/**
	 * 
	 * Construtor da Classe quando passado como parametro dois objetos String.
	 * 
	 * @param sDataInicial -
	 *          {@link String}
	 * @param sDataFinal -
	 *          {@link String}
	 */
	@Deprecated
	public DataUtil(final String sDataInicial, final String sDataFinal) {
		super();

		Date dataInicial = new Date(sDataInicial);
		Date dataFinal = new Date(sDataFinal);

		dataInicial = this.validaDataInicial(dataInicial);
		dataFinal = this.validaDataFinal(dataFinal);

		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;

		this.gcInicial = new GregorianCalendar();
		this.gcFinal = new GregorianCalendar();

		this.gcInicial.setTime(dataInicial);
		this.gcFinal.setTime(dataFinal);

		this.cDataInicial = this.gcInicial;
		this.cDataFinal = this.gcFinal;

		this.diaDataInicial = this.gcInicial.get(Calendar.DAY_OF_MONTH);
		this.mesDataInicial = this.gcInicial.get(Calendar.MONTH);
		this.anoDataInicial = this.gcInicial.get(Calendar.YEAR);

		this.diaDataFinal = this.gcFinal.get(Calendar.DAY_OF_MONTH);
		this.mesDataFinal = this.gcFinal.get(Calendar.MONTH);
		this.anoDataFinal = this.gcFinal.get(Calendar.YEAR);

		boolean b = this.cDataInicial.after(this.cDataFinal); // false
		b = this.cDataInicial.before(this.cDataFinal); // true

		this.criaObjeto();
	}

	/**
	 * Valor da diferen�a em Mili-Segundos.
	 * @return
	 */
	public Long getDiferencaMiliSegundos() {
		return this.diferencaMiliSegundos;
	}

	/**
	 * Valor da diferen�a em Segundos.
	 * 
	 * @return
	 */
	public Long getDiferencaSegundos() {
		return this.diferencaSegundos;
	}

	/**
	 * Valor da diferen�a em Minutos, contando o valor total de horas.
	 * @return
	 */
	public Long getDiferencaMinutosTotal() {
		return this.diferencaMinutosTotal;
	}

	/**
	 * Valor da diferen�a em Minutos sem contar com o total de horas.
	 * 
	 * @return
	 */
	public Long getDiferencaMinutos() {
		return this.diferencaMinutos;
	}

	/**
	 * Valor da diferen�a em Horas.
	 * 
	 * @return
	 */
	public Long getDiferencaHoras() {
		return this.diferencaHoras;
	}

	/**
	 * Valor da diferen�a em Dias.
	 * 
	 * @return
	 */
	public Long getDiferencaDias() {
		return this.diferencaDias;
	}

	/**
	 * Data Inicial
	 * 
	 * @return o valor do campo dataInicial.
	 */
	public Date getDataInicial() {
		return this.dataInicial;
	}

	/**
	 * Data Final
	 * 
	 * @return o valor do campo dataFinal.
	 */
	public Date getDataFinal() {
		return this.dataFinal;
	}

	/**
	 * Recupera o dia do m�s da Data Inicial
	 * 
	 * @return {@link Integer} o valor do dia do m�s da data inicial
	 */
	public Integer getDiaDataInicial() {
		return this.diaDataInicial;
	}

	/**
	 * Recupera o m�s da Data Inicial<br>
	 * <strong>Observa��o: O valor retornado � no padr�o JAVA, exemplo:<br>
	 * Janeiro = 0, Fevereiro = 1, ..., Dezembro = 11</strong>
	 * 
	 * @return {@link Integer} o valor do m�s da data inicial
	 */
	public Integer getMesDataInicial() {
		return this.mesDataInicial;
	}

	/**
	 * Recupera o ano da Data Inicial
	 * 
	 * @return {@link Integer} o valor do ano da data inicial
	 */
	public Integer getAnoDataInicial() {
		return this.anoDataInicial;
	}

	/**
	 * Recupera o dia do m�s da Data Final
	 * 
	 * @return {@link Integer} o valor do dia do m�s da data final
	 */
	public Integer getDiaDataFinal() {
		return this.diaDataFinal;
	}

	/**
	 * Recupera o m�s da Data Final<br>
	 * <strong>Observa��o: O valor retornado � no padr�o JAVA, exemplo:<br>
	 * Janeiro = 0, Fevereiro = 1, ..., Dezembro = 11</strong>
	 * 
	 * @return {@link Integer} o valor do m�s da data final
	 */
	public Integer getMesDataFinal() {
		return this.mesDataFinal;
	}

	/**
	 * Recupera o ano da Data Final
	 * 
	 * @return {@link Integer} o valor do ano da data final
	 */
	public Integer getAnoDataFinal() {
		return this.anoDataFinal;
	}

	/**
	 * M�todo utilizado para popular o objeto instanciado com os calculos de seus
	 * respectivos m�todos.
	 * 
	 */
	private void criaObjeto() {
		this.calculoDiferencaEmMiliSegundos();
		this.calculoDiferencaEmSegundos();
		this.calculoDiferencaEmMinutosTotal();
		this.calculoDiferencaEmMinutos();
		this.calculoDiferencaEmHoras();
		this.calculoDiferencaEmDias();
	}

	/**
	 * Calcula a diferen�a em Mili-Segundos.
	 * 
	 */
	private void calculoDiferencaEmMiliSegundos() {
		// Diferen�a em milisegundos
		final Long diferencaoMiliSegundos = this.cDataFinal.getTimeInMillis() - this.cDataInicial.getTimeInMillis();

		this.diferencaMiliSegundos = diferencaoMiliSegundos;
	}

	/**
	 * Calcula a diferen�a em Segundos.
	 * 
	 */
	private void calculoDiferencaEmSegundos() {
		// Diferen�a em segundos
		final Long diferencaSegundos = this.diferencaMiliSegundos / 1000;

		this.diferencaSegundos = diferencaSegundos;
	}

	/**
	 * Calcula a diferen�a em Minutos com o valor total de horas.
	 * 
	 */
	private void calculoDiferencaEmMinutosTotal() {
		final Long diferencaMinutos = this.diferencaMiliSegundos / (60 * 1000);

		this.diferencaMinutosTotal = diferencaMinutos;
	}

	/**
	 * Calcula a diferen�a em Minutos sem o valor total de horas.
	 * 
	 */
	private void calculoDiferencaEmMinutos() {
		final Long diferencaMinutos = this.diferencaMiliSegundos / (60 * 1000);

		// Diferen�a em horas
		final Long diferencaHoras = this.diferencaMiliSegundos / (60 * 60 * 1000);

		final Long minutos = diferencaMinutos - diferencaHoras * 60;

		this.diferencaMinutos = minutos;
	}

	/**
	 * Calcula a diferen�a em Horas.
	 * 
	 */
	private void calculoDiferencaEmHoras() {

		// Diferen�a em horas
		final Long diferencaHoras = this.diferencaMiliSegundos / (60 * 60 * 1000);

		this.diferencaHoras = diferencaHoras;

	}

	/**
	 * Calcula a diferen�a em Dias.
	 * 
	 */
	private void calculoDiferencaEmDias() {
		// Diferen�a em Dias
		final Long diferencaDias = this.diferencaMiliSegundos / (24 * 60 * 60 * 1000);

		this.diferencaDias = diferencaDias;
	}

	/**
	 * Cria uma nova inst�ncia para a Data Inicial caso a mesma seja nula[null],
	 * evitando assim erro de {@link NullPointerException}
	 * 
	 * @param dDataInicial
	 * @return dDataInicial
	 */
	private Date validaDataInicial(Date dDataInicial) {
		if ( dDataInicial == null ) {
			dDataInicial = new Date();
		}
		return dDataInicial;
	}

	/**
	 * Cria uma nova inst�ncia para a Data Final caso a mesma seja nula[null],
	 * evitando assim erro de {@link NullPointerException}
	 * 
	 * @param dDataFinal
	 * @return dDataFinal
	 */
	private Date validaDataFinal(Date dDataFinal) {
		if ( dDataFinal == null ) {
			dDataFinal = new Date();
		}
		return dDataFinal;
	}

}
