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

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.dao.ProcessoDAO;
import database.dao.TabelaProcessarDAO;
import database.model.Conexoes;
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
					setConnections();
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
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private void setConnections() throws SQLException {
		ConexoesDAO cDAO = new ConexoesDAO(connectionControle);

		Conexoes STR_CONN_ORIGEM = cDAO.SelectAllById(2);

		connectionOrigem = ConnectionFactory.getConnection(STR_CONN_ORIGEM.getEndereco_ip(),
				STR_CONN_ORIGEM.getEndereco_porta(), STR_CONN_ORIGEM.getNome_banco(), STR_CONN_ORIGEM.getUsuario(),
				STR_CONN_ORIGEM.getSenha(), STR_CONN_ORIGEM.getTipo_banco());

		setLabelText("Conectando ao DB: ORIGEM!!");

		Conexoes STR_CONN_DESTINO = cDAO.SelectAllById(1);

		connectionDestino = ConnectionFactory.getConnection(STR_CONN_DESTINO.getEndereco_ip(),
				STR_CONN_DESTINO.getEndereco_porta(), STR_CONN_DESTINO.getNome_banco(), STR_CONN_DESTINO.getUsuario(),
				STR_CONN_DESTINO.getSenha(), STR_CONN_DESTINO.getTipo_banco());

		setLabelText("Conectando ao DB: DESTINO!!");
	}

	private void processoVerificar() throws SQLException {

		ProcessoDAO dao = new ProcessoDAO(connectionControle);
		ArrayList<Processo> resultado = dao.selectAll();

		for (Processo p : resultado) {
			System.out.println("Processo: " + p.getNome_processo());
			setLabelText("Processo: " + p.getNome_processo());
			tabelasProcessar(p.getId());
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

		ResultSet resultOrigin = statementOrigin
				.executeQuery("SELECT * FROM " + table.getNome_tb_destino() + " ORDER BY id ASC;");
		ResultSet resultDest = statementDest
				.executeQuery("SELECT * FROM " + table.getNome_tb_destino() + " ORDER BY id ASC;");

		ResultSetMetaData metaDataOrigin = resultOrigin.getMetaData();
		ResultSetMetaData metaDataDest = resultDest.getMetaData();

		int columnCountOrigin = metaDataOrigin.getColumnCount();
		int columnCountDest = metaDataDest.getColumnCount();

		if (columnCountDest == columnCountOrigin) {

			while (resultOrigin.next() && resultDest.next()) {
				StringBuilder updateQuery = new StringBuilder("UPDATE " + table.getNome_tb_destino() + " SET ");

				boolean updateFlag = false;
				for (int i = 1; i <= columnCountOrigin; i++) {
					String columnNameOrigin = metaDataOrigin.getColumnName(i);
					String valueOrigin = resultOrigin.getString(i);

					String columnNameDest = metaDataDest.getColumnName(i);
					String valueDest = resultDest.getString(i);

					if (columnNameOrigin.equals(columnNameDest)) {
						if (!valueOrigin.equals(valueDest)) {
							updateFlag = true;
							updateQuery.append(columnNameDest).append(" = '").append(valueOrigin).append("', ");
						}
					}
				}
				updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
				updateQuery.append(" WHERE id = ").append(resultOrigin.getString("id")).append(";");

				if (updateFlag) {
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

	private void InsertIntoTables(TabelaProcessar table, TabelaProcessarDAO dao) throws SQLException {
		Statement sourceStatement = connectionOrigem.createStatement();
		ResultSet resultSet = sourceStatement
				.executeQuery("SELECT * FROM " + table.getNome_tb_destino() + " WHERE id > " + table.getCondicao());
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		Statement destinationStatement = connectionDestino.createStatement();
		while (resultSet.next()) {
			StringBuilder insertQuery = new StringBuilder("INSERT INTO " + table.getNome_tb_destino() + " VALUES (");

			for (int i = 1; i <= columnCount; i++) {
				String value = resultSet.getString(i);
				insertQuery.append("'").append(value).append("', ");
			}

			insertQuery.delete(insertQuery.length() - 2, insertQuery.length());
			insertQuery.append(");");
			System.out.println(" " + insertQuery.toString());
			destinationStatement.executeUpdate(insertQuery.toString());
		}
		resultSet = destinationStatement.executeQuery("SELECT MAX(id) as max FROM " + table.getNome_tb_destino());
		if (resultSet.next()) {
			dao.updateCondicao(resultSet.getInt("max"), table.getNome_tb_destino(), table.getId_processo());
		}
	}
}