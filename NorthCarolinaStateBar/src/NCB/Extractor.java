package NCB;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.firefox.FirefoxDriver;


/**
 *
 * @author developer
 */
public class Extractor {
	
   static DBActivityMInsLM db = null;
   static Connection con = null;
    static Statement stmt = null;
   static  Statement stUpdte = null,stmtdelete=null;
   static  PreparedStatement stmtstatusupdate=null;
    
    //ResultSet rs = null;
    static String line,strInputid;
    
   /* public static void main(String []as)
    {
        idextractor(new FirefoxDriver());
                
    }*/
    public static void idextractor(FirefoxDriver driver)
    {
    	try{
    	   String src=null;   
           db=new DBActivityMInsLM();
        stUpdte=db.con.createStatement();
        
        ResultSet count=stUpdte.executeQuery("select * from NCB_Linkids where status=0");
        
        while(count.next())
        {
           strInputid=count.getString("Link");
           
                System.out.println(strInputid);
        	driver.navigate().to(strInputid);
   	      Thread.sleep(2000);
   	      src=driver.getPageSource().toString();
              Thread.sleep(3000);
   	   extractData(src,strInputid);
              String q="update NCB_Linkids set Status=1 where Link='"+strInputid+"'";
              System.out.println(q);
           stUpdte=db.con.createStatement();
                                stUpdte.executeUpdate(q);
                                stUpdte.close();
        
        
        }
          stmtdelete.executeUpdate("Truncate NCB_Linkids"); 
          stmtdelete.close();
        db.closeDB();
        driver.close();
    	
    	}catch (Exception e) {
            System.out.println("Error in idextraction" + e);
        }
    	
    }

    public static void extractData(String strSrc, String strUrl) {
        String H_Url="",strId="",strName = "", strAddress = "", strCity = "",strState="",strZip="",strCountry="", strPhone = "", strFax = "", strLicense = "", strStatus = "", strStatusdef = "", strDiscipline = "";
        try {
            
            System.out.println("regex condition");
            
            Pattern regex = Pattern.compile("id=\\\"W0016W0013TEXTBLOCKMID\\\".*?>ID.*?id=\\\"span_W0016W0013MID\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKNAME\\\".*?>Name.*?id=\\\"span_W0016W0013MDISPLAYNAME\\\".*?>(.*?)</span>.*? id=\\\"W0016W0013TEXTBLOCKMADDR1\\\".*?>Address.*?id=\\\"span_W0016W0013MADDR1\\\".*?>(.*?)</span>.*?id=\\\"span_W0016W0013MADDR2\\\".*?>(.*?)</span>.*?id=\\\"span_W0016W0013MADDR3\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMCITY\\\".*?>City.*?id=\\\"span_W0016W0013MCITY\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMSTATE\\\".*?>State.*?id=\\\"span_W0016W0013MSTATE\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMZIP\\\".*?>ZIP Code.*?id=\\\"span_W0016W0013MZIP\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCK2\\\".*?>Country.*?id=\\\"span_W0016W0013MCOUNTRY\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMWPHONE\\\".*?>Work Phone.*?id=\\\"span_W0016W0013MWPHONE\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMFAX\\\".*?>Fax.*?id=\\\"span_W0016W0013MFAX\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMEMAIL\\\".*?>Email.*?id=\\\"span_W0016W0013MEMAIL\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMLICDT\\\".*?>License Date.*?id=\\\"span_W0016W0013MLICDT\\\".*?>(.*?)</span>.*?.*?.*?id=\\\"W0016W0013TEXTBLOCKMSTAT\\\".*?>Status.*?id=\\\"span_W0016W0013MSTAT\\\".*?>(.*?)</span>.*?id=\\\"W0016W0013TEXTBLOCKMSTAT2\\\".*?>Status Definition.*?id=\\\"W0016W0013TXTSTATUSDEF\\\".*?>(.*?)</div>.*?id=\\\"W0016W0013TEXTBLOCK1\\\".*?>Discipline.*?id=\\\"W0016W0013W0100TXTDHCSTATUS\\\".*?>(.*?)</div>",
		Pattern.CANON_EQ | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
            Matcher regexMatcher = regex.matcher(strSrc);
            int i = 0;
            
            while (regexMatcher.find()) {
                i = 1;
                
                strId = regexMatcher.group(1).trim();
                strName = regexMatcher.group(2).replaceAll("amp;", "").trim();
                strAddress = regexMatcher.group(3)+regexMatcher.group(4)+regexMatcher.group(5);
                strAddress=strAddress.replaceAll("amp;", "").trim();
                strCity = regexMatcher.group(6).replaceAll("amp;", "").trim();
                strState = regexMatcher.group(7).replaceAll("amp;", "").trim();
                strZip = regexMatcher.group(8).replaceAll("amp;", "").trim();
                strCountry = regexMatcher.group(9).replaceAll("amp;", "").trim();
                strPhone = regexMatcher.group(10).replaceAll("amp;", "").trim();
                strFax = regexMatcher.group(11).replaceAll("amp;", "").trim();
                strLicense = regexMatcher.group(12).replaceAll("amp;", "").trim();
                strStatus = regexMatcher.group(13).replaceAll("amp;", "").trim();
                strStatusdef= regexMatcher.group(14).replaceAll("<.*?>", "").replaceAll("amp;", "").trim();
                strDiscipline = regexMatcher.group(15).replaceAll("<.*?>", "").replaceAll("amp;", "").trim();
               
                System.out.println(strUrl+ strId+ strName+ strAddress+ strCity+ strState+ strZip+ strCountry+ strPhone+ strFax+ strLicense + strStatus+ strStatusdef+  strDiscipline);
                //db = new DBActivityMInsLM();
                db.insertQry(strUrl, strId, strName, strAddress, strCity, strState, strZip, strCountry, strPhone, strFax, strLicense , strStatus, strStatusdef,  strDiscipline);
                //db.closeDB();
            }
 } catch (Exception e) {
            System.out.println("Error in extractData " + e);
        }
    }
}
