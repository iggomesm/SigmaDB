package br.com.sigmadb.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.sigmadb.enumerations.EnumVersionador;
import br.com.sigmadb.utilitarios.Constantes;
/**
 * Classe responsável por demandar qualquer sintaxe sql ao banco.
 * @author igorjua
 * @since 04/07/2015
 */
public class DataBase {

	//private final String PROPERTIES_FILE = "main" + File.separator + "resources" + File.separator + "configuracoes.properties";
	private final String PROPERTIES_FILE = Constantes.CONFIG + ".xml";
	private String url;
    private String driver;
    private String usuario;
    private String senha;
    private static String useIlog;
    private static String printSql;
    private static DataBase instance = null;
    public static final int ACTION_ALTEROU_VALORES = 1;
    public static final int ACTION_NAO_ALTEROU_VALORES = 0;
    public static final int TODOS_REGISTROS_ALTERADOS = 1;
    public static final int NEM_TODOS_REGISTROS_ALTERADOS = 0;
    public static final int NENHUM_REGISTRO_ALTERADO = 2;
    public static final int TIPOINSERT = 0;
    public static final int TIPOUPDATE = 1;
    

    /**
     * Executa qualquer consulta sql.
     * @param sql Consulta sql que será executada.
     * @return ResultSet obtido pela consulta.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static ResultSet executeQuery(String sql, Connection connection) throws ClassNotFoundException, SQLException {
        ResultSet resultado = null;
        try {
            Statement stmt = null;
            stmt = connection.createStatement();
            resultado = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            throw new SQLException(ex.getMessage() + "\n" + sql);
        }finally {
        	if(Boolean.parseBoolean(printSql)) {
        		System.out.println(sql);
        	}
		}
        return resultado;
    }

    /**
     * Persiste qualquer sintaxe sql no banco.
     * @param sql Sintaxe sql de insert, update ou delete.
     * @return Id do elemento que foi persistido.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static int applyUpdates(String sql, Connection connection) throws ClassNotFoundException, SQLException {
        int idRetorno = 0;
        try {
            
            connection.setAutoCommit(false);
            
            PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();

            while(rs.next()){
            	idRetorno = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            throw new SQLException(ex.getMessage() + "\n" + sql);
        } finally {
        	if(Boolean.parseBoolean(printSql)) {
        		System.out.println(sql);
        	}
		}
        return idRetorno;
    }

    /**
     * Realiza o commit da conexão aberta.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void grava(Connection connection) throws ClassNotFoundException, SQLException {
        try {
            connection.setAutoCommit(false);
            connection.commit();
        } finally {
            connection.close();
        }
    }
    
    /**
     * Realiza o rollBack da conexão aberta.
     * @throws SQLException
     */
    public static void rollBack(Connection connection) throws SQLException{
    	try {
            connection.setAutoCommit(false);
            connection.rollback();
        } finally {
            connection.close();
        }
    }

    public DataBase() {
		readProperties();
    }

    /**
     * Metodo para obter a configuracao do banco pelo arquivo configuracoes.properties.
     */
	private void readProperties() {
		Properties properties = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
		
		try {
			if (inputStream != null) {
				properties.loadFromXML(inputStream);
			} else {
				throw new FileNotFoundException("Arquivo '" + PROPERTIES_FILE + "' não encontrado no classpath.");
			}
			url = properties.getProperty("url");
			usuario = properties.getProperty("userName");
			senha = properties.getProperty("password");
			driver = properties.getProperty("driver");
			printSql = properties.getProperty("printSqlConsole");
			useIlog  = properties.getProperty("useIlog");
		}catch (IOException e) {
			System.out.println("Exception: " + e);
		}finally {
			try {
				inputStream.close();
			} catch (IOException e) {
			
			}
		}
		
	}

    public static DataBase getSingleton() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    /**
     * Cria uma conexão com o banco.
     * @return Objeto de conexão aberta com o banco.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection c = null;
        try {
            Class.forName(driver);
            c = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return c;
    }

    public static synchronized int pegaVersaoConexao() throws Exception{
		
		DataBase dataBase = DataBase.getSingleton();
		
		int versao = 0;
		
		Connection con = null;
		try {
			
			con = dataBase.getConnection();
			
			String sql = "select seqr_valor from sequenciador where seqr_descricao = '" + EnumVersionador.VERSAO_ILOG.getValor() + "'";
			
			ResultSet rs = dataBase.executeQuery(sql, con);
			
			while(rs.next()){
				versao = rs.getInt("seqr_valor");
			}
			
			sql = "update sequenciador set seqr_valor = (seqr_valor + 1) where seqr_descricao = '" + EnumVersionador.VERSAO_ILOG.getValor() + "'";
			
			PreparedStatement pstmt = con.prepareStatement(sql);

			pstmt.executeUpdate();	           
			
			grava(con);
			
		} catch (Exception e) {
			e.printStackTrace();
			rollBack(con);
		} finally {
			if (con != null){
				con.close();
			}
		}
		
		return versao;
	}

	public static String getUseIlog() {
		return useIlog;
	}
}
