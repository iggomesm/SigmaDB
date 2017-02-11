package br.com.sigmadb.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import br.com.sigmadb.beans.utilitarios.BeanFilter;
import br.com.sigmadb.beans.utilitarios.Ordenacao;
import br.com.sigmadb.enumerations.EnumFiltroLista;
import br.com.sigmadb.enumerations.EnumSortType;
import br.com.sigmadb.enumerations.EnumTipoMapeamento;
import br.com.sigmadb.exceptions.SigmaDBException;

public class SigmaDBUtil {

	public static Locale loc_brasil = new Locale("pt", "BR");
	public static DecimalFormat decimalFormat = new DecimalFormat("0.00");
	public static final String MASCARA_CPF = "###.###.###-##";
	public static final String MASCARA_CNPJ = "##.###.###/####-##";
	public static final String MASCARA_TELEFONE = "(##) ####-####";
	public static final String MASCARA_XSD_DATE = "yyyy-MM-dd";
	public static final String MASCARA_XSD_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String MASCARA_XSD_TIME = "HH:mm:ss";
	public static final String ALGORITMO_MD5 = "MD5";

	/**
	 * Verifica se uma String é nula ou vazia.
	 * 
	 * @param valor
	 *            String a ser verificada.
	 * @return True caso a String seja nula ou esteja vazia, false caso seja não
	 *         seja nula e não esteja vazia.
	 */
	public static boolean isNullOrEmpty(final String valor) {
		return valor == null || valor.trim().isEmpty();
	}

	/**
	 * Verifica se uma Lista é nula ou vazia.
	 * 
	 * @param lista
	 *            lista a ser verificada.
	 * @return True caso a Lista seja nula ou esteja vazia, false caso seja não
	 *         seja nula e não esteja vazia.
	 */
	public static boolean isNullOrEmpty(List lista) {
		return (lista == null || lista.isEmpty());
	}

	/**
	 * 
	 * Se o valor for igual a 10 usar dd/MM/yyyy caso não, usar yyyy-MM-dd
	 * HH:mm:ss.mmm
	 */
	public static Timestamp converteStringTimestamp(String valor)
			throws Exception {
		if (valor == null) {
			return null;
		}

		if ((valor.indexOf('/') != -1) && (valor.length() < 10)) {
			DecimalFormat format = new DecimalFormat("00");
			String[] parts = valor.split("/");
			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < parts.length; i++) {
				buffer.append(format.format(Long.parseLong(parts[i])));

				if (i < (parts.length - 1)) {
					buffer.append("/");
				}
			}

			valor = buffer.toString();
		}

