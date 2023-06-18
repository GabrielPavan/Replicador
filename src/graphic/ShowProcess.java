package graphic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import database.ConnectionFactory;
import database.dao.ProcessoDAO;
import database.model.Processo;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class ShowProcess extends JDialog {
	private JTable table;
	private String processo = "";
	private Integer processoId;
	
	public String getProcesso() {
		return processo;
	}
	public Integer getProcessoId() {
		return processoId;
	}
	
	public ShowProcess(JDialog parent) {
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(parent);
		setTitle("Processos existentes");
		setBounds(100, 100, 562, 225);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 525, 139);
		getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Processo", "Descri\u00E7\u00E3o", "Vers\u00E3o"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(15);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(60);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(215);
		table.getColumnModel().getColumn(2).setMinWidth(80);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(3).setPreferredWidth(25);
		scrollPane.setViewportView(table);
		
		JLabel lblInfo = new JLabel("Novos processos serão adicionados nas proximas verções\r\n");
		lblInfo.setEnabled(false);
		lblInfo.setBounds(10, 161, 414, 14);
		getContentPane().add(lblInfo);
		
		JButton btnNewButton = new JButton("Fechar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int linha = table.getSelectedRow();
					processoId = (Integer) table.getValueAt(linha, 0);
					processo = table.getValueAt(linha, 1).toString();
				} catch (Exception e2) {
					
				} finally {
					dispose();
				}
			}
		});
		btnNewButton.setBounds(444, 157, 89, 23);
		getContentPane().add(btnNewButton);
		preencherTabela();
		setVisible(true);
	}
	private void preencherTabela() {
		Connection conn;
		try {
			conn = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
			
			ProcessoDAO pDAO = new ProcessoDAO(conn);
			ArrayList<Processo> processos = pDAO.selectAll();
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
			
			for (Processo processo : processos) {
				model.addRow(new Object[] {
						processo.getId(),
						processo.getNome_processo(),
						processo.getDescricao(),
						"1.0"// add colon;
				}); 
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}