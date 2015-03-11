/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.*;
import javax.sql.rowset.Predicate;

/**
 *
 * @author PLATINUM
 */
public class MemberFilter implements Predicate {

    public String memberNumber=null;
    public String memberNumberColumnName=null;

    public MemberFilter() {
    }

    public MemberFilter(String memberNumberColumnName, String memberNumber) {
        this.memberNumberColumnName = memberNumberColumnName;
        this.memberNumber = memberNumber;
    }

    @Override
    public boolean evaluate(RowSet rs) {
        
        //if(memberNumber.isEmpty() || memberNumberColumnName.isEmpty()) throw new NullPointerException();
        
        try {
            if(rs.isBeforeFirst() || rs.isAfterLast()) return false;
            if (rs.getString(memberNumberColumnName).trim().contentEquals(memberNumber.trim())){
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            Logger.getLogger(MemberFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
        public boolean evaluate(Object value, int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean evaluate(Object value, String columnName) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
