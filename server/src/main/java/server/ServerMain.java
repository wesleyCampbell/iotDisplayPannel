package server;

public class ServerMain {
	//
	// ======================== GLOBAL VARIABLES =======================
	//
	
	private static final int DEFAULT_PORT = 24560;

	//
	// ======================== OUTPUT MESSAGES ========================
	//
	
	private static final String INVALID_PORT_MSG_TEMPLATE = """
		Invalid port '%s' provided. Please provide a valid port number and try again.""";
	private static final String USAGE_MSG = """
		Invalid parameters! Usage: `java -jar server.jar <port>`""";
	private static final String SERVER_RUN_MSG_TEMPLATE = """
		Server running on port %d...""";

	//
	// ========================= MAIN ============================
	//
	
	private static int verifyParameters(String[] args) {
		int serverPort;
		if (args.length == 0) {
			serverPort = DEFAULT_PORT;
		} else if (args.length == 1) {
			try {
				serverPort = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				System.out.println(String.format(INVALID_PORT_MSG_TEMPLATE, args[0]));
				System.exit(1);
				return -1;
			}
		} else {
			System.out.println(USAGE_MSG);
			System.exit(1);
			return -1;
		}

		return serverPort;
	}


	public static void main(String[] args) {
		System.out.println("Hello world!");

		int serverPort = verifyParameters(args);

		Server server = new Server();

		int actualPort = server.run(serverPort);

		System.out.println(String.format(SERVER_RUN_MSG_TEMPLATE, actualPort));
	}
}
