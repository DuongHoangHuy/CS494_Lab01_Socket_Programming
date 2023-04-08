package Client;

import java.io.IOException;
import java.util.logging.Logger;

import UI.Register;

public class AppClient {
    static Logger logger = Logger.getLogger("root");
    static final int APP_PORT = 8001;
    static final String SERVER_IP = "localhost";

	public static void main(String[] args) throws IOException {
		TCPClient client = new TCPClient(SERVER_IP, APP_PORT, logger);
//		client.run();
		Register registerScreen = new Register(client);
		registerScreen.setVisible(true);
	}
}
