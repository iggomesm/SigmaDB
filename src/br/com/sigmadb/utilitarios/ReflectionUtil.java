package br.com.sigmadb.utilitarios;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import br.com.sigmadb.annotations.PKTableMaster;
import br.com.sigmadb.annotations.RemoveAtributoConsulta;

/**
 * Classe utilitária para reflexão.
 * 
 */
public class ReflectionUtil {

	/**
	 * Prefixo <code>get</code> métodos
	 */
	public final static String PREFIXO_METODO_GET = "get";

	public final static String PREFIXO_METODO_GET_BOLEANO = "is";

	/**
	 * Sufixo <code>set</code> de métodos
	 */
	public final static String PREFIXO_METODO_SET = "set";

	/**
	 * Método responsável por retornar um campo ({@link Field}) de uma classe (
	 * {@link Class}) via reflection.
	 * 
	 * <p>
	 * <b>OBS: O controle de acesso do campo pode ser qualquer um, ou seja,
	 * tanto faz ser <code>protected</code>, <code>private</code>,
	 * <code>public</code> e etc.</b>
	 * 
	 * <p>
	 * <b>Exemplo</b>:
	 * <code><p>final String nomeCampo = "pergunta"	+ pesquisa_respostas.getPere_pergunta();
	 * 		<p>Field campo = Util.getCampoDaClasse(operadoraANS.getClass(), nomeCampo);
	 * 		<p>final String pere_resposta = pesquisa_respostas.getPere_resposta();
	 * 		<p>campo.set(operadoraANS, pere_resposta);</code>
	 * 
	 * @param classe
	 *            Classe ({@link Class}) com o campo ({@link Field}) a ser
	 *            retornado
	 * 
	 * @param nomeCampo
	 *            Nome do campo ({@link Field}) a ser retornado. Caso não ache o
	 *            campo na classe, retorna <code>null</code>.
	 * 
	 * @return Campo ({@link Field}) a ser retornado
	 */
	public static Field getCampoDaClasse(final Class<?> classe,
			final String nomeCampo) {

		Field retorno = null;

		final Field[] declaredFields = classe.getDeclaredFields();

		for (Field field : declaredFields) {

			if (nomeCampo.equals(field.getName())) {
				field.setAccessible(true);
				retorno = field;
				break;
			}
		}

		return retorno;
	}

	/**
	 * Método responsável por retornar um método ({@link Method})
	 * <code><b>set</b></code> de uma classe ( {@link Class}) via reflection.
	 * 
	 * @param classe
	 *            Classe ({@link Class}) com o campo ({@link Field}) a ser
	 *            retornado
	 * 
	 * @param nomePropriedade
	 *            Nome da propriedade ({@link Field}) a ser pesquisado o método
	 *            <code><b>set</b></code>. Caso não ache o método na classe,
	 *            retorna <code>null</code>.
	 *            <p>
	 *            <b>Exemplo</b>:
	 *            <code><p> se o nomePropriedade for "pergunta" então o código vai buscar 
	 *             e retornar o método set, no caso, setPergunta.
	 * 
	 * @return Método ({@link Method}) a ser retornado
	 */
	public static Method getMetodoSetNaClasse(final Class<?> classe,
			final String nomePropriedade) {

		return getMetodoNaClasse(classe, nomePropriedade, PREFIXO_METODO_SET);

	}

	/**
	 * Método responsável por retornar um método ({@link Method})
	 * <code><b>get</b></code> de uma classe ( {@link Class}) via reflection.
	 * 
	 * 
	 * @param classe
	 *            Classe ({@link Class}) com o campo ({@link Field}) a ser
	 *            retornado
	 * 
	 * @param nomePropriedade
	 *            Nome da propriedade ({@link Field}) a ser pesquisado o método
	 *            <code><b>get</b></code>. Caso não ache o método na classe,
	 *            retorna <code>null</code>.
	 *            <p>
	 *            <b>Exemplo</b>:
	 *            <code><p> se o nomePropriedade for "pergunta" então o código vai buscar 
	 *             e retornar o método get, no caso, getPergunta.
	 * 
	 * @return Método ({@link Method}) a ser retornado
	 */
	public static Method getMetodoGetNaClasse(final Class<?> classe,
			final String nomePropriedade) {

		return getMetodoNaClasse(classe, nomePropriedade, PREFIXO_METODO_GET);

	}

