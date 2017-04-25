CREATE TABLE ilog
(
   ilog_id serial, 
   ilog_versao integer, 
   ilog_tipo character varying(1), 
   ilog_tabela character varying(20), 
   ilog_data_hora timestamp without time zone, 
   ilog_usuario character varying(20), 
   ilog_origem character varying, 
   ilog_pk_tabela serial, 
   ilog_valores character varying, 
   CONSTRAINT "Pk_ilog" PRIMARY KEY (ilog_id)
) 

CREATE TABLE sequenciador
(
   seqr_descricao character varying(30), 
   seqr_valor integer, 
   CONSTRAINT "Pk_sequenciador" PRIMARY KEY (seqr_descricao)
) 

INSERT INTO SEQUENCIADOR VALUES ('VERSAO_ILOG', 1)
