package NCB;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


public class DBActivityMInsLM{

	Connection con;
	boolean isCon;
	PreparedStatement stmtlink=null,stmt=null;
        static String strDbServer="";
	static String strDbName="";
	static String strDbUser="";
	static String strDbPass="";
        static String strtablename="";
	DBActivityMInsLM() throws FileNotFoundException, IOException{
             Properties props = new Properties();
	Reader read=(Reader)new FileReader(new File("settings.properties"));
        try{
			

			props.load(read);
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	  try{
	    //String[] s=new String[6];
            //Config cn=new Config();
            //s=cn.configuration();
            strDbServer=props.getProperty("DatabaseServer");
		strDbName=props.getProperty("DataBaseName");
		strDbUser=props.getProperty("user");
		strDbPass=props.getProperty("Password");
                strtablename=props.getProperty("InputTable");
		read.close();
		    Class.forName("com.mysql.jdbc.Driver");					
		    con=DriverManager.getConnection("jdbc:mysql://"+strDbServer+":3306/"+strDbName+"",""+strDbUser+"",""+strDbPass+"");
		    isCon=true;
                    System.out.println("Connected to Database");
		}catch(ClassNotFoundException cnfe){
			System.out.println("Error..."+cnfe);
		}
		catch(SQLException sqle){
			System.out.println("SQL Error..."+sqle);
		}
	}

	void closeDB()throws Exception{
	        con.close();
	}
	void insertids(String link)
	{
		try{
			
			//PreparedStatement stmt=null;
			stmtlink=con.prepareStatement("insert into NCB_Linkids(Link,Status) values(?,?)");
			stmtlink.setString(1,link);
			stmtlink.setInt(2,0);
			stmtlink.executeUpdate();
			stmtlink.close();
		}catch(Exception e){
            System.out.println("Error "+e);
        }
	}
	void insertQry(String hurl,String strId,String strName,String strAddress,String strCity,String strState,String strZip,String strCountry,String strPhone,String strFax,String strLicense ,String strStatus,String strStatusdef, String strDiscipline)throws Exception
	{
		try{
		
		//PreparedStatement stmt=null;
		stmt=con.prepareStatement("insert into NCB_CUR(Harvested_Ind_URL,ID, Name, Address,City,State,Zip,Country,Phone,Fax,License,Status ,Status_definition,Discipline) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		stmt.setString(1,hurl);
		stmt.setString(2,strId);
		stmt.setString(3,strName);
		stmt.setString(4,strAddress);
		stmt.setString(5,strCity);
		stmt.setString(6,strState);
		stmt.setString(7,strZip);
                stmt.setString(8,strCountry);
                stmt.setString(9,strPhone);
                stmt.setString(10,strFax);
                stmt.setString(11,strLicense);
                stmt.setString(12,strStatus);
                stmt.setString(13,strStatusdef);
                stmt.setString(14,strDiscipline);
		stmt.executeUpdate();
		stmt.close();
            }catch(Exception e){
                System.out.println("Error "+e);
            }
	}	
}