	/**
	 * Método responsável por retornar um método ({@link Method}) de uma classe
	 * ( {@link Class}) via reflection.
	 * 
	 * 
	 * @param classe
	 *            Classe ({@link Class}) com o campo ({@link Field}) a ser
	 *            retornado
	 * 
	 * @param nomePropriedade
	 *            Nome da propriedade ({@link Field}) a ser pesquisado o método.
	 * 
	 * @param tipoMetodo
	 *            Informa o tipo de método, se método get ou set.
	 * 
	 * @return Método ({@link Method}) a ser retornado
	 */
	private static Method getMetodoNaClasse(final Class<?> classe,
			final String nomePropriedade, final String tipoMetodo) {

		Method retorno = null;

		final Method[] declaredMethods = classe.getMethods();

		for (Method method : declaredMethods) {

			if (PREFIXO_METODO_SET.equals(tipoMetodo) && isSetter(method)) {

				if (method.getName().replaceAll("set", "").toLowerCase()
						.equals(nomePropriedade.toLowerCase())) {
					retorno = method;
					break;
				}
			}

			if (PREFIXO_METODO_GET.equals(tipoMetodo) && isGetter(method)) {
				boolean ehMetodoGetPropriedade = (method.getName()
						.replaceAll("get", "").toLowerCase()
						.equals(nomePropriedade.toLowerCase()))
						|| (method.getName().replaceAll("is", "").toLowerCase()
								.equals(nomePropriedade.toLowerCase()));
				if (ehMetodoGetPropriedade) {
					retorno = method;
					break;
				}
			}
		}

		return retorno;
	}

	/**
	 * Lista o nome dos métodos getters ou setters do objeto informado.
	 * 
	 * @param objeto
	 *            Objeto que deverá ter seus métodos listados.
	 * @param tipoMetodo
	 *            Constantes ReflectionUtil.PREFIXO_METODO_SET ou
	 *            ReflectionUtil.PREFIXO_METODO_GET.
	 * @return Lista dos métodos do objeto.
	 */
	public static List listaNomeDosMetodosDoObjetoVO(Object objeto,
			String tipoMetodo) {
		List nomes = new ArrayList();
		if (objeto != null) {
			Class<?> classe = objeto.getClass();
			Method[] declaredMethods = classe.getMethods();
			for (Method method : declaredMethods) {
				if (PREFIXO_METODO_SET.equals(tipoMetodo) && isSetter(method)
						&& nomes.indexOf(method.getName()) < 0) {
					nomes.add(method.getName());
				} else if (PREFIXO_METODO_GET.equals(tipoMetodo)
						&& isGetter(method)
						&& nomes.indexOf(method.getName()) < 0) {
					nomes.add(method.getName());
				}
			}
		}
		return nomes;
	}

	/**
	 * Pega o valor do método get informado para o objeto informado.
	 * 
	 * @param Objeto
	 *            Objeto que terá o método get.
	 * @param NomeMetodo
	 *            nome do método a ser executado.
	 * @return Valor da execução da função.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object pegaValorDoMetodoGet(Object objeto, String nomeMetodo)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Object valor = null;
		if (objeto != null && !Util.isNullOrEmpty(nomeMetodo)) {
			Class<?> classe = objeto.getClass();
			Method[] declaredMethods = classe.getMethods();
			for (Method method : declaredMethods) {
				if (method.getName().toLowerCase()
						.equals(nomeMetodo.toLowerCase())
						&& isGetter(method)) {
					valor = method.invoke(objeto);
					break;
				}
			}
		}
		return valor;
	}

	/**
	 * Pega o tipo que o método get informado retorna do objeto informado.
	 * 
	 * @param Objeto
	 *            Objeto que terá o método get.
	 * @param NomeMetodo
	 *            nome do método a ser executado.
	 * @return Valor da execução da função.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Class pegaTipoDoMetodoGet(Object objeto, String nomeMetodo)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Class tipo = null;
		if (objeto != null && !Util.isNullOrEmpty(nomeMetodo)) {
			Class<?> classe = objeto.getClass();
			Method[] declaredMethods = classe.getMethods();
			for (Method method : declaredMethods) {
				if (method.getName().toLowerCase()
						.equals(nomeMetodo.toLowerCase())
						&& isGetter(method)) {
					tipo = method.getReturnType();
					break;
				}
			}
		}
		return tipo;
	}

	/**
	 * Seta um valor no objeto tendo como referencia o nome da propriedade do
	 * objeto.
	 * 
	 * @param objeto
	 *            Objeto que deverá ter seu atributo setado.
	 * @param nomePropriedade
	 *            Nome da propriedade que deverá ser setada.
	 * @param valor
	 *            Valor que deverá ser inserido.
	 * @param tipo
	 *            Tipo do valor que será inserido.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void setValorMetodoSetDaPropriedade(Object objeto,
			String nomePropriedade, Object valor, Class tipo)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (objeto != null && !Util.isNullOrEmpty(nomePropriedade)) {
			Class<?> classe = objeto.getClass();
			Method metodo = getMetodoNaClasse(classe, nomePropriedade,
					PREFIXO_METODO_SET);
			if (metodo != null) {
				metodo = classe.getMethod(metodo.getName(), tipo);
				if (metodo != null) {
					metodo.invoke(objeto, valor);
				}
			}
		}
	}

	/**
	 * Seta um valor no objeto tendo como referencia o nome do método do objeto.
	 * 
	 * @param objeto
	 * @param nomeMetodo
	 * @param valor
	 * @param tipo
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void setValorMetodoSet(Object objeto, String nomeMetodo,
			Object valor, Class tipo) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (objeto != null && !Util.isNullOrEmpty(nomeMetodo)) {
			Class<?> classe = objeto.getClass();
			Method metodo = classe.getMethod(nomeMetodo, tipo);
			if (metodo != null) {
				metodo.invoke(objeto, valor);
			}
		}
	}

	/**
	 * Verifica se um método ({@link Method}) é do tipo
	 * <code><b>getter</b></code>.
	 * 
	 * @param metodo
	 *            método a ser verificado se é getter
	 * 
	 * @return Retorna <code>true</code> se o método for do tipo Getter, caso
	 *         contrário, retorna <code>false</code>.
	 */
	public static boolean isGetter(final Method metodo) {

		if (!metodo.getName().startsWith(PREFIXO_METODO_GET)
				&& !metodo.getName().startsWith(PREFIXO_METODO_GET_BOLEANO)) {
			return false;
		}
		
		if (metodo.getParameterTypes().length != 0) {
			return false;
		}

		if (void.class.equals(metodo.getReturnType())) {
			return false;
		}

		return true;
	}

