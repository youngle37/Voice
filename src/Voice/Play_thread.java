package Voice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.sound.sampled.SourceDataLine;

public class Play_thread extends Thread{
	
	public SourceDataLine audio_out;
	public DatagramSocket din;
	byte buffer[] = new byte[512];
	
	@Override
	public void run() {
		DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
		
		try {
			din.setSoTimeout(10000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(Main.trigger) {
			try {
				din.receive(incoming);
				buffer = incoming.getData();
				audio_out.write(buffer, 0, buffer.length);
				GUI.status_rece.setText("Received");
			} catch (SocketTimeoutException e) {
				Main.trigger = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		GUI.status_rece.setText("Disconnected");
		audio_out.close();
		audio_out.drain();
		din.close();
	}
}
