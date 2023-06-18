package graphic;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.model.Conexoes;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JSeparator;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

@SuppressWarnings("serial")
public class SetCreateTables extends JDialog {

	private JTree treeOrigem, treeDestino;
	private DefaultMutableTreeNode rootNodeOrigem, rootNodeDestino;
	private Connection connControle;
	private Connection connOrigem, connDestino;
	
	public SetCreateTables() {
		setResizable(false);
		setTitle("Gerenciamento das tabelas");
		setBounds(100, 100, 500, 340);
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
		
		rootNodeDestino = new DefaultMutableTreeNode("Destino");
		treeDestino = new JTree(rootNodeDestino);
		treeDestino.setBounds(285, 45, 184, 240);
		getContentPane().add(treeDestino);
		
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
	                	try {
							CreateTable(selectedObject);
						} catch (SQLException e1) {
							JOptionPane.showConfirmDialog(null, "Erro ao criar a tabela no destino!");
						}
	                	
	                	DefaultMutableTreeNode table = new DefaultMutableTreeNode(selectedObject);
		                rootNodeDestino.add(table);
		                treeDestino.updateUI();
	                } else {
	                	JOptionPane.showMessageDialog(null, "A tabela já existe no destino");
	                }
				}
			}
		});
		btnAddDestino.setBounds(213, 135, 49, 39);
		getContentPane().add(btnAddDestino);
		
		JButton btnX = new JButton("X");
		btnX.setEnabled(false);
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//try {
				//	TreePath selectionPath = treeDestino.getSelectionPath();
				//	if(selectionPath != null) {
				//		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
		        //      DefaultTreeModel model = (DefaultTreeModel) treeDestino.getModel();
		        //      model.removeNodeFromParent(selectedNode);
		        //        treeDestino.updateUI();
				//	}
				//} catch (Exception e2) {
				//		JOptionPane.showMessageDialog(null, "Não pode remover a raiz destino");
				//}
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
		
		buscarTabelasOrigem(connControle);
		buscarTabelasDestino(connControle);
		setVisible(true);
	}
	private void buscarTabelasOrigem(Connection conn) {
		try {
			ConexoesDAO cDAO = new ConexoesDAO(conn);
			Conexoes STR_CONN_ORIGEM = cDAO.SelectAllById(2);
			
			connOrigem = ConnectionFactory
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
			
			connDestino = ConnectionFactory
					.getConnection(	STR_CONN_DESTINO.getEndereco_ip(), 
									STR_CONN_DESTINO.getEndereco_porta(), 
									STR_CONN_DESTINO.getNome_banco(), 
									STR_CONN_DESTINO.getUsuario(), 
									STR_CONN_DESTINO.getSenha(), 
									STR_CONN_DESTINO.getTipo_banco());

            DatabaseMetaData metaData = connDestino.getMetaData();
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
	
	private void CreateTable(Object table) throws SQLException {
		Statement statementOrigin = connOrigem.createStatement();
		Statement statementDest = connDestino.createStatement();
		ResultSet result = statementOrigin.executeQuery("SELECT * FROM " + table);

		ResultSetMetaData metaData = result.getMetaData();
	
		StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + table + "(");
		StringBuilder createTableConstraint = new StringBuilder("");
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metaData.getColumnName(i);
			String columnType = metaData.getColumnTypeName(i);
			int columnSize = metaData.getColumnDisplaySize(i);

			if (columnType.contains("bigserial")) {
				columnType = "bigint";
			}
			if (columnName.equalsIgnoreCase("id")) {
				createTableConstraint = new StringBuilder("ALTER TABLE `" + table + "` ");
				createTableConstraint.append("CHANGE COLUMN `" + columnName + "` `" + columnName + "` " + columnType + " NOT NULL AUTO_INCREMENT, ");
				createTableConstraint.append("ADD PRIMARY KEY (`" + columnName + "`);");
			} 
			if (columnType.equalsIgnoreCase("date")) {
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
		
		JOptionPane.showMessageDialog(null, "Tabela " + table + " Criada no destino com sucesso!");
	}
}