/* 
 * Zohair Aashiq
 * Albert-Ludwigs-Universitaet Freiburg
 * Institut fuer Informatik
 */
package org.eclipse.rdf4j.recommender.datastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

//TODO
//This class has to be tested to make sure that the table in the db is actually
//emptied before inserting new values!
//Up to now I haven't managed to get rid of the problem of emptying the tables.

//The Map should throw exceptions of kind RecommenderException

/**
 * Persistent map. Keys and values are backed in a database.
 */
public class PersistentMap<K,V> extends AbstractMap<K,V>{
        /*--------*
	 * Static *
	 *--------*/ 
    
        private static Connection con;
        
        /*--------*
	 * Fields *
	 *--------*/
        private Statement st = null;           
        private PreparedStatement pSt = null;

        private Set<K> keySet = new HashSet<K>();

        private final String dbAccess = "jdbc:hsqldb:file:INVLISTS.db";
        private final String dbUserName = "sa";
        private final String dbPassword = "";
        private final String jdbcDriver = "org.hsqldb.jdbcDriver";
        private final String createTableSql = 
                "CREATE cached TABLE INVLISTS (" +
                "KEY INT PRIMARY KEY NOT NULL," +
                "INVLIST OBJECT NOT NULL" + 
                ") " ;       
        //private String dropDatabase = "DROP DATABASE INVLISTS"; 
        private String dropTableSql = "DROP TABLE IF EXISTS INVLISTS";  
        private String insertPairSql = "INSERT INTO INVLISTS (KEY, INVLIST) values (?, ?)";
        private String updatePairSql = "UPDATE INVLISTS SET INVLIST = ? WHERE KEY = ?";
        private String retrieveInvListSql = "SELECT * FROM INVLISTS WHERE KEY = ?";    
    
        /*-------------*
	 * Constructor *
	 *-------------*/
        
        public PersistentMap() {
                try {
                        Class.forName(jdbcDriver);		
                        con = DriverManager.getConnection(dbAccess, dbUserName, dbPassword);
                        st = con.createStatement();
                        st.executeUpdate(dropTableSql);         
                        st = con.createStatement();
                        st.executeUpdate(createTableSql);
                        con.close();
                } catch (ClassNotFoundException ex) {
                        System.out.println("CANNOT CREATE DATABASE");
                        System.out.println(ex.getMessage());
                } catch (SQLException ex) {
                        System.out.println("CANNOT CREATE DATABASE");
                        System.out.println(ex.getMessage());
                }                                      
        }
            
        /*---------*
	 * Methods *
	 *---------*/ 
    
        public Set<K> keySet() {
                return keySet;
        }
                      
        public V put(K key, V value) {
                try {            		
                        con = DriverManager.getConnection(dbAccess, dbUserName, dbPassword);
                        //System.out.println(deserializeObject(serializeObject(value)));
                        if (keySet.contains(key)) {
                                pSt = con.prepareStatement(updatePairSql);
                                pSt.setInt(2, key.hashCode());
                                pSt.setObject(1, value, Types.JAVA_OBJECT);
                                pSt .executeUpdate(); 
                        }
                        else {
                                pSt = con.prepareStatement(insertPairSql);
                                pSt.setInt(1, key.hashCode());
                                pSt.setObject(2, value, Types.JAVA_OBJECT);
                                pSt.executeUpdate();          
                                keySet.add(key);
                        }
                        con.close();
                } catch (SQLException ex) {
                        System.out.println("CANNOT INSERT INTO DATABASE");
                        System.out.println(ex.getMessage());
                }
                return null;
        }

        public V get(Object key) {
                try {
                                    con = DriverManager.getConnection(dbAccess, dbUserName, dbPassword);
                                    pSt = con.prepareStatement(retrieveInvListSql);
                                    pSt.setInt(1, key.hashCode());        		
                        ResultSet rs = pSt.executeQuery();
                        if (rs.next()) {
                                return (V)rs.getObject(2);
                        }
                        con.close();
                } catch (SQLException ex) {
                        System.out.println("CANNOT GET VALUE FROM DATABASE");
                        System.out.println(ex.getMessage());
                        return null;
                }
                return null;
        }

        @Override
        public boolean containsKey(Object key) {
                return keySet().contains(key);
        }

        @Override
        public String toString() {
                    String output = new String("");
                    for (K key: keySet()) {
                                    output = output + key.toString() + "=";
                                    output = output + get(key).toString() + ", ";

                    }
                    output = output.substring(0, output.length() - 2);
                    return output;
        }    
        /*
        public void getAll() {
            try {                            
                    st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM INVLISTS");// WHERE KEY = " + key.hashCode());
                    while (rs.next()) {
                            System.out.println(rs.getObject(1));
                            System.out.println(rs.getObject(2));
                    }

            } catch (SQLException ex) {
                    System.out.println("CANNOT GET VALUE FROM DATABASE");
                    System.out.println(ex.getMessage());
            }
        }
        */

        @Override
        public Set entrySet() {
                return null;
        }
}
