package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Conector {
	private String User, Password, DatabaseName;
	private TypeDatabase TypeDatabase;
	
	public Conector(TypeDatabase _pTypeDatabase, String _pDatabaseName) {
		TypeDatabase = _pTypeDatabase;
		DatabaseName = _pDatabaseName;
	}
	
	public Connection getConnection() throws SQLException {
		String _pConnectionString = null;
		switch (TypeDatabase) {
			case MONGODB: 
				break;
			case POSTGRES: 
				User = "postgres";
				Password = "1KUkd2HXpelZ7TkV6zU2";
				_pConnectionString = "jdbc:postgresql://localhost:5432/" + DatabaseName;
				break;
			case ORACLE: 
			
				break;
			case MYSQL: 
			
				break;
			default:
				JOptionPane.showMessageDialog(null, "Erro ao obter a conecxão!");
				break;
		}
		return DriverManager.getConnection(_pConnectionString, User, Password);
	}
}