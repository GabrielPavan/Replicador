package execution;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.cj.jdbc.DatabaseMetaData;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.dao.DirecaoDAO;
import database.dao.ProcessoDAO;
import database.dao.TabelaProcessarDAO;
import database.model.Conexoes;
import database.model.Direcao;
import database.model.Processo;
import database.model.TabelaProcessar;

public class ReplicacaoExecutar {

	private Connection connectionOrigem;
	private Connection connectionDestino;
	private Connection connectionControle;

	private String connStringOrigem;
	private String connStringDest;

	public Thread minhaTheadr;
	public boolean exec = false;
	
	public ReplicacaoExecutar(long sleepReplication, String connOrigem, String connDest) throws SQLException {
		connStringOrigem = connOrigem;
		connStringDest = connDest;
		minhaTheadr = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (exec) {
						connectionControle = ConnectionFactory.getConnection(
								"jdbc:postgresql://localhost:5432/Controle", "postgres", "1KUkd2HXpelZ7TkV6zU2",
								ConnectionFactory.TIPO_BANCO_POSTGRES);
						if (connectionControle != null) {
							processoVerificar();
						} else {
							System.out.println("Não foi possível conectar no banco de controle!");
						}
						Thread.sleep(sleepReplication);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		minhaTheadr.start();
	}

	private void processoVerificar() throws SQLException {

		ProcessoDAO dao = new ProcessoDAO(connectionControle);
		ArrayList<Processo> resultado = dao.selectAll();
		
		for (Processo p : resultado) {
			System.out.println("Processo: " + p.getNome_processo() + " Status: " + p.isHabilitado());
			direcaoVerificar(p.getId());
		}
	}

	private void direcaoVerificar(int idProcesso) throws SQLException {

		DirecaoDAO dao = new DirecaoDAO(connectionControle);
		ArrayList<Direcao> resultado = dao.SelectAll(idProcesso);

		for (Direcao d : resultado) {
			conexoesVerificar(d.getId_conexao_origem(), d.getId_conexao_destino(), idProcesso);
		}
	}

	private void conexoesVerificar(int id_conexao_origem, int id_conexao_destino, int id_processo) throws SQLException {

		ConexoesDAO dao = new ConexoesDAO(connectionControle);
		Conexoes conexoes = dao.SelectAll(id_conexao_origem);

		// Faz a conexão no banco de controle ...
		connectionOrigem = ConnectionFactory.getConnection(connStringOrigem, conexoes.getUsuario(), conexoes.getSenha(),
				conexoes.getTipo_banco());
		if (connectionOrigem != null) {

			conexoes = dao.SelectAll(id_conexao_destino);
			connectionDestino = ConnectionFactory.getConnection(connStringDest, conexoes.getUsuario(),
					conexoes.getSenha(), conexoes.getTipo_banco());
			if (connectionDestino != null) {
				tabelasProcessar(id_processo);
			} else {
				System.out.println("Não foi possível conectar no banco destino: " + conexoes.getEndereco_ip() + ":"
						+ conexoes.getEndereco_porta());
			}
		} else {
			System.out.println("Não foi possível conectar no banco origem: " + conexoes.getEndereco_ip() + ":"
					+ conexoes.getEndereco_porta());
		}
	}

	private void tabelasProcessar(int id_processo) throws SQLException {
		TabelaProcessarDAO dao = new TabelaProcessarDAO(connectionControle);
		ArrayList<TabelaProcessar> resultado = dao.selectAll(id_processo);

		for (TabelaProcessar tp : resultado) {
			switch (id_processo) {
			case 1:
				if (!CheckTableExists(tp)) {
					System.out
							.println("  A tabela " + tp.getNome_tabela_origem() + " não existe banco de dados destino.");
					syncTables(tp);
				} else {
					System.out.println("  A tabela " + tp.getNome_tabela_origem() + " já existe banco de dados destino.");
				}
				break;
			case 2:
				Statement sourceStatement = connectionOrigem.createStatement();
				ResultSet resultSet = sourceStatement.executeQuery("SELECT * FROM " + tp.getNome_tabela_origem() +" WHERE id > " + tp.getCondição());
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				Statement destinationStatement = connectionDestino.createStatement();
				while (resultSet.next()) {
					StringBuilder insertQuery = new StringBuilder(
							"INSERT INTO " + tp.getNome_tabela_dest() + " VALUES (");

					for (int i = 1; i <= columnCount; i++) {
						String value = resultSet.getString(i);
						insertQuery.append("'").append(value).append("', ");
					}

					insertQuery.delete(insertQuery.length() - 2, insertQuery.length());
					insertQuery.append(");");
					System.out.println(insertQuery.toString());
					destinationStatement.executeUpdate(insertQuery.toString());	
				}
				resultSet = destinationStatement.executeQuery("SELECT MAX(id) as max FROM " +  tp.getNome_tabela_dest());
				if(resultSet.next()) {
					dao.updateCondicao(tp.getId(), resultSet.getInt("max"));	
				}
			}
		}
	}
	private boolean CheckTableExists(TabelaProcessar table) throws SQLException {
		DatabaseMetaData metaData = (DatabaseMetaData) connectionDestino.getMetaData();
		ResultSet tables = metaData.getTables(null, null, table.getNome_tabela_origem(), null);
		return tables.next();
	}

	private void syncTables(TabelaProcessar table) throws SQLException {
		Statement statementOrigin = connectionOrigem.createStatement();
		Statement statementDest = connectionDestino.createStatement();
		ResultSet result = statementOrigin.executeQuery("SELECT * FROM " + table.getNome_tabela_origem());

		ResultSetMetaData metaData = result.getMetaData();

		StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + table.getNome_tabela_origem() + "(");
		StringBuilder createTableConstraint = new StringBuilder();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metaData.getColumnName(i);
			String columnType = metaData.getColumnTypeName(i);
			int columnSize = metaData.getColumnDisplaySize(i);

			if (columnType.contains("bigserial")) {
				columnType = "bigint";
			}
			if (columnName.equalsIgnoreCase("id")) {
				createTableConstraint = new StringBuilder("ALTER TABLE `" + table.getNome_tabela_origem() + "` ");
				createTableConstraint.append("CHANGE COLUMN `" + columnName + "` `" + columnName + "` " + columnType
						+ " NOT NULL AUTO_INCREMENT, ");
				createTableConstraint.append("ADD PRIMARY KEY (`" + columnName + "`);");
			}
			if (columnType.equalsIgnoreCase("date")) {
				createTableQuery.append(columnName).append(" ").append(columnType).append(", ");
			} else if (columnType.equalsIgnoreCase("numeric")) {
				createTableQuery.append(columnName).append(" ").append("decimal").append("(").append("13")
						.append(", 2), ");
			} else {
				createTableQuery.append(columnName).append(" ").append(columnType).append("(").append(columnSize)
						.append("), ");
			}

		}
		createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length()); // Remover a última vírgula e
																							// espaço
		createTableQuery.append(");");

		statementDest.execute(createTableQuery.toString());
		statementDest.execute(createTableConstraint.toString());

		System.out.println("	A tabela " + table.getNome_tabela_dest() + " criada com sucesso!");
	}
}