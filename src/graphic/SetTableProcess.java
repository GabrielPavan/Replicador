package graphic;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.dao.TabelaProcessarDAO;
import database.model.Conexoes;
import database.model.TabelaProcessar;

import java.awt.Font;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SetTableProcess extends JDialog {
	private JTree treeDestino;
	private DefaultMutableTreeNode rootNodeDestino;
	private Connection connControle, connDestino;

	public SetTableProcess() {
		setResizable(false);
		setTitle("Definir processos por tabela");
		setBounds(100, 100, 455, 340);
		getContentPane().setLayout(null);
		try {
			connControle = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		rootNodeDestino = new DefaultMutableTreeNode("Destino");
		treeDestino = new JTree(rootNodeDestino);
		treeDestino.setBounds(10, 41, 266, 249);
		getContentPane().add(treeDestino);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 30, 266, 2);
		getContentPane().add(separator);

		JLabel lblNewLabel = new JLabel("Destino");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 5, 266, 25);
		getContentPane().add(lblNewLabel);

		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(286, 5, 12, 285);
		getContentPane().add(separator_1);

		JButton btnNewButton = new JButton("Add. Processo");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath selectionPath = treeDestino.getSelectionPath();
				if (selectionPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
					ShowProcess setProcess = new ShowProcess(SetTableProcess.this);
					if (setProcess.getProcesso().trim().equals("")) {
						return;
					}
					try {
						TabelaProcessarDAO tDAO = new TabelaProcessarDAO(connControle);
						if(tDAO.insert(selectedNode.toString(), setProcess.getProcessoId())) {
							DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(setProcess.getProcesso());
							selectedNode.add(newNode);
							DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
							model.nodeStructureChanged(selectedNode);
							JOptionPane.showMessageDialog(null, "Processo adicionado com sucesso!");
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					} finally {
						treeDestino.updateUI();
					}
				}
			}
		});
		btnNewButton.setBounds(297, 8, 132, 23);
		getContentPane().add(btnNewButton);

		JButton btnRevProcesso = new JButton("Rev. Processo");
		btnRevProcesso.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath selectionPath = treeDestino.getSelectionPath();
	            if (selectionPath != null) {
	                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
	                if (selectedNode.isLeaf()) {
	                	DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
	                	if(parentNode != null) {
	                		try {
								TabelaProcessarDAO tDAO = new TabelaProcessarDAO(connControle);
								if(tDAO.delete(parentNode.toString(), selectedNode.toString())) {
									parentNode.remove(selectedNode);
				                    DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
				                    model.nodeStructureChanged(parentNode);
				                    JOptionPane.showMessageDialog(null, "Processo removido com sucesso!");
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							} finally {
								treeDestino.updateUI();
							}
	                	}
	                } else {
	                    JOptionPane.showMessageDialog(null, "Selecione um processo que deseja remover!");
	                }
	            }
			}
		});
		btnRevProcesso.setBounds(297, 38, 132, 25);
		getContentPane().add(btnRevProcesso);

		buscarTabelasDestino(connControle);
		setVisible(true);
	}

	private void buscarTabelasDestino(Connection conn) {
		try {
			ConexoesDAO cDAO = new ConexoesDAO(conn);
			Conexoes STR_CONN_DESTINO = cDAO.SelectAllById(1);

			connDestino = ConnectionFactory.getConnection(STR_CONN_DESTINO.getEndereco_ip(),
					STR_CONN_DESTINO.getEndereco_porta(), STR_CONN_DESTINO.getNome_banco(),
					STR_CONN_DESTINO.getUsuario(), STR_CONN_DESTINO.getSenha(), STR_CONN_DESTINO.getTipo_banco());

			DatabaseMetaData metaData = connDestino.getMetaData();
			ResultSet resultSet = metaData.getTables(STR_CONN_DESTINO.getNome_banco(), null, "%",
					new String[] { "TABLE" });
			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				DefaultMutableTreeNode table = new DefaultMutableTreeNode(tableName);
				rootNodeDestino.add(table);
			}
			
			TabelaProcessarDAO tDAO = new TabelaProcessarDAO(connControle);
			ArrayList<TabelaProcessar> resultado = tDAO.selectAll();
			
			for (int i = 0; i < rootNodeDestino.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNodeDestino.getChildAt(i);
                for (int j = 0; j < resultado.size(); j++) {
                	if (childNode.getUserObject().equals(resultado.get(j).getNome_tb_destino())) {
                		DefaultMutableTreeNode process = new DefaultMutableTreeNode(resultado.get(j).getDescriçãoProcesso());
                		childNode.add(process);
    		            DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
    		            model.nodeStructureChanged(childNode);
                    }
				}
            }
		} catch (Exception e) {
			System.out.println("Erro ao buscar tabelas do banco destino!");
		}
	}
}