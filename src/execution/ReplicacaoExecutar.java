package execution;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

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
import graphic.MainFrame;

public class ReplicacaoExecutar extends Thread {

	private Connection connectionOrigem;
	private Connection connectionDestino;
	private Connection connectionControle;

	private MainFrame frame;

	public ReplicacaoExecutar(MainFrame frame) {
		this.frame = frame;
	}

	public void setLabelText(String text) {
		SwingUtilities.invokeLater(() -> {
			JLabel test = frame.getLblNewLabel();
			test.setText(text);
		});
	}

	@Override
	public void run() {

		try {
			while (!Thread.currentThread().isInterrupted()) {
				connectionControle = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
						"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);

				if (connectionControle != null) {
					setLabelText("Conectando ao DB: CONTROLE!!");
					processoVerificar();
				} else {
					System.out.println("Não foi possível conectar no banco de controle!");
				}
				for (int i = 30; i >= 0; i--) {
					final int progress = i;
					setLabelText("Proxima Replicação em: " + progress + " Segundos");
					Thread.sleep(1000);
				}
			}
		} catch (SQLException | InterruptedException e) {
			setLabelText("Replicação pausada!");
			Thread.currentThread().interrupt();
		}
	}

	private void processoVerificar() throws SQLException {

		ProcessoDAO dao = new ProcessoDAO(connectionControle);
		ArrayList<Processo> resultado = dao.selectAll();

		for (Processo p : resultado) {
			System.out.println("Processo: " + p.getNome_processo());
			setLabelText("Processo: " + p.getNome_processo());
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
		Conexoes conexoes = dao.SelectAllById(id_conexao_origem);

		connectionOrigem = ConnectionFactory.getConnection(conexoes.getEndereco_ip(), conexoes.getEndereco_porta(),
				conexoes.getNome_banco(), conexoes.getUsuario(), conexoes.getSenha(), conexoes.getTipo_banco());
		if (connectionOrigem != null) {

			conexoes = dao.SelectAllById(id_conexao_destino);
			connectionDestino = ConnectionFactory.getConnection(conexoes.getEndereco_ip(), conexoes.getEndereco_porta(),
					conexoes.getNome_banco(), conexoes.getUsuario(), conexoes.getSenha(), conexoes.getTipo_banco());
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
		
		SwingUtilities.invokeLater(() -> {
			JProgressBar bar = frame.getProgressBar();
			bar.setValue(0);
		});
		final int progress = 100 / resultado.size();
		for (TabelaProcessar tp : resultado) {
			switch (id_processo) {
			case 1:
				if (!CheckTableExists(tp)) {
					System.out.println(
							"  A tabela " + tp.getNome_tabela_origem() + " não existe banco de dados destino.");
					CreateTables(tp);
				} else {
					System.out.println("  A tabela " + tp.getNome_tabela_origem() + " já existe banco de dados destino.");
				}
				SwingUtilities.invokeLater(() -> {
					JProgressBar bar = frame.getProgressBar();
					bar.setValue(bar.getValue() + progress);
				});
				break;
			case 2:
				InsertIntoTables(tp, dao);
				SwingUtilities.invokeLater(() -> {
					JProgressBar bar = frame.getProgressBar();
					bar.setValue(bar.getValue() + progress);
				});
				break;
			case 3:
				UpdateTables(tp);
				break;
			}
		}
		SwingUtilities.invokeLater(() -> {
			JProgressBar bar = frame.getProgressBar();
			bar.setValue(100);
		});
	}

	private void UpdateTables(TabelaProcessar table) throws SQLException {
		Statement statementOrigin = connectionOrigem.createStatement();
		Statement statementDest = connectionDestino.createStatement();
		
		ResultSet resultOrigin = statementOrigin.executeQuery("SELECT * FROM " + table.getNome_tabela_origem() + " ORDER BY id ASC;");
		ResultSet resultDest = statementDest.executeQuery("SELECT * FROM " + table.getNome_tabela_dest() + " ORDER BY id ASC;");
		
		ResultSetMetaData metaDataOrigin = resultOrigin.getMetaData();
		ResultSetMetaData metaDataDest = resultDest.getMetaData();
		
		int columnCountOrigin = metaDataOrigin.getColumnCount();
		int columnCountDest = metaDataDest.getColumnCount();
		
		if(columnCountDest == columnCountOrigin) {
	
			while(resultOrigin.next() && resultDest.next()) {
				StringBuilder updateQuery = new StringBuilder("UPDATE " + table.getNome_tabela_dest() + " SET ");
				
				boolean updateFlag = false;
                for (int i = 1; i <= columnCountOrigin; i++) {
                    String columnNameOrigin = metaDataOrigin.getColumnName(i);
                    String valueOrigin = resultOrigin.getString(i);
                    
                    String columnNameDest = metaDataDest.getColumnName(i);
                    String valueDest = resultDest.getString(i);

                    if(columnNameOrigin.equals(columnNameDest)) {
                    	if(!valueOrigin.equals(valueDest)) {
                    		updateFlag = true;
                    		updateQuery.append(columnNameDest).append(" = '").append(valueOrigin).append("', ");
                    	}
                    }
                }
                updateQuery.delete(updateQuery.length() - 2, updateQuery.length()); 
                updateQuery.append(" WHERE id = ").append(resultOrigin.getString("id")).append(";");
                
                if(updateFlag) {
                	Statement destinationStatement = connectionDestino.createStatement();
                	destinationStatement.executeUpdate(updateQuery.toString());
                	System.out.println(" " + updateQuery.toString());
                	SwingUtilities.invokeLater(() -> {
            			JProgressBar bar = frame.getProgressBar();
            			bar.setValue(bar.getValue() + 1);
            		});
                }
			}
		}
	}

	private boolean CheckTableExists(TabelaProcessar table) throws SQLException {
		DatabaseMetaData metaData = (DatabaseMetaData) connectionDestino.getMetaData();
		ResultSet tables = metaData.getTables(null, null, table.getNome_tabela_origem(), null);
		return tables.next();
	}

	private void CreateTables(TabelaProcessar table) throws SQLException {
		Statement statementOrigin = connectionOrigem.createStatement();
		Statement statementDest = connectionDestino.createStatement();
		ResultSet result = statementOrigin.executeQuery("SELECT * FROM " + table.getNome_tabela_origem());

		ResultSetMetaData metaData = result.getMetaData();
	
		StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + table.getNome_tabela_origem() + "(");
		StringBuilder createTableConstraint = new StringBuilder("");
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metaData.getColumnName(i);
			String columnType = metaData.getColumnTypeName(i);
			int columnSize = metaData.getColumnDisplaySize(i);

			if (columnType.contains("bigserial")) {
				columnType = "bigint";
			} else if (columnName.equalsIgnoreCase("id")) {
				createTableConstraint = new StringBuilder("ALTER TABLE `" + table.getNome_tabela_origem() + "` ");
				createTableConstraint.append("CHANGE COLUMN `" + columnName + "` `" + columnName + "` " + columnType + " NOT NULL AUTO_INCREMENT, ");
				createTableConstraint.append("ADD PRIMARY KEY (`" + columnName + "`);");
			} else if (columnType.equalsIgnoreCase("date")) {
				createTableQuery.append(columnName).append(" ").append(columnType).append(", ");
			} else if (columnType.equalsIgnoreCase("numeric") || columnType.equalsIgnoreCase("float8")) {
				createTableQuery.append(columnName).append(" ").append("decimal").append("(").append("13").append(", 2), ");
			} else {
				createTableQuery.append(columnName).append(" ").append(columnType).append("(").append(columnSize).append("), ");
			}

		}
		createTableQuery.delete(createTableQuery.length() - 2, createTableQuery.length());
		createTableQuery.append(");");

		statementDest.execute(createTableQuery.toString());
		if (createTableQuery.length() > 0) {
			statementDest.execute(createTableConstraint.toString());
		}
		
		System.out.println("	A tabela " + table.getNome_tabela_dest() + " criada com sucesso!");
	}

	private void InsertIntoTables(TabelaProcessar table, TabelaProcessarDAO dao) throws SQLException {
		Statement sourceStatement = connectionOrigem.createStatement();
		ResultSet resultSet = sourceStatement
				.executeQuery("SELECT * FROM " + table.getNome_tabela_origem() + " WHERE id > " + table.getCondição());
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		Statement destinationStatement = connectionDestino.createStatement();
		while (resultSet.next()) {
			StringBuilder insertQuery = new StringBuilder("INSERT INTO " + table.getNome_tabela_dest() + " VALUES (");

			for (int i = 1; i <= columnCount; i++) {
				String value = resultSet.getString(i);
				insertQuery.append("'").append(value).append("', ");
			}

			insertQuery.delete(insertQuery.length() - 2, insertQuery.length());
			insertQuery.append(");");
			System.out.println(" " + insertQuery.toString());
			destinationStatement.executeUpdate(insertQuery.toString());
		}
		resultSet = destinationStatement.executeQuery("SELECT MAX(id) as max FROM " + table.getNome_tabela_dest());
		if (resultSet.next()) {
			dao.updateCondicao(table.getId(), resultSet.getInt("max"));
		}
	}
}