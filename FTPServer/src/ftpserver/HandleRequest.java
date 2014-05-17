/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ftpserver;

import com.sun.xml.internal.ws.message.stream.StreamAttachment;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbcLayer.*;



   enum  HandleRequsetState
{
    occupied,unoccupied;
}

public class HandleRequest extends Thread
{


    //Number of Bytes That you Want to Read From File and  Write to Stream
    public static final int BUFFER_SIZE = 102400;

    // Server for creates Conenction and Handle Requests from Clietns
    private ServerSocket Server = null;
    
    private  InputStream is; //inputStream for Reading Bytes

    private  OutputStream os;//OutputStream for Writting Bytes

    private  BufferedReader br;//Wrapping for Reading Primitive DataType

    private  BufferedWriter bw;//Wrapping for Writting Primitive DataType

    private  Socket sock;//Socket With Connected Client 
    
    private   byte[] buffer;//Buffer for Reading From File and Writting to The Stream

    public  HandleRequsetState ServerThreadState;//State of The Current Thread in the Pool


    //This is Static initializing
    static  DatabaseController controller=null; //this Connection is shared for All Thread 
    static 
    {
          //Information For Connection  to DB
            String Drivername="com.mysql.jdbc.Driver";
            String dbUrl="jdbc:mysql://127.0.0.1/lab";
            String username="root";
            String password="";

            controller =new DatabaseController();

            controller.Connect(Drivername,dbUrl,username,password); //Connect To Any MySQL DB
    }

    
    /**
     *  
     * @param s is ServerSocket(Connection) From Server To working with it
     */
    public HandleRequest(ServerSocket s)
    {
        super();

        this.Server=s;

        buffer=new byte[BUFFER_SIZE];

        ServerThreadState=HandleRequsetState.unoccupied;//initial value
    }

    @Override
    public void run()
    {
       
        //This Thread in not Died ,But After Handles the Requset From Client it
        //will  Wait for Anther Client To be Connect With it
        while(true)
        {
            try
            {
                // (1)
                sock = Server.accept();//Accept The Client Request
                
                ServerThreadState=HandleRequsetState.occupied;// After (1), become Thread is Busy Now

                is=sock.getInputStream();

                os=sock.getOutputStream();
 
                //To Read Primitive Data Type
                 br = new BufferedReader(new InputStreamReader(is));
          
                 bw = new BufferedWriter(new OutputStreamWriter(os));


                String filename=br.readLine(); //Reading Filename from Client

               //Send this File To The Client  if it was Found

               Sendfile(filename);
               

               //Release Resources


                br.close();
                bw.flush();

                bw.close();
                os.close();
                is.close();
                sock.close();

            }
            catch (IOException ex)
            {
                Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                ServerThreadState=HandleRequsetState.unoccupied; //Thread is Not Busy
            }
            
        }//End While(true) //Return(Come Back to Handle the New Client)
        
    }//End Run


    /**
     * 
     * @param name
     * @return
     */
    public void  Sendfile(String name)
    {

            String url=controller.getData(name);
            
            if(url!=null) //File is found
            {
                 FileInputStream fis = null;
                    try
                    {
                         bw.write("1\n"); //File is Founded

                         bw.flush();

                        try //Sleep The Current Thread (Server Thread)  until the Cleint Read "1\n"
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex)
                        {
                            Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        String Path=url+"\\"+name;
                        
                         //Open the File stream
                        fis = new FileInputStream(Path);
                        
                        //Read, and write the file to the socket
                        int j = 0;

                        while ((j = fis.read(buffer)) > -1)//Read bytes From file until get -1
                        {
                            os.write(buffer,0, j);//j is a number of bytes you read from file
                        }

                        bw.write(-1); // to signal the end of file??a

                        os.flush();

                        bw.flush();

                    }
                    catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ioe)
                    {
                        System.out.println(ioe.getMessage());
                    }
                    finally
                    {
                        try
                        {
                            fis.close();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    return; // ok
           }
            
//------------------------------------------------------------------------------

            try
            {
                 //Serach  in Another FTPServer
                  //FTP Server  Connect  With Another FTPServer Server To Get The
                  //File From it (if  th file  was Found in The  Another FTPServer )

                  //7000 is the Port of Another FTPServer
                  SendRequest Req =new SendRequest(0, name,"localhost",7000);
                  Req.start();

                 try
                 {
                    //Sleep The Current Thread(HandleRequest Thread) until The Req Thread Finish it is Work !
                    Thread.sleep(1000);// 100 Dosent Work -Try !!
                 }
                 catch (InterruptedException ex)
                 {
                    Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                 }
                   
                if(Req.flag==true)//Requseted File had  given From Another Server
                {
                     //Register New file in DB

                     //We Have an problem With ('') in SQL Statment
                     //url="D";//"@D:\\DataServer\\";

                     //controller.addNewRow(name,url);

                    System.out.println("search 4 file method");

                  Search4file(name); //Search Again in DataServer Folder
                }

                bw.write("0\n");//failed... Filename is Wrong
                bw.flush();

                return;
            }
            catch (IOException ex)
            {
                Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//End Send


        public boolean  Search4file(String name)
    {
            File ServerDir = new File("D:\\DataServer");

            File[] res = ServerDir.listFiles();

            boolean isfound = false;

            for (int i = 0; i < res.length; i++)
            {
                if (res[i].getName().equalsIgnoreCase(name))
                {
                    FileInputStream fis = null;
                    try
                    {
                        isfound = true;

                         bw.write("1\n"); //File is Founded

                         bw.flush();


                        try //Sleep The Current Thread (Server Thread)  until the Cleint Read "1\n"
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex) {
                            Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                        }

                         //Open the File stream
                        fis = new FileInputStream(res[i]);

                        //Read, and write the file to the socket
                        int j = 0;

                        while ((j = fis.read(buffer)) > -1)//Read bytes From file until get -1
                        {
                              os.write(buffer,0, j);//j is a number of bytes you read from file
                        }

                        bw.write(-1); // to signal the end of file??a

                        os.flush();

                        bw.flush();

                    }
                    catch (FileNotFoundException ex)
                    {
                        Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ioe)
                    {
                        System.out.println(ioe.getMessage());
                    }
                    finally
                    {
                        try
                        {
                            fis.close();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return  true;

                } //End if

            } //End For


            try
            {
                    bw.write("0\n");//failed... Filename is Wrong
                    bw.flush();

                    return  false;
            }
            catch (IOException ex)
            {
                Logger.getLogger(HandleRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
    }



}