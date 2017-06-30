package com.zaumal.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnectMysql {
	public static void main(String[] args){
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/gxl?useUnicode=true&characterEncoding=utf8&useSSL=false" ;    
		String username = "gxl" ;   
		String password = "gxl" ;   
		
		Connection con = null;
		 try {
			Class.forName(driver) ;
			con = DriverManager.getConnection(url , username , password ) ;  
			
			System.out.println(con);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   finally{
			if(null != con){
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
