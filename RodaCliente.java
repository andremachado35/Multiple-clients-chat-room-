import java.net.*;
import java.io.*;
import java.util.*;


class RodaCliente {

	public static void main(String[] args)
			throws UnknownHostException,	IOException {
		new Cliente("127.0.0.1", 12345).executa();
	}
}

class Cliente {

	private String host;
	private int porta;
	 String userName;

	public Cliente(String host, int porta) {
		this.host = host;
		this.porta = porta;
	}

	public String getUserName(){
		return this.userName;

	}

	public void executa() throws UnknownHostException, IOException {

			String ip_porta = null;
			Socket cliente  = new Socket(this.host, this.porta);

			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);

			BufferedReader in = new BufferedReader( new InputStreamReader(cliente.getInputStream()));
			BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));


			while(true){
				System.out.print("És user ou genius?  ");
				userName=sin.readLine();
				if(userName.equals("user") || userName.equals("USER") ){
				        out.println(userName);
					System.out.println("O User conetou-se ao servidor!");
					// aqui ja tenho o ip e porta do meu futuro par
					ip_porta = in.readLine();
					System.out.println("Dados do seu par:\n"+ip_porta);
					//conectar-se ao outro cliente
					break;
				}
				if(userName.equals("genius") || userName.equals("GENIUS") ){
					out.println(userName);
					System.out.println("O Genius conetou-se ao servidor!");
					// aqui ja tenho o ip e porta do meu futuro par
					ip_porta = in.readLine();
					System.out.println("Dados do seu par:"+ip_porta);
					//conectar-se ao outro cliente}
					break;

				}
				else System.out.println("input não válido");
			}
			this.emparelhar(ip_porta,cliente);
	}


	public void emparelhar(String str, Socket oldsocket){

	 	Console console=System.console();
		// se vai ligar ao seu par
		if(!str.equals("Aguarde")){
			try{
			String[] aux = str.split(" ");
			int port = Integer.parseInt(aux[1]);
			String ip = aux[0].replace("/", "");
			System.out.println("porta " + port);
			System.out.println("endereço ip "+ ip);

			Socket eu = new Socket(ip, port);
			System.out.println("Arranjei um par\n");
			new ReadThread(eu,this).start();
			new WriteThread(eu, this).start();

			}
		}catch(IOException e){e.printStackTrace();}}
		else{
			try{
				System.out.println("Sou o host deste chat");

				int port = oldsocket.getLocalPort();
				oldsocket.setReuseAddress(true);
				oldsocket.close();
				Socket par;
				ServerSocket cs = new ServerSocket(port);
				System.out.println("Aguardo par");
				par = cs.accept();

				new ReadThread(par,this).start();
				new WriteThread(par, this).start();



			}catch(IOException e){e.printStackTrace();}
		}
	}
}


//////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////
class ReadThread extends Thread{

	BufferedReader reader;
	Socket socket;
	Cliente client;

	public ReadThread(Socket socket, Cliente client) {
        this.socket = socket;
        this.client = client;


		try {
           		 InputStream input = socket.getInputStream();
            		reader = new BufferedReader(new InputStreamReader(input));
        	} catch (IOException ex) {

           	System.out.println("Error getting input stream: " + ex.getMessage());
                ex.printStackTrace();

        	}
    	}

	public void run() {
	String response;

        while (true) {
            try {
							response=reader.readLine();
							if(!response.equals("bye")){
								System.out.println("\n" + response);
								// prints the username after displaying the server's message
              if (client.getUserName() != null) {
              System.out.print("[" + client.getUserName() + "]: ");
			}
		}else{
			//System.out.println("O teu para bazou!!");
			break;
		}

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }

        }
    }

}
//////////////////////////////////////////////////////
class WriteThread extends Thread {
     PrintWriter writer;
     Socket socket;
     Cliente client;


    public WriteThread(Socket socket, Cliente client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Console console = System.console();
				String userName;
				userName= client.getUserName();

        String text;

        do {

            text = console.readLine("["+client.getUserName()+"]: ");
            writer.println("["+client.getUserName()+"]: "+text);

        } while (!text.equals("bye"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
