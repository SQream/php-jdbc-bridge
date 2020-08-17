/*
 * Copyright (C) 2007 lenny@mondogrigio.cjb.net
 *
 * This file is part of PJBS (http://sourceforge.net/projects/pjbs)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * This class is instatiated by the ServerThread class.
 * Contains the commands implementations.
 * @author lenny
 */
public class ServerCommands {
    
    private ServerThread serverThread;
    private Connection conn = null;
    PreparedStatement ps;
    private Hashtable results = new Hashtable();
    
    /** Creates a new instance of ServerCommands */
    public ServerCommands(ServerThread serverThread) {
        
        this.serverThread = serverThread;
    }
    
    /**
     * Connect to a JDBC data source.
     * @param cmd 
     */
    public void connect(String[] cmd) {
        
        if (conn == null && cmd.length == 4) {
            
            try {
                
                conn = DriverManager.getConnection(cmd[1], cmd[2], cmd[3]);
                serverThread.write("ok");
                
            } catch (SQLException ex) {
               
                Utils.log("error", "SQL exception encountered: " + ex.getMessage());           
                serverThread.write("ex");
            }
            
        } else {
            Utils.log("error", "Unexpected error encountered"); 
            serverThread.write("err");
        }
    }
    
    /**
     * Execute an SQL query.
     * @param cmd 
     */
    public void exec(String[] cmd) {
        
        Utils.log("Command: ", String.join(" ", cmd));
        Utils.log("Command length: ", String.valueOf(cmd.length));

        if (conn != null && cmd.length >= 2) {
            
            try {
                
                ps = conn.prepareStatement(cmd[1]);
                ps.setFetchSize(1);
                if (cmd.length > 2)
                    ps.setQueryTimeout(Integer.parseInt(cmd[2]));
                Utils.log("Query timeout: ", String.valueOf(ps.getQueryTimeout()));
                if (ps.execute()) {
                    
                    String id = Utils.makeUID();
                    results.put(id, ps.getResultSet());
                    serverThread.write("ok", id);
                    
                } else {
                    serverThread.write("ok", ps.getUpdateCount());
                    ps.close(); // If no resultset - closing statement
                }
                
            } catch (SQLException ex) {
     
                Utils.log("error", "SQL exception encountered: " + ex.getMessage());
                serverThread.write("ex");
            }
            
        } else {
           
            Utils.log("error", "Unexpected error encountered in exec()");
            serverThread.write("err");
        }
    }
    
    /**
     * Fetch a row from a ResultSet.
     * @param cmd 
     */
    public void fetch_array(String[] cmd) {
        
        if (conn != null && cmd.length == 2) {
            
            ResultSet rs = (ResultSet)results.get(cmd[1]);
            
            if (rs != null) {
                
                try {
                    
                    if (rs.next()) {
                        
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int cn = rsmd.getColumnCount();
                        
                        serverThread.write("ok", cn);
                        
                        for (int i = 1; i <= cn; i ++)
                            serverThread.write(rsmd.getColumnName(i), rs.getString(i));
                        
                    } else {
                        
                        serverThread.write("end");
                    }
                    
                } catch (SQLException ex) {
                   
                    Utils.log("error", "SQL exception encountered: " + ex.getMessage());
                    serverThread.write("ex");
                }
                
            } else {
               
                Utils.log("error", "Unexpected error encountered in fetch_array() resultSet");
                serverThread.write("err");
            }
            
        } else {
           
            Utils.log("error", "Unexpected error encountered in fetch_array()");
            serverThread.write("err");
        }
    }
    
    /**
     * Release a ResultSet.
     * @param cmd 
     */
    public void free_result(String[] cmd) {
        
        if (conn != null && cmd.length == 2) {
            
            ResultSet rs = (ResultSet)results.get(cmd[1]);
            
            if (rs != null) {
                
                results.remove(cmd[1]);
                serverThread.write("ok");
                
            } else {
               
                Utils.log("error", "Unexpected error encountered in free_result() resultSet");
                serverThread.write("err");
            }
            
        } else {
           
            Utils.log("error", "Unexpected error encountered in free_result()");
            serverThread.write("err");
        }
    }
    
    /**
     * Release the JDBC connection.
     */
    public void close() {
        
        if (conn != null) {
            
            try {
                
                conn.close();
                
            } catch (SQLException ex) {
                
                Utils.log("error", "could not close JDBC connection");
            }
        }
    }
}
