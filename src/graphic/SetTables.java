package graphic;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.model.Conexoes;

import java.awt.Font;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JSeparator;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

@SuppressWarnings("serial")
public class SetTables extends JDialog {

	private JTree treeOrigem;
	private DefaultMutableTreeNode rootNodeOrigem, rootNodeDestino;
	private Connection connControle;
		
	public SetTables() {
		setTitle("Selecione as tabelas");
		setBounds(100, 100, 500, 380);
		getContentPane().setLayout(null);
		try {
			connControle = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		rootNodeOrigem = new DefaultMutableTreeNode("Origem");
		treeOrigem = new JTree(rootNodeOrigem);
		treeOrigem.setBackground(new Color(255, 255, 255));
		treeOrigem.setBounds(10, 45, 184, 240);
		treeOrigem.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getContentPane().add(treeOrigem);
		
		buscarTabelasOrigem(connControle);
		
		rootNodeDestino = new DefaultMutableTreeNode("Destino");
		JTree treeDestino = new JTree(rootNodeDestino);
		treeDestino.setBounds(285, 45, 184, 240);
		getContentPane().add(treeDestino);
		
		buscarTabelasDestino(connControle);
		
		JLabel lblDbOrigem = new JLabel("Origem");
		lblDbOrigem.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDbOrigem.setHorizontalAlignment(SwingConstants.CENTER);
		lblDbOrigem.setBounds(10, 11, 184, 23);
		getContentPane().add(lblDbOrigem);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(204, 11, 10, 274);
		getContentPane().add(separator);
		
		JButton btnAddDestino = new JButton(">>");
		btnAddDestino.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath selectionPath = treeOrigem.getSelectionPath();
				if (selectionPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
	                Object selectedObject = selectedNode.getUserObject();
	                
	                boolean itemExistente = false;
	                if(rootNodeDestino.getChildCount() == 0) {
	                	DefaultMutableTreeNode table = new DefaultMutableTreeNode(selectedObject);
		                rootNodeDestino.add(table);
		                treeDestino.updateUI();
		                return;
	                }
	                for (int i = 0; i < rootNodeDestino.getChildCount(); i++) {
	                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNodeDestino.getChildAt(i);
	                    if (childNode.getUserObject().equals(selectedObject)) {
	                        itemExistente = true;
	                        break;
	                    }
	                }
	                if (!itemExistente) {
	                	DefaultMutableTreeNode table = new DefaultMutableTreeNode(selectedObject);
		                rootNodeDestino.add(table);
		                treeDestino.updateUI();
	                } else {
	                	JOptionPane.showMessageDialog(null, "A tabela já existe no destino");
	                }
				}
			}
		});
		btnAddDestino.setBounds(213, 124, 49, 39);
		getContentPane().add(btnAddDestino);
		
		JButton btnX = new JButton("X");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath selectionPath = treeDestino.getSelectionPath();
					if(selectionPath != null) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
		                DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
		                model.removeNodeFromParent(selectedNode);
		                treeDestino.updateUI();
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "Não pode remover a raiz destino");
				}
			}
		});
		btnX.setBounds(213, 185, 49, 39);
		getContentPane().add(btnX);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(273, 11, 10, 274);
		getContentPane().add(separator_1);
		
		
		JLabel lblDbDestino = new JLabel("Destino");
		lblDbDestino.setHorizontalAlignment(SwingConstants.CENTER);
		lblDbDestino.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDbDestino.setBounds(285, 11, 184, 23);
		getContentPane().add(lblDbDestino);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 32, 184, 2);
		getContentPane().add(separator_2);
		
		JSeparator separator_2_1 = new JSeparator();
		separator_2_1.setBounds(285, 32, 184, 2);
		getContentPane().add(separator_2_1);
		
		JSeparator separator_2_1_1 = new JSeparator();
		separator_2_1_1.setBounds(10, 296, 459, 2);
		getContentPane().add(separator_2_1_1);
		
		JButton btnNewButton = new JButton("Add. Processo");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreePath selectionPath = treeDestino.getSelectionPath();
				if(selectionPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
					SetProcess setProcess = new SetProcess(SetTables.this);
					if (setProcess.getProcesso().trim().equals("")) {
						return;
					}
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(setProcess.getProcesso());
					selectedNode.add(newNode);
		            DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
		            model.nodeStructureChanged(selectedNode);
				}
			}
		});
		btnNewButton.setBounds(317, 307, 125, 23);
		getContentPane().add(btnNewButton);
		setVisible(true);
	}
	private void buscarTabelasOrigem(Connection conn) {
		try {
			ConexoesDAO cDAO = new ConexoesDAO(conn);
			Conexoes STR_CONN_ORIGEM = cDAO.SelectAllById(2);
			
			Connection connOrigem = ConnectionFactory
					.getConnection(	STR_CONN_ORIGEM.getEndereco_ip(), 
									STR_CONN_ORIGEM.getEndereco_porta(), 
									STR_CONN_ORIGEM.getNome_banco(), 
									STR_CONN_ORIGEM.getUsuario(), 
									STR_CONN_ORIGEM.getSenha(), 
									STR_CONN_ORIGEM.getTipo_banco());

            DatabaseMetaData metaData = connOrigem.getMetaData();
            ResultSet resultSet = metaData.getTables(STR_CONN_ORIGEM.getNome_banco(), null, "%", new String[] {"TABLE"});
            while (resultSet.next()) {
                  String tableName = resultSet.getString("TABLE_NAME");
                  DefaultMutableTreeNode table = new DefaultMutableTreeNode(tableName);
                  rootNodeOrigem.add(table);
            }
		} catch (Exception e) {
			System.out.println("Erro ao buscar tabelas do banco origem!");
		}
	}
	
	private void buscarTabelasDestino(Connection conn) {
		try {			
			ConexoesDAO cDAO = new ConexoesDAO(conn);
			Conexoes STR_CONN_DESTINO = cDAO.SelectAllById(1);
			
			Connection connOrigem = ConnectionFactory
					.getConnection(	STR_CONN_DESTINO.getEndereco_ip(), 
									STR_CONN_DESTINO.getEndereco_porta(), 
									STR_CONN_DESTINO.getNome_banco(), 
									STR_CONN_DESTINO.getUsuario(), 
									STR_CONN_DESTINO.getSenha(), 
									STR_CONN_DESTINO.getTipo_banco());

            DatabaseMetaData metaData = connOrigem.getMetaData();
            ResultSet resultSet = metaData.getTables(STR_CONN_DESTINO.getNome_banco(), null, "%", new String[] {"TABLE"});
            while (resultSet.next()) {
                  String tableName = resultSet.getString("TABLE_NAME");
                  DefaultMutableTreeNode table = new DefaultMutableTreeNode(tableName);
                  rootNodeDestino.add(table);
            }
		} catch (Exception e) {
			System.out.println("Erro ao buscar tabelas do banco destino!");
		}
	}
}