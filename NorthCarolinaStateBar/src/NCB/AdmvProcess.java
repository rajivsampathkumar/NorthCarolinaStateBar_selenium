/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package NCB;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
/**
 *
 * @author RAJIV
 */
public class AdmvProcess {
    PreparedStatement psDelete=null;
    Statement stmtDelete=null;
    ResultSet rsDelete=null;
    ArrayList col=new ArrayList();
    ArrayList type=new ArrayList();
    String strQuerry="",strQuest="";
    void admv(String tbl1,String tbl2,String opTbl,String pk1,String pk2)throws Exception
    {
      
      DBActivityMInsLM db=new DBActivityMInsLM();
      DatabaseMetaData meta = db.con.getMetaData();
      ResultSet res = meta.getColumns(null, null,tbl2, null);      
      
      while (res.next()) {
          col.add(res.getString("COLUMN_NAME").toString());
          type.add(res.getString("TYPE_NAME").toString());
     }
     res.close();
     boolean flag=false;
     Statement stat = db.con.createStatement();
      Statement stat1 = db.con.createStatement();
     res=stat.executeQuery("select count(*) from "+tbl2+"");
     int tblrowcount=0;
     if(res.next()){
         tblrowcount=res.getInt(1);
     }
     System.out.println("table count"+tblrowcount);
     res.close();
     res=stat.executeQuery("select * from "+tbl2+"");   
     while(res.next())
     {

                 System.out.println("Inside Result");
                 ArrayList colms=new ArrayList();
                 String fields="",val="",PK="";
                 boolean f=true,pklimit=true;
                 for(int i=0;i<col.size();i++)
                 {
                     String typPK="";
                     for(int j=0;j<col.size();j++)
                     {
                         String colnamePK= col.get(j).toString();
                         if(colnamePK.equals(pk2)){
                             typPK=type.get(j).toString();
                         }
                         break;
                     }
                        System.out.println("***********************************");
                        String[] s=new String[3];
                        String colname= col.get(i).toString();
                        if(f)
                        {
                           f=false;
                           fields="source_"+colname+",final_"+colname+","+colname+"_ADMV";
                           val="?,?,?";
                        }
                        else
                        {
                          fields=fields+","+"source_"+colname+",final_"+colname+","+colname+"_ADMV";
                          val=val+","+"?,?,?";
                        }
                        String typ=type.get(i).toString();
                        String colVal="";
                        int tpeIdentifier=0;
                        System.out.println("Type >>>"+typ+" columnname>> "+colname);
                        if(pklimit)
                        {
                            pklimit=false;
                               if(typPK.equalsIgnoreCase("INT")||typPK.equalsIgnoreCase("BIGINT")||typPK.equalsIgnoreCase("DOUBLE")||typPK.equalsIgnoreCase("DECIMAL")||typPK.equalsIgnoreCase("FLOAT"))
                               {
                                  tpeIdentifier=1;
                                  PK=String.valueOf(res.getInt(pk2));
                                  System.out.println("Inside Int>>PK "+PK);
                               }
                               else
                               {
                                  tpeIdentifier=2;
                                  PK=res.getString(pk2).toString();
                                  System.out.println("Inside String>>>PK "+PK);
                               }
                          
                        }
                          System.out.println("PK>>>"+PK);
                          boolean r=false;
                          ResultSet finl=null;
                       try
                       {
                             PreparedStatement ps1=null;
                            // String sql="select * from Test where Ind_URL=?";
                            String sql="select * from "+tbl1+" where "+pk1+"=?";
                             System.out.println(""+sql);
                             ps1=db.con.prepareStatement(sql);
                             if(tpeIdentifier==1)
                             {
                             System.out.println("Int???");
                             ps1.setInt(1,Integer.parseInt(PK));
                             }
                             else
                             {
                             System.out.println("String???. "+PK);
                             ps1.setString(1,PK);
                             }
                           finl=ps1.executeQuery();
                           String colval2="",admv="";
                           if(finl.next())
                            {
                                System.out.println("Inside tabl1 contents");
                                
                                if(colname.equals(pk1))
                                {
                                    colVal=PK;
                                    colval2=PK;
                                    admv="V";
                                }
                                else
                                {
                                    System.out.println("column not pk");
                                    try
                                    {

                                        if(typ.equalsIgnoreCase("INT")||typ.equalsIgnoreCase("BIGINT")||typ.equalsIgnoreCase("DOUBLE")||typ.equalsIgnoreCase("DECIMAL")||typ.equalsIgnoreCase("FLOAT"))
                                        {
                                              System.out.println("column type...INT..."+colname);
                                              colval2=String.valueOf(finl.getInt(colname));
                                              colVal=String.valueOf(res.getInt(colname));
                                        }else
                                        {
                                                System.out.println("Col type String..."+colname);
                                                colval2=finl.getString(colname).toString();
                                                colVal=res.getString(colname).toString();
                                                System.out.println("Col values..."+colVal+","+colval2);
                                        }
                                    }
                                    catch(NullPointerException e)
                                    {
                                        colval2="";
                                        colVal="";
                                        r=true;
                                    }
                                    String currentData=colVal.trim();
                                    String sourceData=colval2.trim();
                                    currentData=currentData.toLowerCase();
                                    sourceData=sourceData.toLowerCase();
                                    System.out.println("Col1......."+colVal+" col2...."+colval2);
                                    if(r)
                                    {
                                        admv="";
                                        r=false;
                                    }
                                    if(currentData.equals("") && sourceData.equals(""))
                                    {
                                        admv="";
                                    }
                                    else if(sourceData.equals(currentData))
                                    {
                                        admv="V";
                                    }
                                    else if(currentData.equals("") && (!sourceData.equals("")))
                                    {
                                        System.out.println("Deleted>>>>>>>>>>>>>>>>>>>>>>");
                                        admv="D";
                                    }
                                    else if(sourceData.equals("") && (!currentData.equals("")))
                                    {
                                        admv="A";
                                        System.out.println("Added>>>>>>>>>>>>>>>>>>>>>>");
                                    }
                                    else if(!currentData.equals(sourceData))
                                    {
                                        admv="M";
                                        System.out.println("Modified>>>>>>>>>>>>>>>>>>>>>>");
                                    }
                                }
                                System.out.println("Col1 "+colVal+" col2 "+colval2+" admv result "+admv);
                                colms.add(colval2);
                                colms.add(colVal);                                
                                colms.add(admv);
                                colval2="";
                                admv="";
                            }
                            else
                            {
                                if(typ.equalsIgnoreCase("INT")||typ.equalsIgnoreCase("BIGINT")||typ.equalsIgnoreCase("DOUBLE")||typ.equalsIgnoreCase("DECIMAL")||typ.equalsIgnoreCase("FLOAT"))
                                        {
                                            System.out.println("column type...INT..."+colname);
                                              colval2="";
                                              colVal=String.valueOf(res.getInt(colname));
                                        }else
                                        {
                                                System.out.println("Col type String..."+colname);
                                                colval2="";
                                                colVal=res.getString(colname).toString();
                                               System.out.println("Col values..."+colVal+","+colval2);
                                        }
                                admv="A";
                                if(colVal.equals("")){
                                    admv="";
                                }
                                colms.add(colval2);
                                colms.add(colVal);
                                colms.add(admv);
                                colval2="";
                                colVal="";
                                
                            }
                            ps1.close();
                 }catch(Exception e){
                     System.out.println("Sql Exception "+e);
                 }

                           finl=null;
                   }
                                 int len2=colms.size();
                                 System.out.println("Length..."+len2);
                                 PreparedStatement ps=null;
                                 System.out.println(""+fields);
                                 System.out.println(""+val);
                                 String qryy="insert into "+opTbl+"("+fields+")values("+val+")";
                                 strQuerry=qryy;
                                 System.out.println("QUERRY>>>>>>>>>>>"+qryy);
                                 ps=db.con.prepareStatement(qryy);
                                 int j=1;
                                 for(int i=0;i<len2;i++)
                                 {
                                     ps.setString(j,colms.get(i).toString());
                                     j++;
                                 }
                                 ps.executeUpdate();

        }
     insertNotFound();

    }
    public void insertNotFound()
    {
        try
        {
            DBActivityMInsLM db=new DBActivityMInsLM();
            stmtDelete=db.con.createStatement();
            rsDelete=stmtDelete.executeQuery("select * from NCB_PREV where Harvested_Ind_URL NOT IN (select Harvested_Ind_URL from NCB_CUR)");
            while(rsDelete.next())
            {
                ArrayList alValues=new ArrayList();
                for(int i=0;i<col.size();i++)
                {
                    String strColVal=rsDelete.getString(col.get(i).toString());
                    if(strColVal==null)
                    {
                       strColVal="";
                       alValues.add(strColVal);
                       alValues.add("");
                       alValues.add("");
                    }
                    else
                    {
                       alValues.add(strColVal);
                       alValues.add("");
                       alValues.add("D");
                    }
                }
                psDelete=db.con.prepareStatement(strQuerry);
                for(int i=0;i<alValues.size();i++)
                {
                    psDelete.setString(i+1,alValues.get(i).toString());
                }
                psDelete.executeUpdate();
            }
        }
        catch(Exception e)
        {
            System.out.println("Error while inserting NotFound Content : "+e);
        }
    }

}
