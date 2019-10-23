import java.net.*;
import java.io.*;
import java.util.*;



class RequestHandler implements Runnable{

	private List<Socket> genius;
	private List<Socket> user;
	private ServerSocket servidor;
	private Socket cliente;

	public RequestHandler(Servidor serv){
		this.genius =new ArrayList<Socket>();
		this.user = new ArrayList<Socket>();
		this.servidor = serv.serv;
	}	

	public void run(){

			System.out.println("Porta 12345 aberta!");
		//It extracts the first connection request on the queue of pending connections for the listening socket,
		//sockfd, creates a new connected socket, and returns a new file descriptor referring to that socket.
			while(true){
				try{
					// cliente e o socket pelo qual ele fala com o cliente
					System.out.println("Ainda nao aceitei nada ");
					//verifica que se é user
						this.cliente = servidor.accept();
						BufferedReader in = new BufferedReader( new InputStreamReader(cliente.getInputStream()));
						BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
						String input = in.readLine();	
						
						// se for USER
						if (input.equals("user")){
							System.out.println("entrou");
							System.out.println("Recebi string "+input);
							System.out.println("Nova conexão com o cliente " +	cliente.getInetAddress().getHostAddress());
							this.user.add(cliente);
							int temp = this.user.indexOf(cliente);
							PrintWriter out = new PrintWriter(cliente.getOutputStream(),true);
							if(this.genius.size()>=temp+1){
								//existe um genius para tratador
								System.out.println("Ligar a genius");
								PeerToPeer ligar = new PeerToPeer(this.servidor,this.genius.get(temp) ,cliente );
								String connect = ligar.pairToG();
								out.println(connect);
								input = "";
							}
							else{
								System.out.println("Esperar por genius");
								out.println("Aguarde");
								input = "";
							}
						}

							//SE FOR GENIUS
						if(input.equals("genius")){
							System.out.println("entrou");
							System.out.println("Recebi string "+input);
							System.out.println("Nova conexão com o cliente " +	cliente.getInetAddress().getHostAddress());
							input = "";
							this.genius.add(cliente);
							int temp = this.genius.indexOf(cliente);
							PrintWriter out = new PrintWriter(cliente.getOutputStream(),true);
							if(this.user.size()>= temp+1){
								//existe um um geniuLigr a s para tratador
								System.out.println("Ligar a user");
								PeerToPeer ligar = new PeerToPeer(this.servidor,cliente,this.user.get(temp));
								String connect = ligar.pairToU();
								out.println(connect);
								input = "";
							}
							else{
								System.out.println("Esperar por user");
								out.println("Aguarde");
								input ="";
							}
						}
					
				}				catch(IOException e){e.printStackTrace();}

				}
		}
		}


class Servidor{

	 ServerSocket serv;

	public Servidor(int porta) {
		try{
			this.serv = new ServerSocket (porta);
		}catch(IOException e){e.printStackTrace();}
	}

}

class PeerToPeer {

		private Socket clienteU;
		private Socket clienteG;
		private ServerSocket server;
		public PeerToPeer(ServerSocket server, Socket genius, Socket user) {
			this.server = server;
			this.clienteU = user;
			this.clienteG = genius;
		}
	
// esta classe trata de emparelhar clientes 	
	// o genius vai receber o socket do user para se ligar
	public String pairToG() {
		String data = null;
		try{
			PrintWriter out = new PrintWriter(clienteG.getOutputStream(), true);
			data = clienteG.getInetAddress().toString();
			data  = data +" "+ String.valueOf(clienteG.getPort());
			System.out.println(data);
		} catch(IOException e){e.printStackTrace();}
		return data;
	}
	
	// o user vai receber o socket do genius para se ligar
	public String pairToU() {
		String data = null;
		try{
			PrintWriter out = new PrintWriter(clienteU.getOutputStream(), true);
			data = clienteU.getInetAddress().toString();
			data  = data + " " + String.valueOf(clienteU.getPort());
			System.out.println(data);
		} catch(IOException e){e.printStackTrace();}
		return data;	
	}
}


public class RodaServidor {

	public static void main(String[] args)throws IOException {
		Servidor servidor = new Servidor(12345);
		
	//	while( true){
			RequestHandler target = new RequestHandler(servidor);
			Thread trataPedidos = new Thread(target);
			trataPedidos.start();
	//	}
	}
}
