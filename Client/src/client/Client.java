/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

/**
 *
 * @author mohammad
 */
public class Client
{
    public static void main(String[] args)
    {
        
        String filename="Lisp-summary.pdf";
        
        for(int i=0; i<10; i++)
        {
            new SendRequest(i,filename).start();
        }
    }
}
