package br.com.sigmadb.enumerations;

/**
 * Responsável por manter o nome dos tipos de atributos que podem ser passados da camda de visão
 * para a camada de aplicação do sistema.
 * @author igorgomesdemoises
 * @since 14/07/2015
 */
public enum EnumViewProperties {

	/**
	 * Id do usuário informado pela camada de visão.
	 */
	USUARIO, 
	/**
	 * OperacaoDAO Enum para dizer se será uma atualização ou inserçao na tabela.
	 */
	CONNECTION_LOG,

	/**
	 * Nome da JSP ou controle que origiou a requisição.
	 */
	ORIGEM_REQUISICAO;	
}
