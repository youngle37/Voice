package Voice;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txta;
	private JTextField input_ip;
	private JTextField input_port;
	private JLabel lblVoicesend;
	private JLabel lblVoicereceive;
	public static JTextField status_send;
	public static JTextField status_rece;
	JButton btn_start;
	JButton btn_stop;
	
	public int port;
	public String address;
	TargetDataLine audio_in;
	SourceDataLine audio_out;
	
	public static AudioFormat getaudioformat() {
		float sampleRate = 8000.0f;
		int sampleBitSize = 16;
		int channel = 2;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleBitSize, channel, signed, bigEndian);
	}
	
	public void init_audio() {
		
		AudioFormat format = getaudioformat();
		DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
		DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
		
		if(!AudioSystem.isLineSupported(info_in) || !AudioSystem.isLineSupported(info_out)) {
			System.out.println("Not supported :(");
			System.exit(0);
		}
		
		try {
			audio_out = (SourceDataLine)AudioSystem.getLine(info_out);
			audio_out.open(format);
			audio_out.start();
			
			audio_in = (TargetDataLine)AudioSystem.getLine(info_in);
			audio_in.open(format);
			audio_in.start();
			
			Main.trigger = true;
			
			Play_thread p = new Play_thread();
			Record_thread r = new Record_thread();
			
			p.audio_out = audio_out;
			p.din = new DatagramSocket(port);
			
			r.audio_in = audio_in;
			r.dout = new DatagramSocket();
			r.ip = InetAddress.getByName(address);
			r.port = port;
			
			p.start();
			r.start();
		} catch (LineUnavailableException | SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(230, 230, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txta = new JTextField();
		txta.setText("\u3010=\u25C8\uFE3F\u25C8=\u3011");
		txta.setBackground(new Color(230, 230, 250));
		txta.setBounds(5, 5, 424, 34);
		txta.setEditable(false);
		txta.setHorizontalAlignment(SwingConstants.CENTER);
		txta.setFont(new Font("Serif", Font.PLAIN, 20));
		contentPane.add(txta);
		txta.setColumns(10);
		
		btn_start = new JButton("Start");
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				port = Integer.parseInt(input_port.getText());
				address = input_ip.getText();
				init_audio();
				btn_start.setEnabled(false);
				btn_stop.setEnabled(true);
				input_port.setEditable(false);
				input_ip.setEditable(false);
			}
		});
		btn_start.setBounds(25, 137, 81, 94);
		btn_start.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(btn_start);
		
		btn_stop = new JButton("Stop");
		btn_stop.setEnabled(false);
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.trigger = false;
				btn_start.setEnabled(true);
				btn_stop.setEnabled(false);
				input_port.setEditable(true);
				input_ip.setEditable(true);
			}
		});
		btn_stop.setBounds(326, 137, 81, 94);
		btn_stop.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(btn_stop);
		
		JLabel lblIp = new JLabel("IP");
		lblIp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblIp.setHorizontalAlignment(SwingConstants.CENTER);
		lblIp.setBounds(76, 61, 46, 14);
		contentPane.add(lblIp);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPort.setHorizontalAlignment(SwingConstants.CENTER);
		lblPort.setBounds(76, 94, 46, 14);
		contentPane.add(lblPort);
		
		input_ip = new JTextField();
		input_ip.setBounds(146, 60, 191, 20);
		contentPane.add(input_ip);
		input_ip.setColumns(10);
		
		input_port = new JTextField();
		input_port.setBounds(146, 93, 191, 20);
		contentPane.add(input_port);
		input_port.setColumns(10);
		
		lblVoicesend = new JLabel("Send\uFF1A");
		lblVoicesend.setHorizontalAlignment(SwingConstants.CENTER);
		lblVoicesend.setBounds(132, 149, 68, 26);
		contentPane.add(lblVoicesend);
		
		lblVoicereceive = new JLabel("Receive\uFF1A");
		lblVoicereceive.setHorizontalAlignment(SwingConstants.CENTER);
		lblVoicereceive.setBounds(116, 187, 88, 26);
		contentPane.add(lblVoicereceive);
		
		status_send = new JTextField();
		status_send.setForeground(Color.RED);
		status_send.setBackground(new Color(230, 230, 250));
		status_send.setEditable(false);
		status_send.setHorizontalAlignment(SwingConstants.CENTER);
		status_send.setText("Disconnected");
		status_send.setBounds(193, 151, 111, 23);
		contentPane.add(status_send);
		status_send.setColumns(10);
		
		status_rece = new JTextField();
		status_rece.setForeground(Color.RED);
		status_rece.setBackground(new Color(230, 230, 250));
		status_rece.setEditable(false);
		status_rece.setText("Disconnected");
		status_rece.setHorizontalAlignment(SwingConstants.CENTER);
		status_rece.setColumns(10);
		status_rece.setBounds(193, 189, 111, 23);
		contentPane.add(status_rece);
	}
}
