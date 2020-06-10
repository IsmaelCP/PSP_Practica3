package es.studium.PSP_Practica3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread {

	DataInputStream fentrada;
	Socket socket;

	public HiloServidor(Socket socket)
	{
		this.socket = socket;
		try
		{
			fentrada = new DataInputStream(socket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}

	// En el m�todo run() lo primeo que hacemos es enviar todos los mensajes actuales al cliente que se acaba de incorporar
	public synchronized void run()
	{
		ServidorJuego.mensaje.setText("N�mero de conexiones actuales: " + ServidorJuego.ACTUALES);
		String texto = ServidorJuego.textarea.getText();
		EnviarMensajes(texto);
		// Seguidamente, se crea un bucle en el que se recibe lo que el cliente escribe en el chat.
		// Cuando un cliente finaliza con el bot�n Salir, se env�a un * al servidor del Chat,
		// entonces se sale del bucle while, ya que termina el proceso del cliente,
		// de esta manera se controlan las conexiones actuales
		while(true)
		{
			String cadena = "";
			try
			{
				cadena = fentrada.readUTF();
				if(cadena.trim().equals("*"))
				{
					ServidorJuego.ACTUALES--;
					ServidorJuego.mensaje.setText("N�mero de conexiones actuales: " + ServidorJuego.ACTUALES);
					break;
				}
				// Comprueba si el n�mero dado por el jugador es ">", "<" o "=" que el n�mero oculto
				else
				{
					if(cadena.contains("."))
					{
						ServidorJuego.textarea.append(cadena + "\n");
					}
					else
					{
						String[] arrayJuego = cadena.split("> ");
						String nombre = arrayJuego[0];
						String numero = arrayJuego[1];
						if(Integer.parseInt(numero) < ServidorJuego.random)
						{
							ServidorJuego.textarea.append("> " + nombre + " piensa que el n�mero es el " + numero + ", pero el n�mero es MAYOR. \n");
						}
						else if(Integer.parseInt(numero) > ServidorJuego.random)
						{
							ServidorJuego.textarea.append("> " + nombre + " piensa que el n�mero es el " + numero + ", pero el n�mero es MENOR. \n");	
						}
						else if(Integer.parseInt(numero) == ServidorJuego.random)
						{
							ServidorJuego.textarea.append("> " + nombre + " piensa que el n�mero es el " + numero + ", y ha ACERTADOOOO!!!! \n" + "El ganad@r ha sido: " + nombre + ". \n" + "El juego ha finalizado. �Te atreves a jugar otra vez?");	
							texto = ServidorJuego.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(10000);
							System.exit(0);
						}
					}
					texto = ServidorJuego.textarea.getText();
					EnviarMensajes(texto);
				}
			}
			catch(Exception e)
			{
				try
				{
					fentrada.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
				e.printStackTrace();
				break;
			}
		}
	}

	// El m�todo EnviarMensajes() env�a el texto del textarea a todos los sockets que est�n en la tabla de sockets, 
	// de esta forma todos ven la conversaci�n.
	// El programa abre un stream de salida para escribir el texto en el socket

	private void EnviarMensajes(String texto)
	{
		for(int i=0; i<ServidorJuego.CONEXIONES; i++)
		{
			Socket socket = ServidorJuego.tabla[i];
			try
			{
				DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
				fsalida.writeUTF(texto);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}