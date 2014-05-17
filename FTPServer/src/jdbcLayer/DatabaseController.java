/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbcLayer;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


// Example on Reflection 
class A
{
    public A()
    {
        System.out.println("good");
    }
}


/**
 * 
 * @author mohammad  
 */
public class DatabaseController
{

   
    private Connection connection=null;

    public void Connect(String Drivername,String url ,String username,String password)
    {
        try
        {
            // 1 :  Load DataBase Driver Manually (MySql Driver)
            Class.forName(Drivername).newInstance();

            // 2 : Connect To DataBase on DataBase Server (MySQl)
            connection=DriverManager.getConnection(url,username,password);
        } 

        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IllegalAccessException iex)
        {
            System.out.println(iex.getMessage());
        }
        catch(InstantiationException inex)
        {
            System.out.println(inex.getMessage());
        }
        catch(SQLException sqlex)
        {
            System.out.println(sqlex.getMessage());
        }
        
    }//End Connect



    /**
     * 
     * Get Search for File in DB and Return URl to the File if the File is Found else
     * return null
     * @param filename File wich you want to get it 
     * @return Return URl to the File if the File is Found else
     * return null
     *
     */
    public  String  getData(String filename)
    {

        try
        {
            //Create  SQL Statment
            Statement statement = connection.createStatement();
            
            String s="SELECT  url  from  filestable  where filename='"+filename+"'";

            ResultSet result= statement.executeQuery(s);
            
            while(result.next()) //this Statement Moves Cursor to first row
            {
                 return  result.getString("URL");
            }
            
            result.close();

            return null;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
            
            return  null;
        }
    }//End getData

    
    public boolean addNewRow(String filename,String url )
    {
        try
        {
            Statement insertStatment = connection.createStatement();

            String sqlString = "insert into filestable ( filename, url )"+ "values('" + filename + "', '" + url + "' )" ;

            insertStatment.execute(sqlString);

            return true;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    
   

}
