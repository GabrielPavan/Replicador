package graphic;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import database.ConnectionFactory;
import database.dao.TabelaProcessarDAO;
import database.model.TabelaProcessar;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class SetProcessTable extends JDialog {
	private JTable table;

	public SetProcessTable() {
		setResizable(false);
		setTitle("Selecione as tabelas a processar");
		setBounds(100, 100, 775, 325);
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 739, 200);
		getContentPane().add(scrollPane);

		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Processo", "Tabela origem", "Tabela destino", "Ordem", "Condicao", "Habilitado"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, String.class, String.class, String.class, Integer.class, Integer.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(15);
		table.getColumnModel().getColumn(0).setMinWidth(5);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setMinWidth(75);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(130);
		table.getColumnModel().getColumn(2).setMinWidth(130);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(3).setPreferredWidth(130);
		table.getColumnModel().getColumn(3).setMinWidth(130);
		table.getColumnModel().getColumn(4).setResizable(false);
		table.getColumnModel().getColumn(4).setPreferredWidth(15);
		table.getColumnModel().getColumn(5).setResizable(false);
		table.getColumnModel().getColumn(5).setPreferredWidth(15);
		table.getColumnModel().getColumn(6).setResizable(false);
		table.getColumnModel().getColumn(6).setPreferredWidth(15);
		scrollPane.setViewportView(table);

		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnSalvar.setBounds(649, 250, 100, 25);
		getContentPane().add(btnSalvar);

		JButton btnAdicionar = new JButton("Adicionar");
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnAdicionar.setBounds(10, 250, 100, 25);
		getContentPane().add(btnAdicionar);

		preencherTabela();
		setVisible(true);
	}

	private void preencherTabela() {
		try {
			Connection conn = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);

			TabelaProcessarDAO dao = new TabelaProcessarDAO(conn);
			ArrayList<TabelaProcessar> tabelaProcessar = dao.selectAll();

			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
			
			for (TabelaProcessar tp : tabelaProcessar) {
				model.addRow(new Object[] {
						tp.getId(),
						tp.getDescricaoProcesso(),
						tp.getNome_tabela_origem(),
						tp.getNome_tabela_dest(),
						tp.getOrdem(),
						tp.getCondição(),
						tp.isHabilitado()
					}); 
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}