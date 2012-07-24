/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NCB;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {

    static String strDbServer = "";
    static String strDbName = "";
    static String strDbUser = "";
    static String strDbPass = "";
    static String strtablename = "";
    static String truncate = "",status="";
    static String start = "";
    static String end = "", proxyIP = "", port = "";
    Connection con = null;
    static Statement stmt = null;
    static Statement stUpdte = null;
    ResultSet rs = null;
    DBActivityMInsLM db = null;

    public static void loadProps() throws Exception {
        Properties props = new Properties();
        Reader read = (Reader) new FileReader(new File("settings.properties"));
        try {
            System.out.println("start");

            props.load(read);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        strDbServer = props.getProperty("DatabaseServer");
        strDbName = props.getProperty("DataBaseName");
        strDbUser = props.getProperty("user");
        strDbPass = props.getProperty("Password");
        strtablename = props.getProperty("InputTable");
        truncate = props.getProperty("Truncate");
        status=props.getProperty("StatusUpdate");
        start = props.getProperty("Start");
        end = props.getProperty("End");
        proxyIP = props.getProperty("ProxyIP");
        port = props.getProperty("Port").toString();

        read.close();
    }

    //  FirefoxDriver driver = new FirefoxDriver();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        try {


            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream("OutputFile.txt")));
            System.setErr(new java.io.PrintStream(new java.io.FileOutputStream("ErrorFile.txt")));
            System.out.println("North Carolina State Bar");
            new Main().execute();
        } catch (Exception e) {
            System.out.println("Error in main :" + e);
        }

    }

    public void moveTableAsPrev(DBActivityMInsLM db) {
        try {

            stmt = db.con.createStatement();
            if (truncate.equals("true")) {
                
//             

                stmt.executeUpdate("Truncate NCB_PREV");
                stmt.executeUpdate("INSERT INTO NCB_PREV select * from NCB_CUR");
                stmt.executeUpdate("Truncate NCB_CUR");
                stmt.executeUpdate("Truncate NCB_ADMV");
                
                

            }
            if(status.equals("true"))
            {
                stmt.executeUpdate("update Input_tbl set status=0");
            }
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error while moving table as previous :" + e);
        }
    }

    public void execute() {
        String evenrecord, oddrecord;
        Extractor extract = new Extractor();
        List<String> records = new ArrayList<String>();
        try {
            loadProps();
            db = new DBActivityMInsLM();
            stUpdte = db.con.createStatement();
            ResultSet count = stUpdte.executeQuery("select count(*) from  " + strtablename + " where status=1");
            while (count.next()) {
                int zeroCount = count.getInt(1);
                if (zeroCount == 0) {
                    moveTableAsPrev(db);
                }
            }

            stUpdte.close();
            stmt = db.con.createStatement();
            System.out.println("select * from " + strtablename + " where status=0 and id between " + start + " and " + end);
            rs = stmt.executeQuery("select * from " + strtablename + " where status=0 and id between " + start + " and " + end);
            FirefoxDriver driver = new FirefoxDriver();
            try {
                
            driver.get("http://www.ncbar.com/gxweb/mem_search.aspx");
                ((JavascriptExecutor) driver).executeScript("if (window.screen)"
                        + "{window.moveTo(0, 0);window.resizeTo(window.screen.availWidth,"
                        + "window.screen.availHeight);};");
            } catch (Exception exx) {
                System.out.println("Script Error:" + exx);
            }
            while (rs.next()) {

                String strInput = rs.getString("Search");
                int id = rs.getInt("id");
                
                driver.findElement(By.id("vMFNAME")).sendKeys(strInput);
                //driver.findElement(By.id("vMMNAME")).sendKeys("");
                driver.findElement(By.className("SpecialButtons")).click();
                //System.out.println(driver.getPageSource().toString());
                int i = 0;
                Thread.sleep(3000);
                System.out.println("Srearch id          -"+strInput);
                while (driver.getPageSource().contains("WorkWithOdd")) {
                    Thread.sleep(2000);
                    System.out.println("Page no" + (++i));

                    List<WebElement> allSuggestions = driver.findElements(By.className("WorkWithOdd"));

                    for (WebElement suggestion : allSuggestions) {
                        oddrecord = suggestion.findElement(By.tagName("a")).getAttribute("href").toString();
                        db.insertids(oddrecord);
                        records.add(oddrecord);
                        System.out.println(oddrecord);
                    }
                    Thread.sleep(1000);
                    List<WebElement> allSug = driver.findElements(By.className("WorkWithEven"));

                    for (WebElement sug : allSug) {
                        evenrecord = sug.findElement(By.tagName("a")).getAttribute("href").toString();
                        db.insertids(evenrecord);

                        records.add(evenrecord);
                        System.out.println(evenrecord);
                    }
                    Thread.sleep(5000);
                    if (driver.getPageSource().contains("PagingButtonsNext")) {
                        Thread.sleep(1000);
                        boolean allow = true;
                        while (allow) {
                            try {
                                System.out.println("clicking Next");
                                JavascriptExecutor pj = (JavascriptExecutor) driver;
                                pj.executeScript("gx.fn.setHidden('GRIDPAGING','NEXT');gx.evt.execEvt('EGRIDPAGING.', gx.evt.dummyCtrl);");
                                Thread.sleep(5000);
                                allow = false;
                            } catch (Exception e) {

                                if (e.getMessage().contains("org.openqa.selenium.StaleElementReferenceException")) {
                                    allow = true;
                                    Thread.sleep(3000);
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }

                stUpdte = db.con.createStatement();
                stUpdte.executeUpdate("update " + strtablename + "  set status=1 where id=" + id);
                stUpdte.close();
                driver.navigate().to("http://www.ncbar.com/gxweb/mem_search.aspx");
            }

            stmt.close();
            Extractor.idextractor(driver);
            stUpdte = db.con.createStatement();
            ResultSet count1 = stUpdte.executeQuery("select count(*) from " + strtablename + " where status=0");
            while (count1.next()) {
                int zeroCount = count1.getInt(1);
                if (zeroCount == 0) {
                    AdmvProcess adm = new AdmvProcess();
                    adm.admv("NCB_PREV", "NCB_CUR", "NCB_ADMV", "Harvested_Ind_URL", "Harvested_Ind_URL");
                }
            }
            stUpdte.close();

            db.con.close();
            //driver.close();
                /*
             * }
             * else { System.out.println("Regex Problem while taking Country
             * List"); }
             */
        } catch (Exception e) {
            System.out.println("Error in Execute :" + e);
        }

    }

    public int createAdmvTable() {
        int iOut = 0;
        try {
            db = new DBActivityMInsLM();
            DatabaseMetaData meta = db.con.getMetaData();
            ResultSet res = meta.getColumns(null, null, "NCB_CUR", null);
            System.out.println("Creating Output Table ");
            String cols = "", coma = "";
            while (res.next()) {
                String colname = res.getString("COLUMN_NAME").toString();
                String colType = res.getString("TYPE_NAME").toString();
                int colsize = res.getInt("COLUMN_SIZE");
                cols = cols + coma + "source_" + colname + " LONGTEXT," + "final_" + colname + " LONGTEXT," + colname + "_ADMV varchar(5)";
                coma = ",";
            }
            res.close();
            String opTbl = "NCB_ADMV";
            String TblCreate = "create table IF NOT EXISTS " + opTbl + " (" + cols + ")";
            System.out.println("" + TblCreate);
            Statement stat = db.con.createStatement();
            iOut = stat.executeUpdate(TblCreate);
            stat.close();
            db.closeDB();
        } catch (Exception ex) {
            System.out.println("Error while creating ADMV table " + ex);
        }
        return iOut;
    }
}