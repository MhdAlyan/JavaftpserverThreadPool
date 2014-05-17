/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ftpserver;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;


/**
 *
 * @author mohammad
 */
public class FTPServer
{
  
    //Number of Static Connection in The S_arr
    public static int StaticConnections=10; //Edited in The Configuration of The Server

    //Number of Dynamic  Connection in The S_arr
    public static  int dynConnections=5; //Edited in The Configuration of The Server

    
     // first (n=StaticConnections) Connections(object from HandleRequests)
    //is Static and Remains  is dynamic as nedded
    public static  HandleRequest [] S_arr=new HandleRequest[StaticConnections+dynConnections]; //S_arry is Static Array


    //Return True if S_arr has unoccupied Threads
    public static  boolean  isnotfull()
    {
        for(int i=0; i<S_arr.length; i++)
        {
            if ((S_arr[i]!=null) && (S_arr[i].ServerThreadState==HandleRequsetState.unoccupied))
                    return  true;
              else if (S_arr[i]==null)
                   return false;
        }
        return false;
    }

    public   static void main(String[] args)
    {
        ServerSocket server=null;
        try 
        {
            //Server Open Cnnection at Prot=5000
             server= new ServerSocket(5000);

             int co=Thread.activeCount();
             
             for(int i=0; i<StaticConnections; i++)
             {
                 //All Thread is Shared With this Connections(Server)
                 S_arr[i]=new HandleRequest(server);

                 S_arr[i].start();
             }

            int co1=Thread.activeCount();

             boolean ok;
             //if S_arr has an unoccupied Threads
             while((ok=isnotfull()))
             {
                 System.out.println("in While Number of Thread = "+Thread.activeCount());
             }
             
             for(int j=StaticConnections; j<S_arr.length;j++)
             {
                 S_arr[j]=new HandleRequest(server);

                 S_arr[j].start();

                 // Also if S_arr has an unoccupied Threads
                 while((isnotfull()))
                 {
                    System.out.println("in for loop Number of Thread = "+Thread.activeCount());
                 }
             }
             
        }//End Try
        catch (IOException ex)
        {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
