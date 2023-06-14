package graphic;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.model.Conexoes;

import java.awt.Font;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javax.swing.JSeparator;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class SetTables extends JDialog {

	private JTree treeOrigem;
	private DefaultMutableTreeNode rootNodeOrigem, rootNodeDestino;
		
	public SetTables() {
		setTitle("Selecione as tabelas");
		setBounds(100, 100, 500, 380);
		getContentPane().setLayout(null);
		
		rootNodeOrigem = new DefaultMutableTreeNode("Origem");
		treeOrigem = new JTree(rootNodeOrigem);
		treeOrigem.setBounds(10, 45, 184, 285);
		getContentPane().add(treeOrigem);
		
		buscarTabelasOrigem();
		
		rootNodeDestino = new DefaultMutableTreeNode("Destino");
		JTree treeDestino = new JTree(rootNodeDestino);
		treeDestino.setBounds(285, 45, 184, 285);
		getContentPane().add(treeDestino);
		
		buscarTabelasDestino();
		
		JLabel lblDbOrigem = new JLabel("Origem");
		lblDbOrigem.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDbOrigem.setHorizontalAlignment(SwingConstants.CENTER);
		lblDbOrigem.setBounds(10, 11, 184, 23);
		getContentPane().add(lblDbOrigem);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(204, 11, 10, 319);
		getContentPane().add(separator);
		
		JButton btnAddDestino = new JButton(">>");
		btnAddDestino.setBounds(213, 124, 49, 39);
		getContentPane().add(btnAddDestino);
		
		JButton btnX = new JButton("X");
		btnX.setBounds(213, 185, 49, 39);
		getContentPane().add(btnX);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(273, 11, 2, 319);
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
		

		setVisible(true);
	}
	private void buscarTabelasOrigem() {
		try {
			Connection connControle = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
			
			ConexoesDAO cDAO = new ConexoesDAO(connControle);
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
			
		}
	}
	
	private void buscarTabelasDestino() {
		try {
			Connection connControle = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
			
			ConexoesDAO cDAO = new ConexoesDAO(connControle);
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
                  System.out.println(tableName);
                  DefaultMutableTreeNode table = new DefaultMutableTreeNode(tableName);
                  rootNodeDestino.add(table);
            }
		} catch (Exception e) {
			
		}
	}
}