package Voice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.TargetDataLine;

public class Record_thread extends Thread {
	
	public TargetDataLine audio_in;
	public DatagramSocket dout;
	byte byte_buf[] = new byte[512];
	public InetAddress ip;
	public int port;
	
	@Override
	public void run() {
		DatagramPacket data = new DatagramPacket(byte_buf, byte_buf.length, ip, port);
		
		while(Main.trigger) {
			try {
				audio_in.read(byte_buf, 0, byte_buf.length);
				dout.send(data);
				GUI.status_send.setText("Sent");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		GUI.status_send.setText("Disconnected");
		audio_in.close();
		audio_in.drain();
		dout.close();
	}
}
