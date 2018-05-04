package aPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;


public class JClient {
    JChatComm chat;
    public JClient() throws ClassNotFoundException, IOException {
    	BufferedReader ans = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Ingrese la dirección del servidor: ");
        this.callServer(ans.readLine(), 5123);
    }

    public void callServer(String hostName,int portNumber) throws ClassNotFoundException, UnknownHostException, IOException {
     	System.out.println("Intentando conectar al servidor ... ");
        Socket echoSocket = new Socket(hostName, portNumber);
        System.out.println("Conectado al servidor "+hostName);
        ObjectOutputStream out =  new ObjectOutputStream(echoSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(echoSocket.getInputStream());
        System.out.println("Chat listo ...\n");
        WaitingThread wait = new WaitingThread();
        chat = new JChatComm(echoSocket, in, out,"Cliente"); 
        JPacket pkt = new JPacket(new JMessage("Mis llaves públicas son: " + chat.myPublicKey+" and "+chat.myModulus));
        System.out.println("Yo : "+pkt.core.text);
        System.out.println("<enviado: "+pkt.timestamp+">\n");
        out.writeObject(pkt);
        wait.start();
        JPacket reply = (JPacket)(in.readObject());
        if ((reply.core.text).contains("Sure")) {
	        System.out.println(hostName+ " : " + reply.core.text);
	        System.out.println("[Recibido:"+ new Timestamp((new Date()).getTime())+"]");
	        Sender sender = new Sender(chat, "Yo: ");
	        Receiver receiver = new Receiver(chat, hostName+" : ", hostName);
	        sender.start();
	        receiver.start();
        }
        else {
            echoSocket.close();
            System.out.println("Fin de sesión");
        }
 
    }
}
