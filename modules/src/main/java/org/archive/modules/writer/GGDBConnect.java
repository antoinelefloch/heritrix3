/**
 *
 * Description: mysql related
 *
 * Note: You can copy this source code, but you need to keep these 2 lines in your file header.
 * author:  https://github.com/antoinelefloch
 *
 */
package org.archive.modules.writer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import com.mysql.jdbc.Connection;

/**
 * @author lefla
 *
 */
public class GGDBConnect {

    // final private String url = "jdbc:mysql://192.168.0.12:3306/test";
    final private String url = "jdbc:mysql://192.168.0.1:3306/whatever_schema"; // host of the docker container
    final private String un = "user";
    final private String pw = "password";
    final private String table = "jpg_seeds_22";

    private Statement stmt;

    // --- MD5 part
    private byte[] createChecksum(InputStream fis) throws Exception {
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    // ---
    private String getMD5Checksum(InputStream fis) throws Exception {
        byte[] b = createChecksum(fis);
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    // ---
    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        Connection con = null;
        try {
            con = (Connection) DriverManager.getConnection(url, un, pw);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String buildInsertQuery(String via, String url) {
        Calendar now = Calendar.getInstance();

        // System.out.println(" stringBuffer len = " + stringBuffer.length());

        String retVal = null;
        try {
            retVal = "INSERT INTO " + table + " (Date, Via, Url, Md5) VALUES ('" + now.get(Calendar.YEAR) + "-"
                            + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "', '" + via.replace("'", "_") + "', '"
                            + url.replace("'", "_") + "', '" + getMD5Checksum((new URL(url)).openStream()).replace("'", "_") + "'" + ");";
            int i = 1;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }

    public boolean insert(String via, String url) {
        try {
            stmt.executeUpdate(buildInsertQuery(via, url));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }
}
