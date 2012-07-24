/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NCB;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Rajiv
 */
public class Config{
    public String[] configuration(){
        String[] s=new String[6];
       // String ip="",port="",user="",pass="",db="";
        try{
        FileReader fr = new FileReader("settings.properties");
    	        BufferedReader br = new BufferedReader(fr);
    	        
    	        String src="";
    	        int i=0;
    	        while((src= br.readLine()) != null){
    	            s[i]=src.replaceAll(".*:", "");
    	            i++;
    	        }
//    	        	ip=s[0].toString();
//    			port=s[1].toString();
//    			user=s[2].toString(); 
//    			pass=s[3].toString();
//                        db=s[4].toString();
    	        fr.close();
    	 }catch(Exception ex){
    		 System.out.println("Error while configuration"+ex);
    	 }
        return s;
    }

}
