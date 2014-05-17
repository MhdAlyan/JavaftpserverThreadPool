

package client;

import java.net.*;
import  java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.PortableServer.THREAD_POLICY_ID;

public class SendRequest  extends Thread
{
    //Number of Bytes you Want to Read From Stream  and  Write to File
    private static final int BUFFER_SIZE = 102400;
    
    private byte[] buffer;//Buffer for Reading From Stream  and Writting to The File

    private Socket sock; //Socket for Connect With Server

    private int ClientNumber; //for IDENTIFYING The Client

    private  BufferedReader br;//Wrapping for Reading Primitive DataType

    private BufferedWriter bw;//Wrapping for Writting Primitive DataType

    private  String filename; //name  of the file the Client you Want to get it From FTPServer

    public SendRequest(int number,String filename)
    {        
       buffer=new byte[BUFFER_SIZE];

       this.ClientNumber=number;

       try 
       {
           //Connect To The Server
            sock = new Socket("localhost", 5000);
            
            this.filename=filename;
       }
       catch (IOException ex)
       {
            System.out.println("error on Client in Constructor");
       }

    }

   @Override
    public void run()
    {
            int i = 0;
            
            InputStream is;

            OutputStream os;
            
            try
            {
                is= sock.getInputStream();

                os= sock.getOutputStream();

                br = new BufferedReader(new InputStreamReader(is));

                bw = new BufferedWriter(new OutputStreamWriter(os));

                bw.write(filename+"\n");//Send Filename to Server
                
                bw.flush();// This Statment is Mandatory

                //Check if The Server  Found filename
                String state=br.readLine();

                if(state.equalsIgnoreCase("0")) //failed... Filename is Wrong
                {
                    System.out.println("failed... Filename is Wrong");
                    
                    return ;
                }

                //Create File at Destination Path
                FileOutputStream fos = new FileOutputStream("D:\\ClientData\\"+ClientNumber+"_"+filename);
              
                // state=1  //filename is founded at Server
                while((i=is.read(buffer))!=-1)// 
                {
                    fos.write(buffer, 0, i);
                }

                fos.flush();
                
                is.close();
                os.close();
                fos.close();
                br.close();
                
            } 
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
    }//End run
}