	/**
	 * Verifica se um método ({@link Method}) é do tipo
	 * <code><b>setter</b></code>.
	 * 
	 * 
	 * @param method
	 *            método a ser verificado se é setter
	 * 
	 * @return Retorna <code>true</code> se o método for do tipo Setter, caso
	 *         contrário, retorna <code>false</code>.
	 */
	public static boolean isSetter(Method method) {

		if (!method.getName().startsWith(PREFIXO_METODO_SET)) {
			return false;
		}

		if (method.getParameterTypes().length != 1) {
			return false;
		}

		return true;
	}

	/**
	 * Faz a chamada ao método <code><b>get</b></code> e retorna o valor.
	 * 
	 * @param classe
	 *            Classe ({@link Class}) com o campo ({@link Field}) a ser
	 *            retornado
	 * 
	 * @param nomePropriedade
	 *            Nome da propriedade ({@link Field}) a ser pesquisado o método
	 *            <code><b>get</b></code>. Caso não ache o método na classe,
	 *            retorna <code>null</code>.
	 *            <p>
	 *            <b>Exemplo</b>:
	 *            <code><p> se o nomePropriedade for "pergunta" então o código vai buscar 
	 *             e retornar o método get, no caso, getPergunta.
	 * 
	 * @return Retorna um objeto ({@link Object}) com o valor do método
	 *         invocado.
	 * 
	 * @throws IllegalArgumentException
	 *             Possível exceção a ser levantada
	 * 
	 * @throws IllegalAccessException
	 *             Possível exceção a ser levantada
	 * 
	 * @throws InvocationTargetException
	 *             Possível exceção a ser levantada
	 */
	public static Object getValorMetodoGet(final Object objeto,
			final String nomePropriedade) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		Method metodoRetorno = getMetodoNaClasse(objeto.getClass(),
				nomePropriedade, PREFIXO_METODO_GET);
		return ((metodoRetorno == null ? metodoRetorno : metodoRetorno
				.invoke(objeto)));
	}

	/**
	 * Retorna o nome do método <code><b>get</b></code> de uma propriedade.
	 * 
	 * 
	 * @param nomePropriedade
	 *            Nome da propriedade ({@link Field}) para que seja retornado o
	 *            método get.
	 *            <p>
	 *            <b>Exemplo</b>:
	 *            <code><p> se o nomePropriedade for "pergunta" então o código vai retornar
	 *             o nome do método get, no caso, getPergunta.
	 * 
	 * @return Retorna uma {@link String} com o nome do método.
	 */
	public static String getNomeMetodoGet(final String nomePropriedade) {

		return PREFIXO_METODO_GET + WordUtils.capitalize(nomePropriedade);
	}

	/**
	 * Retorna o valor do campo {@link Field} da classe representada pelo Objeto
	 * ({@link Object})
	 * 
	 * 
	 * @param objeto
	 *            Objeto ({@link Object}) representando a classe ({@link Class})
	 * 
	 * @param nomeCampo
	 *            Nome do campo a ser pesquisado ({@link Field})
	 * 
	 * @return Retorna um objeto com o valor do campo.
	 * 
	 * @throws IllegalArgumentException
	 *             Possivel exceção a ser levantada
	 * 
	 * @throws IllegalAccessException
	 *             Possivel exceção a ser levantada
	 */
	public static Object getValorCampoClasse(final Object objeto,
			final String nomeCampo) throws IllegalArgumentException,
			IllegalAccessException {
		return getCampoDaClasse(objeto.getClass(), nomeCampo).get(objeto);
	}

	/**
	 * 
	 * Constrói o nome do método get, de acordo com o nome do atributo
	 * 
	 * @param fieldName
	 * @return método get do atributo
	 */
	public static String buildGetMethodName(String fieldName) {
		StringBuilder methodName = new StringBuilder("get");
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		methodName.append(fieldName.substring(1, fieldName.length()));

		return methodName.toString();
	}

	/**
	 * 
	 * Constrói o nome do método set, de acordo com o nome do atributo
	 * 
	 * @param fieldName
	 * @return método set do atributo
	 */
	public static String buildSetMethodName(String fieldName) {
		StringBuilder methodName = new StringBuilder("set");
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		methodName.append(fieldName.substring(1, fieldName.length()));

		return methodName.toString();
	}

	/**
	 * Realiza a chamada de qualquer método de qualquer objeto.<br>
	 * <b>Utilizado para chamadas de métodos que não retornam nada(void).</b>
	 * 
	 * @param objeto
	 *            Objeto que deverá ter sua função executada.
	 * @param nomeMetodo
	 *            Nome da função que deverá ser executada no objeto informado.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void executarMetodo(Object objeto, String nomeMetodo)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (objeto != null && !Util.isNullOrEmpty(nomeMetodo)) {
			Class<?> classe = objeto.getClass();
			Method metodo = classe.getMethod(nomeMetodo);
			if (metodo != null) {
				metodo.invoke(objeto);
			}
		}
	}

	/**
	 * Realiza a chamada de qualquer método de qualquer objeto.<br>
	 * <b>Utilizado para chamadas de métodos que retornam alguma coisa.</b>
	 * 
	 * @param objeto
	 *            Objeto que deverá ter sua função executada.
	 * @param nomeMetodo
	 *            Nome da função que deverá ser executada no objeto informado.
	 * @return Retorno da função executada.
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object executarFuncao(Object objeto, String nomeMetodo)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Object retorno = null;

		if (objeto != null && !Util.isNullOrEmpty(nomeMetodo)) {
			Class<?> classe = objeto.getClass();
			Method metodo = classe.getMethod(nomeMetodo);
			if (metodo != null) {
				retorno = metodo.invoke(objeto);
			}
		}

		return retorno;
	}

	/**
	 * Lista o nome de todos os atributos do objeto informado.
	 * 
	 * @param objeto
	 *            Objeto que deverá ter seus atributos listados.
	 * @return Lista dos nomes dos atributos do objeto.
	 */
	public static List<String> listaNomeDosAtributosDoObjetoVO(Object objeto) {

		List<String> nomes = new ArrayList<String>();

		if (objeto != null) {

			List<String> nomeMetodos = listaNomeDosMetodosDoObjetoVO(objeto,
					PREFIXO_METODO_GET);

			for (String metodo : nomeMetodos) {
				if (!metodo.equals("getClass")) {
					nomes.add(metodo.replaceAll("get", "").toLowerCase());
				}
			}
		}
		Collections.sort(nomes);
		return nomes;
	}

	/**
	 * Pega o nome das propriedades de uma classe cuja as quais representam uma
	 * Primary Key({@link PKTableMaster}) numa tabela.
	 * 
	 * @param classe
	 * @return Lista contendo o nome das colunas que representam uma PrimaryKey
	 */
	public static List<String> pegaColunasPK(Class classe) {

		List<String> colunasPK = new ArrayList<String>();

		for (Field field : classe.getDeclaredFields()) {

			Annotation[] annotation = field.getDeclaredAnnotations();

			if (annotation.length > 0) {
				for (Annotation annotation2 : annotation) {
					if (annotation2 instanceof PKTableMaster) {
						colunasPK.add(field.getName());
					}
				}
			}
		}

		return colunasPK;
	}

	/**
	 * Verifica se o objeto é um agrupamento de {@link TableMaster}.
	 * @param object Objeto que deverá ser verificado.
	 * @return True caso o objeto informado como parâmetro represente um agrupamento de {@link TableMaster}. False caso não represente.
	 */
	public static boolean isGroupTableMaster(Object object) {
		boolean isGroupTableMaster = false;

		if (object != null) {

			for (Field field : object.getClass().getDeclaredFields()) {

				isGroupTableMaster = field.getType().getSuperclass() == TableMaster.class;
				if (isGroupTableMaster)
					break;
			}
		}
		return isGroupTableMaster;
	}

}
