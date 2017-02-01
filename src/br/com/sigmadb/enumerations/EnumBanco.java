package br.com.sigmadb.enumerations;

import java.util.ArrayList;
import java.util.List;

import br.com.sigmadb.connection.SigmaDB;

public enum EnumBanco {

	POSTGRESQL("select * from pg_tables where schemaname = 'public'");
	
	private static List<String> tabelas = new ArrayList<String>();
	
	private EnumBanco(String consulta) {
		preparaTabelasPostgres(consulta);
	}
	
	private static void preparaTabelasPostgres(String consulta) {
		try {
			
			Pg_tables tables = new Pg_tables();
			tables.setSchemaname("public");
			
			SigmaDB sigma = new SigmaDB();
			
			List<Pg_tables> resultadoConsulta = sigma.pesquisaTabela(tables, null);
			
			for (Pg_tables pg_tables : resultadoConsulta) {
				tabelas.add(pg_tables.getTablename().trim().toLowerCase());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean verificaTabela(String nomeTabela) {
		return tabelas.contains(nomeTabela.trim().trim().toLowerCase());
	}
	
	public static class Pg_tables {
		private String schemaname;
		private String tablename;
		private String tableowner;
		private String tablespace;
		private String hasindexes;
		private String hasrules;
		private String hastriggers;
		public String getSchemaname() {
			return schemaname;
		}
		public void setSchemaname(String schemaname) {
			this.schemaname = schemaname;
		}
		public String getTablename() {
			return tablename;
		}
		public void setTablename(String tablename) {
			this.tablename = tablename;
		}
		public String getTableowner() {
			return tableowner;
		}
		public void setTableowner(String tableowner) {
			this.tableowner = tableowner;
		}
		public String getTablespace() {
			return tablespace;
		}
		public void setTablespace(String tablespace) {
			this.tablespace = tablespace;
		}
		public String getHasindexes() {
			return hasindexes;
		}
		public void setHasindexes(String hasindexes) {
			this.hasindexes = hasindexes;
		}
		public String getHasrules() {
			return hasrules;
		}
		public void setHasrules(String hasrules) {
			this.hasrules = hasrules;
		}
		public String getHastriggers() {
			return hastriggers;
		}
		public void setHastriggers(String hastriggers) {
			this.hastriggers = hastriggers;
		}
		
	}
}
