

package ftpserver;

import java.net.*;
import  java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendRequest  extends Thread
{
  //Number of Bytes you Want to Read From Another FTPServer Stream  and  Write
  //to FPTServer(main FTPServer) Stream
    private static final int BUFFER_SIZE = 102400;
    
    private byte[] buffer;//Buffer for Reading From Stream  and Writting to The File

    private Socket sock; //Socket for Connect With Server

    private int ClientNumber; //for IDENTIFYING The Client

    private  BufferedReader br;//Wrapping for Reading Primitive DataType

    private BufferedWriter bw;//Wrapping for Writting Primitive DataType

    private  String filename; //name  of the file the Client you Want to get it From FTPServer

//is and os have Defualt Access Modifier
            InputStream is;

            OutputStream os;




    //Fisrt Overload
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
            Logger.getLogger(SendRequest.class.getName()).log(Level.SEVERE, null, ex);
       }

    }

   //Second OverLoad
    public SendRequest(int number,String filename,String hostname , int Port)
    {
       buffer=new byte[BUFFER_SIZE];

       this.ClientNumber=number;

       try
       {
             //Connect To The Server
            sock = new Socket(hostname,Port);

            this.filename=filename;
       }
       catch (IOException ex)
       {
            Logger.getLogger(SendRequest.class.getName()).log(Level.SEVERE, null, ex);
       }

    }

    public boolean  flag;; // To Know if The Another FTPServer found the Filename

   @Override
    public void run()
    {
            int i = 0;
             
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

                    flag=false; //failed... Filename is Wrong or Server Does not Has Required file

                    return ;
                }

                flag=true;

                 //Create File at Destination Path
                FileOutputStream fos = new FileOutputStream("D:\\DataServer\\"+filename);

                System.out.println("Creating File in DataServer");

                // state=1  //filename is founded at Another FTPServer Server
                while((i=is.read(buffer))!=-1)// 
                {
                    fos.write(buffer, 0, i);
                }
                bw.write(-1); // to signal the end of file??a

                fos.flush();

                bw.flush();

                fos.close();

                bw.close();
                
                is.close();
                os.close();
                br.close();
                
            } 
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
    }//End run
}
