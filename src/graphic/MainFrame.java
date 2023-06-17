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
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;

	private ReplicacaoExecutar myReplExecutar = null;
	private boolean ThreadExec = false;
	
	private JLabel MainLabel;
	private JProgressBar progressBar;
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	public JLabel getLblNewLabel() {
		return MainLabel;
	}
	public void setLblNewLabel(JLabel lblNewLabel) {
		this.MainLabel = lblNewLabel;
	}
	
	public MainFrame() {
		setTitle("Replicador versão 1.0");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 160);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 484, 22);

		JMenu mnNewMenu = new JMenu("Configurações");
		mnNewMenu.setHorizontalAlignment(SwingConstants.CENTER);
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Conexões");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SetConnections();
			}
		});
		mntmNewMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Processos");
		mntmNewMenuItem_2.setHorizontalAlignment(SwingConstants.CENTER);
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SetProcess(null);
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);

		progressBar = new JProgressBar(0,100);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 81, 464, 29);

		JButton btnIniciarReplicação = new JButton("Iniciar");
		btnIniciarReplicação.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (!ThreadExec) {
	                if (myReplExecutar == null || !myReplExecutar.isAlive()) {
	                    myReplExecutar = new ReplicacaoExecutar(MainFrame.this);
	                    myReplExecutar.start();
	                }
	                btnIniciarReplicação.setText("Parar");
	            } else {
	                if (myReplExecutar != null && myReplExecutar.isAlive()) {
	                    myReplExecutar.interrupt();
	                }
	                btnIniciarReplicação.setText("Iniciar");
	            }
				ThreadExec = !ThreadExec;
			}
		});
		btnIniciarReplicação.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnIniciarReplicação.setBounds(369, 33, 105, 37);

		MainLabel = new JLabel("Replicador versão 1.0");
		MainLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		MainLabel.setBounds(10, 33, 349, 37);

		contentPane.add(menuBar);
		
		JMenu mnNewMenu2 = new JMenu("Definições da replicação");
		mnNewMenu2.setHorizontalAlignment(SwingConstants.CENTER);
		menuBar.add(mnNewMenu2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Tabelas");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SetTablesProcess();
			}
		});
		mntmNewMenuItem_3.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu2.add(mntmNewMenuItem_3);
		
		contentPane.add(progressBar);
		contentPane.add(btnIniciarReplicação);
		contentPane.add(MainLabel);
		setVisible(true);
	}
}