		return ((valor.length() == 10) ? new java.sql.Timestamp(FormatDate
				.parseDate(valor).getTime()) : FormatDate.parseTimestamp(valor,
				Constantes.DATA_FORMATO_COMPLETO));
	}

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(
					1, 3));
		}

		return sb.toString();
	}

	public static String md5(String message) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		return hex(md.digest(message.getBytes("CP1252")));
	}

	/**
	 * Este método retorna o nome da tabela através do nome do Bean. O Bean deve
	 * ter o mesmo nome da tabela.
	 * 
	 * @param bean
	 *            Objeto que terá seu nome capturado.
	 * @return String contendo o nome do Bean
	 */
	public static String pegaNomeTabela(Object bean) {
		String packageVo = bean.getClass().getPackage().getName();
		String classVo = bean.getClass().getName();

		return classVo.replaceAll((packageVo + "."), "").trim();
	}

	/**
	 * Se a String for nula ou vazia retorna 0 caso contrário retorna o numero.
	 * 
	 * @param valor
	 *            String que deverá ser convertida em valor inteiro.
	 * @return Valor inteiro, caso exista um valor na String. 0 caso não exista
	 *         nenhum valor na String.
	 */
	public static int parseStringToInt(String obj) {
		try {
			String string = (String) obj;

			return (((string == null) || "".equals(string)) ? 0 : Integer
					.parseInt(string));

		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Se a String for nula ou vazia retorna 0 caso contrário retorna o numero.
	 * 
	 * @param valor
	 *            String que deverá ser convertida em valor double.
	 * @return Valor double, caso exista um valor na String. 0 caso não exista
	 *         nenhum valor na String.
	 */
	public static double parseStringToDouble(String obj) {
		try {
			String string = (String) obj;

			return (((string == null) || "".equals(string)) ? 0 : Double
					.parseDouble(string));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Retorna o primeiro elemento de uma lista, caso ela não seja nula ou
	 * vazia.
	 * 
	 * @param list
	 *            Lista contendo o elemento a ser retornado.
	 * @return Primeiro elemento da lista do parâmetro, caso ela não esteja nula
	 *         ou vazia. Nulo caso a lista seja nula ou Vazia.
	 */
	public static <E> E getFirst(List<E> list) {
		return isNullOrEmpty(list) ? null : list.get(0);
	}

	/**
	 * Obtém o ultimo elemento de uma lista.
	 * 
	 * @param lista
	 *            Lista que deverá ter seu último elemento retornado.
	 * @return Último elmento da lista.
	 */
	public static <E> E getLast(List<E> lista) {
		E retorno = null;

		if (!isNullOrEmpty(lista)) {
			retorno = lista.get(lista.size() - 1);
		}

		return retorno;
	}

	/**
	 * Aplica uma formatação de máscara numa String.
	 * 
	 * @param valor
	 *            String contendo os dados que deverão ser mascarados.
	 * @param mascara
	 *            String representado a máscara que será aplicada no valor.
	 * @return Valor com a máscara informada.
	 * @throws ParseException
	 */
	public static String mascarar(String valor, String mascara)
			throws ParseException {
		MaskFormatter formatter = new MaskFormatter(mascara);
		JFormattedTextField textField = new JFormattedTextField();
		formatter.install(textField);
		textField.setText(valor);

		return textField.getText();
	}

	/**
	 * Aplica uma máscara de CPF na String.
	 * 
	 * @param cpf
	 *            String contendo os dados do CPF
	 * @return String contendo o CPF mascarado.
	 * @throws ParseException
	 */
	public static String mascararCpf(String cpf) throws ParseException {
		return mascarar(cpf, MASCARA_CPF);
	}

	/**
	 * Aplica uma máscara de CNPJ na String.
	 * 
	 * @param cnpj
	 *            String contendo os dados do CNPJ
	 * @return String contendo o CNPJ mascarado.
	 * @throws ParseException
	 */
	public static String mascararCnpj(String cnpj) throws ParseException {
		return mascarar(cnpj, MASCARA_CNPJ);
	}

	/**
	 * Aplica uma máscara de telefone numa String.
	 * 
	 * @param tel
	 *            String contendo os dígitos de um telefone.
	 * @return String formatada com a máscara de telefone.
	 * @throws ParseException
	 */
	public static String mascararTelefone(String tel) throws ParseException {
		return mascarar(tel, MASCARA_TELEFONE);
	}

	public static String dayOfWeek(Day dia) {
		Date agora = new Date(dia.getYear() - 1900, dia.getMonth() - 1,
				dia.getDay());
		String diaSemana = "";

		switch (agora.getDay()) {
		case 0:
			diaSemana = "Domingo";

			break;

		case 1:
			diaSemana = "Segunda";

			break;

		case 2:
			diaSemana = "Terca";

			break;

		case 3:
			diaSemana = "Quarta";

			break;

		case 4:
			diaSemana = "Quinta";

			break;

		case 5:
			diaSemana = "Sexta";

			break;

		case 6:
			diaSemana = "Sábado";

			break;
		}

		return diaSemana;
	}

	/**
	 * Remove a máscara de formatação de uma String mascarada como CPF.
	 * 
	 * @param cpf
	 *            String contendo o cpf.
	 * @return String contendo apenas os dígitos do cpf.
	 */
	public static String removeMascaraCPF(String cpf) {
		String cpfDesmascarado = null;

		if (!SigmaDBUtil.isNullOrEmpty(cpf)) {
			cpfDesmascarado = cpf.replaceAll("[.]*[-]*", "");
		}

		return cpfDesmascarado;
	}

	/**
	 * Remove a máscara de formatação de uma String mascarada como CNPJ.
	 * 
	 * @param cnpj
	 *            String contendo o cnpj.
	 * @return String contendo apenas os dígitos do cnpj.
	 */
	public static String removeMascaraCNPJ(String cnpj) {

		String cnpjDesmascarado = null;

		if (!SigmaDBUtil.isNullOrEmpty(cnpj)) {
			cnpjDesmascarado = cnpj.replaceAll("[.]*[-]*[/]*", "");
		}
		return cnpjDesmascarado;
	}	

	/**
	 * 
	 * @param lista
	 *            - Lista de Objetos Ex: List<Pessoa>
	 * @param nomeCampo
	 *            - nome do atributo de acordo como está declarado na classe
	 * @param enumSortType
	 *            - EnumSortType.DESC - Ordem Decrescente , EnumSortType.ASC
	 * 
	 *            Exemplo da chamada do metodo :
	 * 
	 *            List<Pessoa> lista = new ArrayList<Pessoa>();
	 * 
	 *            Pessoa pessoa = new Pessoa(); pessoa.setPess_id(3);
	 *            pessoa.setPess_nome("Ciclano");
	 * 
	 *            lista.add(pessoa);
	 * 
	 *            Util.ordenaLista(lista, "pess_nome", EnumSortType.ASC);
	 *            Util.ordenaLista(lista, "pess_nome", EnumSortType.DESC);
	 * 
	 */
	public static <T extends Object> void ordenaLista(List<T> lista,
			String nomeCampo, EnumSortType enumSortType) {
		GenericComparator.sortList(lista, nomeCampo, enumSortType);
	}

	/**
	 * @param lista
	 *            - Lista de Objetos Ex: List<Pessoa>
	 * @param campos
	 *            - Lista de strings com os nomes dos campos que serão
	 *            utilizados na ordenação.
	 */
	public static <T extends Object> void ordenaLista(List<T> lista,
			List<Ordenacao> campos) {
		GenericComparator.sortList(lista, campos);
	}

	public static String formataStringComEspacosEmBranco(String texto) {
		String[] quebra = texto.split(" ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < quebra.length; i++) {
			if (!quebra[i].equals("")) {
				sb.append(quebra[i] + " ");
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Responsável por receber uma lista de Beans e retornar uma nova lista
	 * somente com os valores da propriedade solicitada.
	 * 
	 * @param listaBean
	 *            Lista de Beans que deverá ser manipulada.
	 * @param propriedade
	 *            Nome do atributo do objeto Bean da lista que deverá ser
	 *            retornado na nova lista.
	 * @param removeValoresZerados
	 *            Indica se os valores nulos ou iguais a zero deverão ser
	 *            incluídos na nova lista. Caso true os valores nulos ou iguais
	 *            a zero serão removidos. Caso false não serão.
	 * @return Lista com os valores solicitados.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List listaPropriedadeObjeto(List listaBean,
			String propriedade, boolean removeValoresZerados)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return listaPropriedadeObjeto(listaBean, propriedade,
				removeValoresZerados, false);
	}

	/**
	 * Responsável por receber uma lista de Beans e retornar uma nova lista
	 * somente com os valores da propriedade solicitada.
	 * 
	 * @param listaBean
	 *            Lista de Beans que deverá ser manipulada.
	 * @param propriedade
	 *            Nome do atributo do objeto Bean da lista que deverá ser
	 *            retornado na nova lista.
	 * @param removeValoresZerados
	 *            Indica se os valores nulos ou iguais a zero deverão ser
	 *            incluídos na nova lista. Caso true os valores nulos ou iguais
	 *            a zero serão removidos. Caso false não serão.
	 * @param removeValoresRepetidos
	 *            Caso True remove os valores repeditos da lista, caso false
	 *            permite que a lista contenha valores repetidos.
	 * @return Lista com os valores solicitados.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List listaPropriedadeObjeto(List listaBean,
			String propriedade, boolean removeValoresZerados,
			boolean removeValoresRepetidos) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		return listaPropriedadeObjeto(listaBean, propriedade,
				removeValoresZerados, removeValoresRepetidos, false);
	}

	/**
	 * Responsável por receber uma lista de Beans e retornar uma nova lista
	 * somente com os valores da propriedade solicitada.
	 * 
	 * @param listaBean
	 *            Lista de Beans que deverá ser manipulada.
	 * @param propriedade
	 *            Nome do atributo do objeto Bean da lista que deverá ser
	 *            retornado na nova lista.
	 * @param removeValoresZerados
	 *            Indica se os valores nulos ou iguais a zero deverão ser
	 *            incluídos na nova lista. Caso true os valores nulos ou iguais
	 *            a zero serão removidos. Caso false não serão.
	 * @param removeValoresRepetidos
	 *            Caso True remove os valores repeditos da lista, caso false
	 *            permite que a lista contenha valores repetidos.
	 * @param valorEntreAspas
	 *            Caso True adiciona as apas simples nos valores da lista, caso
	 *            false nao inclui as aspas simples.
	 * @return Lista com os valores solicitados.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List listaPropriedadeObjeto(List listaBean,
			String propriedade, boolean removeValoresZerados,
			boolean removeValoresRepetidos, boolean valorEntreAspas)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		List resultado = new ArrayList();

		for (Object vo : listaBean) {
			Object valor = SigmaDBReflectionUtil.getValorMetodoGet(vo, propriedade);
			boolean adicionaValor = true;
			Class classeDoValor = SigmaDBReflectionUtil.pegaTipoDoMetodoGet(vo,
					SigmaDBReflectionUtil.getNomeMetodoGet(propriedade));

			if (removeValoresZerados) {

				if (classeDoValor.getName().equalsIgnoreCase(
						String.class.getName())) {
					adicionaValor = !SigmaDBUtil.isNullOrEmpty((String) valor);
				} else if (verificaSeTipoNumerico(classeDoValor)) {
					adicionaValor = (SigmaDBUtil.parseStringToInt(String
							.valueOf(valor)) != 0);
				}
			}

			if (adicionaValor && removeValoresRepetidos && valor != null) {
				adicionaValor = !resultado.contains(valor);
			}

			if (adicionaValor && valor != null) {
				resultado.add(valor);
			}
		}

		return resultado;
	}

	private static boolean verificaSeTipoNumerico(Class classeDoValor) {
		boolean ehTipoNumerico = Integer.class.getName().equalsIgnoreCase(
				classeDoValor.getName())
				|| int.class.getName()
						.equalsIgnoreCase(classeDoValor.getName())
				|| Double.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| double.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| Float.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| float.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| Long.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| long.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| Byte.class.getName().equalsIgnoreCase(
						classeDoValor.getName())
				|| byte.class.getName().equalsIgnoreCase(
						classeDoValor.getName());

		return ehTipoNumerico;
	}

	/**
	 * Método responsável por realizar um filtro na numa lista.
	 * 
	 * @param lista
	 *            Lista que deverá ser filtrada.
	 * @param filtro
	 *            Objeto que determinará a condição de filtro para a lista.
	 * @param metodoFiltro
	 *            Tipo do método de filtro a ser utilizado. Ver
	 *            {@link EnumFiltroLista}.
	 * @param listaRecipente
	 *            <b><font color='red'>Parâmetro opicional.</font></b> Este
	 *            parâmetro só precisará ser preenchido com algum valor caso o
	 *            método de filtro seja do tipo
	 *            EnumFiltroLista.ADICIONA_ELEMENTOS_CORRESPONDENTES ou
	 *            EnumFiltroLista.ADICIONA_ELEMENTOS_NAO_CORRESPONDENTES.
	 * @return Lista filtrada de acordo com os parâmetros informados.
	 * @throws Exception
	 */
	public static List filtraLista(List lista, BeanFilter filtro,
			EnumFiltroLista metodoFiltro) throws Exception {
		ordenaLista(lista, filtro.getNomePropriedade(), EnumSortType.ASC);
		return aplicaFiltroEmLista(lista, new FiltroListaGenerico<Object>(
				filtro), metodoFiltro.getIndex());
	}

	/**
	 * Método responsável por realizar um filtro na numa lista.
	 * 
	 * @param lista
	 *            Lista que deverá ser filtrada.
	 * @param filtros
	 *            Lista de objetos de filtro que determinará as condições de
	 *            filtro para a lista.<br>
	 *            <b>Caso seja necessário um filtro do tipo IN da consulta sql,
	 *            aplique vários objetos de filtro da lista apontando para uma
	 *            mesma propriedade. Ex:</b><br>
	 *            <font color = 'green'>
	 *            {@code
	 *  
	 * List<FiltroVO> listaFiltro = new ArrayList<FiltroVO>();}<br>
	 *            {@code listaFiltro.add(new FiltroVO("pess_nome", "Jose"));}<br>
	 *            {@code listaFiltro.add(new FiltroVO("pess_nome", "Gil"));}<br>
	 *            {@code listaFiltro.add(new FiltroVO("pess_nome", "Gal"));}<br>
	 * 
	 *            </font>
	 * @param metodoFiltro
	 *            Tipo do método de filtro a ser utilizado. Ver
	 *            {@link EnumFiltroLista}.
	 * @return Lista filtrada de acordo com os parâmetros informados.
	 * @throws Exception
	 */
	public static List filtraLista(List lista, List<BeanFilter> filtros,
			EnumFiltroLista metodoFiltro) throws Exception {

		validaFiltrosDeLista(filtros);

		List<Ordenacao> listaOrdenacao = new ArrayList<Ordenacao>();
		for (BeanFilter filtro : filtros) {
			listaOrdenacao.add(new Ordenacao(filtro.getNomePropriedade(),
					EnumSortType.ASC));
		}
		ordenaLista(lista, listaOrdenacao);
		return aplicaFiltroEmLista(lista, new FiltroListaGenerico<Object>(
				filtros), metodoFiltro.getIndex());
	}

	/**
	 * Método utilizado para identificar o tipo de filtragem de uma lista e
	 * aplica-la a mesma. Esta funçõo não suporta mais de um filtro para uma
	 * mesma propriedade somado a um filtro para uma outra propriedade.<br>
	 * <b>Exemplo incorreto:</b><br>
	 * <font color = 'red'>
	 * {@code  List<FiltroVO> listaFiltro = new ArrayList<FiltroVO>();}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Gil"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Gal"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_id", "1"));} </font> <br>
	 * <b>Exemplo correto:</b><br>
	 * <font color = 'green'>
	 * {@code
	 *  
	 * List<FiltroVO> listaFiltro = new ArrayList<FiltroVO>();}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Jose"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Gil"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Gal"));}<br>
	 * <b><font color='black'>OU</font></b><br>
	 * {@code List<FiltroVO> listaFiltro = new ArrayList<FiltroVO>();}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome", "Jose"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_id", "Gil"));}<br>
	 * {@code listaFiltro.add(new FiltroVO("pess_nome_outra_propriedade", "Valor propriedade"));}
	 * <br>
	 * </font>
	 * 
	 * @param lista
	 *            Lista que conterá os elementos a serem filtrados.
	 * @param filtro
	 *            Objeto que contem as regras de filtragem.
	 * @param metodoFiltro
	 *            Modelo de filtragem a ser utilizado.
	 * @return Lista filtrada.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static List aplicaFiltroEmLista(List lista, FiltroLista filtro,
			int metodoFiltro) throws Exception {
		List listaRetorno = new ArrayList();
		switch (metodoFiltro) {
		case 0 /* BUSCA_TODOS_CORRESPONDENTES */:

			listaRetorno = FiltroListaUtils.buscaTodosCorrespondentes(lista,
					filtro);
			break;

		case 1 /* BUSCA_TODOS_NAO_CORRESPONDENTES */:

			listaRetorno = FiltroListaUtils.buscaTodosNaoCorrespondentes(lista,
					filtro);
			break;

		case 4 /* BUSCA_PRIMEIRO_CORRESPONDENTE */:

			Object elementoCorrespondente = FiltroListaUtils
					.buscaPrimeiroElementoCorrespondente(lista, filtro);
			listaRetorno = new ArrayList();

			if (elementoCorrespondente != null) {
				listaRetorno.add(elementoCorrespondente);
			}
			break;

		case 5 /* BUSCA_PRIMEIRO_NAO_CORRESPONDENTE */:

			Object elementoNaoCorrespondente = FiltroListaUtils
					.buscaPrimeiroElementoNaoCorrespondente(lista, filtro);
			listaRetorno = new ArrayList();
			if (elementoNaoCorrespondente != null) {
				listaRetorno.add(elementoNaoCorrespondente);
			}

			break;

		case 6 /* REMOVE_TODOS_CORRESPONDENTES */:

			FiltroListaUtils.removeTodosCorrespondentes(lista, filtro);
			break;

		case 7 /* RETEM_TODOS_CORRESPONDENTES */:

			FiltroListaUtils.retemApenasCorrespondentes(lista, filtro);
			break;

		default:
			break;
		}

		return listaRetorno;
	}

	/**
	 * Valida se a lista de filtros informados na função Util.filtraLista, estão
	 * voltados para propriedades distintas ou uma única propriedade. A função
	 * não suporta mais de um filtro para uma mesma propriedade somado a um
	 * filtro para uma outra propriedade.
	 * 
	 * @param filtroVO
	 *            Lista contendo os filtros que serão validados.
	 * @throws Exception
	 */
	private static void validaFiltrosDeLista(List<BeanFilter> filtroVO)
			throws Exception {

		List<BeanFilter> listaFiltros = new ArrayList<BeanFilter>();
		listaFiltros.addAll(filtroVO);

		Map<String, List> mapaFiltros = new HashMap<String, List>();
		mapeiaElementos(listaFiltros, mapaFiltros, "nomePropriedade",
				EnumTipoMapeamento.MAPEIA_VALORES_EM_LISTA_DE_OBJETOS);

		Set<String> chaves = mapaFiltros.keySet();

		boolean possuiFiltrosRepetidos = false;

		for (String chave : chaves) {
			List filtros = mapaFiltros.get(chave);

			if (possuiFiltrosRepetidos) {
				String msg = "\nErro ao utilizar a Util.filtraLista. Ao utilizar uma restriçãoo do tipo \"IN\", só poderão ser informados na lista de filtros, objetos do tipo"
						+ "Filtro que tenham o atributo nomePropriedade com o valor apontando para uma mesma propriedade do Bean a ser filtrado.";
				throw new Exception(msg);
			}

			possuiFiltrosRepetidos = filtros.size() > 1;

		}

	}

	/**
	 * Recebe uma lista e retorna uma String com os valores solicitados
	 * separados por vírgulas.<br>
	 * Ex. (a,b,c,d,e,f,g)<br>
	 * <b>Caso a lista seja de tipos primitivos, o parâmetro nomeAtributo deverá
	 * ser setado como null.</b>
	 * 
	 * @param lista
	 *            Lista contendo os valores a serem concatenados.
	 * @param nomeAtributo
	 *            Nome do atributo do VO que deverá ser apontado na concatenção.<br>
	 *            Caso a lista seja de tipos primitivos, este parâmetro deve ser
	 *            setado como null.
	 * @return String contendo todos os valores solicitados na função separados
	 *         por vírgula.
	 * @throws Exception
	 */
	public static String concatenaValoresDaLista(List lista, String nomeAtributo)
			throws Exception {
		return concatenaValoresDaLista(lista, nomeAtributo, false);
	}

	/**
	 * Recebe uma lista e retorna uma String com os valores solicitados
	 * separados por vírgulas.<br>
	 * Ex. (a,b,c,d,e,f,g)<br>
	 * <b>Caso a lista seja de tipos primitivos, o parâmetro nomeAtributo deverá
	 * ser setado como null.</b>
	 * 
	 * @param lista
	 *            Lista contendo os valores a serem concatenados.
	 * @param nomeAtributo
	 *            Nome do atributo do VO que deverá ser apontado na concatenção.<br>
	 *            Caso a lista seja de tipos primitivos, este parâmetro deve ser
	 *            setado como null.
	 * @param insereAspasSimples
	 *            Boleano que indica se cada valor deverá estar entre aspas
	 *            simples.
	 * 
	 * @return String contendo todos os valores solicitados na função separados
	 *         por vírgula.
	 * @throws Exception
	 */
	public static String concatenaValoresDaLista(List lista,
			String nomeAtributo, boolean insereAspasSimples) throws Exception {
		StringBuilder retorno = new StringBuilder("");

		List listaValores;

		if (SigmaDBUtil.isNullOrEmpty(nomeAtributo)) {
			listaValores = lista;
		} else {
			listaValores = listaPropriedadeObjeto(lista, nomeAtributo, true);
		}

		for (Object object : listaValores) {
			retorno.append(String.valueOf(object) + ", ");
		}

		String propriedadesListadas = retorno.length() >= 2 ? retorno
				.substring(0, retorno.length() - 2) : null;
		return propriedadesListadas;
	}

	/**
	 * 
	 * Responsável por realizar o mapeamento de elementos de uma lista.
	 * 
	 * @param listaElementos
	 *            Lista dos elementos que deverão ser mapeados.
	 * 
	 * @param mapa
	 *            Mapa com seus respectivos tipos de chave e valor que deverá
	 *            armazenar o mapeamento dos valores.<br>
	 *            Ou seja, informe aqui qualquer tipo de {@code Map<K,V>}.
	 * 
	 * @param campoChave
	 *            Descricao do atributo que servirá como chave no mapeamento.<br>
	 *            <font color ='Blue'><b>OBS: O tipo do atributo que esta
	 *            descrição representa, deverá ser do mesmo tipo da chave do
	 *            mapa informado acima.</b></font> <br>
	 *            <font color ='Green'><b>Caso este campo seja nulo, o método
	 *            assumirá que o próprio valor deverá ser a chave do
	 *            mapa.</b></font>
	 * 
	 * @param transformaChaveEmString
	 *            Informe true caso o atributo informado acima não seja uma
	 *            String e a chave do mapa deve ser uma String com o valor do
	 *            atributo. Informe false caso contrario.
	 * 
	 * @param tipo
	 *            Tipo de critério de mapeamento. {@link EnumTipoMapeamento}
	 * 
	 * @return Mapa do mesmo tipo informado no parâmetro, contendo a estrutura
	 *         da lista mapeada.
	 * 
	 * @see {@link EnumTipoMapeamento}
	 * 
	 * @throws Exception
	 */
	public static <K extends Object, V extends Object> void mapeiaElementos(
			List listaElementos, Map<K, V> mapa, String campoChave,
			EnumTipoMapeamento tipo) throws Exception {

		if (tipo == EnumTipoMapeamento.MAPEIA_VALORES_EM_OBJETO) {
			mapeiaValoresEmObjeto(listaElementos, mapa, campoChave, false);
		} else {
			mapeiaValoresEmListaDeObjetos(listaElementos, mapa, campoChave,
					false);
		}
	}

	/**
	 * Recebe uma lista e realiza o mapeamento dos valores desta lista num mapa
	 * do tipo {@code Map<K, List<T>>}
	 * 
	 * @param listaElementos
	 *            Lista de elementos que deverá ser mapeado.
	 * 
	 * @param mapa
	 *            Mapa que deverá armazenar os agrupamentos de informações.
	 * 
	 * @param campoChave
	 *            Descricao do atributo que servirá como chave no mapeamento.<br>
	 *            <font color ='Blue'><b>OBS: O tipo do atributo que esta
	 *            descrição representa, deverá ser do mesmo tipo da chave do
	 *            mapa informado acima.</b></font> <br>
	 *            <font color ='Green'><b>Caso este campo seja nulo, o método
	 *            assumirá que o próprio valor deverá ser a chave do
	 *            mapa.</b></font>
	 * 
	 * @param transformaChaveEmString
	 *            Informe true caso o atributo informado acima não seja uma
	 *            String e a chave do mapa deve ser uma String com o valor do
	 *            atributo. Informe false caso contrario.
	 * 
	 * @returnMapa do mesmo tipo informado no parâmetro, contendo a estrutura da
	 *             lista mapeada.
	 * 
	 * @throws Exception
	 */
	private static <K extends Object, V extends Object, T extends Object> void mapeiaValoresEmListaDeObjetos(
			List listaElementos, Map<K, V> mapa, String campoChave,
			boolean transformaChaveEmString) throws Exception {

		if (mapa == null) {
			mapa = new HashMap<K, V>();
		}

		List<T> listaValores = null;

		for (Iterator iterator = listaElementos.iterator(); iterator.hasNext();) {

			T valor = (T) iterator.next();

			K chaveMapa = (K) valor;

			if (!SigmaDBUtil.isNullOrEmpty(campoChave)) {
				chaveMapa = (K) SigmaDBReflectionUtil.getValorMetodoGet(valor,
						campoChave);
				if (transformaChaveEmString) {
					chaveMapa = (K) String.valueOf(chaveMapa);
				}
			}

			listaValores = (List<T>) mapa.get(chaveMapa);

			if (listaValores == null) {
				listaValores = new ArrayList<T>();
				mapa.put(chaveMapa, (V) listaValores);
			}

			listaValores.add(valor);

		}
	}

	/**
	 * Recebe uma lista e realiza o mapeamento dos valores desta lista num mapa
	 * do tipo {@code Map<K, V>}
	 * 
	 * @param listaElementos
	 *            Lista de elementos que deverá ser mapeado.
	 * 
	 * @param mapa
	 *            Mapa que deverá armazenar os agrupamentos de informações.
	 * 
	 * @param campoChave
	 *            Descricao do atributo que servirá como chave no mapeamento.<br>
	 *            <font color ='Blue'><b>OBS: O tipo do atributo que esta
	 *            descriçõo representa, deverá ser do mesmo tipo da chave do
	 *            mapa informado acima.</b></font> <br>
	 *            <font color ='Green'><b>Caso este campo seja nulo, o método
	 *            assumirá que o próprio valor deverá ser a chave do
	 *            mapa.</b></font>
	 * 
	 * @param transformaChaveEmString
	 *            Informe true caso o atributo informado acima não seja uma
	 *            String e a chave do mapa deve ser uma String com o valor do
	 *            atributo. Informe false caso contrario.
	 * 
	 * @returnMapa do mesmo tipo informado no parâmetro, contendo a estrutura da
	 *             lista mapeada.
	 * 
	 * @throws Exception
	 */
	private static <K extends Object, V extends Object> void mapeiaValoresEmObjeto(
			List listaElementos, Map<K, V> mapa, String campoChave,
			boolean transformaChaveEmString) throws Exception {

		if (mapa == null) {
			mapa = new HashMap<K, V>();
		}

		for (Iterator iterator = listaElementos.iterator(); iterator.hasNext();) {

			V valor = (V) iterator.next();

			K chaveMapa = (K) valor;

			if (!SigmaDBUtil.isNullOrEmpty(campoChave)) {
				chaveMapa = (K) SigmaDBReflectionUtil.getValorMetodoGet(valor,
						campoChave);
				if (transformaChaveEmString) {
					chaveMapa = (K) String.valueOf(chaveMapa);
				}
			}

			mapa.put(chaveMapa, valor);
		}
	}

	/**
	 * Este método captura o nome dos atributos de um VO que devem ser excluídos
	 * numa consulta.<br>
	 * <b>Informe o(s) prefixo(s) ou nome(s) de atributo(s) que deverão entrar
	 * no array que será retornado para ser colocado nas propriedades excluídas
	 * da consulta.</b>
	 * 
	 * @param objectBean
	 *            Objeto que contem os atributos a ser listado.
	 * @param prefixosAtributosExclusao
	 *            Prefixos ou nome dos atributos que deverá ser listados.
	 * @return <b>Array que deverá ser inserido nas propriedades excluídas da
	 *         consulta, contendo o nome dos atributos solicitados.</b>
	 */
	public static String[] pegaAtributosExcluirConsulta(Object objectBean,
			String... prefixosAtributosExclusao) {

		List nomesAtributos = SigmaDBReflectionUtil
				.listaNomeDosAtributosDoObjetoVO(objectBean);

		List<String> listaAtributosExclusao = new ArrayList<String>();

		for (Iterator iterator = nomesAtributos.iterator(); iterator.hasNext();) {
			String nomeAtributo = (String) iterator.next();

			int indice = 0;
			boolean deveExcluir = false;

			while (indice < prefixosAtributosExclusao.length && !deveExcluir) {
				deveExcluir = nomeAtributo
						.startsWith(prefixosAtributosExclusao[indice]);
				indice++;
			}

			if (deveExcluir) {
				listaAtributosExclusao.add(nomeAtributo);
			}
		}
		String[] excluidas = listaAtributosExclusao.toArray(new String[0]);

		return excluidas;
	}

	protected static double aplicaConversaoMonetaria(String valor)
			throws SigmaDBException {

		valor = removeMascaraMonetaria(valor);

		try {
			return Double.parseDouble(valor);
		} catch (Exception e) {
		}

		StringBuffer retorno = new StringBuffer();

		if (valor.indexOf(",") != -1) {
			String[] v = valor.split(",");
			retorno.append(pegaValorInteiro(v[0]));

			if (!"00".equals(v[1])) {
				retorno.append("." + pegaValorInteiro(v[1]));
			}
		} else {
			retorno.append(pegaValorInteiro(valor));
		}

		if (valor.startsWith("-")) {
			return Double.parseDouble("-" + retorno.toString());
		}

		return Double.parseDouble(retorno.toString());
	}

	private static String pegaValorInteiro(String numero) {
		if ((numero == null) || "".equals(numero)) {
			return "0";
		}

		char[] valores = numero.toCharArray();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < valores.length; i++) {
			if (Character.isDigit(valores[i])) {
				buffer.append(valores[i]);
			}
		}

		return buffer.toString();
	}

	private static String removeMascaraMonetaria(String numero) {
		if ((numero == null) || "".equals(numero)) {
			return null;
		}

		char[] valores = numero.toCharArray();
		StringBuffer buffer = new StringBuffer();

		if ((valores.length > 0) && ('-' == valores[0])) {
			buffer.append("-");
		}

		for (int i = 0; i < valores.length; i++) {
			if (Character.isDigit(valores[i])) {
				buffer.append(valores[i]);
			} else if (',' == valores[i]) {
				buffer.append(valores[i]);
			} else if ('.' == valores[i]) {
				buffer.append(valores[i]);
			}
		}

		return buffer.toString();
	}

	/**
	 * Aplica aspas simples entre uma String.
	 * 
	 * @param texto
	 *            String que deverá receber as aspas.
	 * @return String contendo aspas simples.
	 */
	public static String aspasSimples(String texto) {
		return "'" + texto + "'";
	}

}
