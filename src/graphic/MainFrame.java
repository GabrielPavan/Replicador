package graphic;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import execution.ReplicacaoExecutar;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	ReplicacaoExecutar replicacao = null;
	
	private String origem = "jdbc:postgresql://localhost:5432/sistema", 
				   destino = "jdbc:mysql://localhost:3306/sistema";
	
	public JLabel lblNewLabel;
	
	public MainFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 160);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 434, 22);
		contentPane.add(menuBar);
		
		JMenu mnNewMenu = new JMenu("Configurações");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Conexões");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetConnectionDialog conexao = new SetConnectionDialog(origem, destino, MainFrame.this);
				
				origem = conexao.getOrigem();
				destino = conexao.getDestino();
			}
		});
		mntmNewMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.add(mntmNewMenuItem);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 81, 414, 29);
		contentPane.add(progressBar);
		
		JButton btnIniciarReplicação = new JButton("Iniciar");
		btnIniciarReplicação.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if( replicacao == null) {
						replicacao = new ReplicacaoExecutar(45000, origem, destino);
						btnIniciarReplicação.setText("Parar");
					} else {
						btnIniciarReplicação.setText("Iniciar");
					}
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnIniciarReplicação.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIniciarReplicação.setBounds(319, 33, 105, 37);
		contentPane.add(btnIniciarReplicação);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 33, 250, 37);
		contentPane.add(lblNewLabel);
		setVisible(true);
	}
}