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
import mb.MemberFilter;

/**
 *
 * @author Tertius Cloete
 */
public class SwipeCardFilter implements Predicate {

    public String cardNumber=null;
    public String cardNumberColumnName=null;

    public SwipeCardFilter() {
    }

    public SwipeCardFilter(String cardNumberColumnName, String cardNumber) {
        this.cardNumberColumnName = cardNumberColumnName;
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean evaluate(RowSet rs) {
        
        if(cardNumber.isEmpty() || cardNumberColumnName.isEmpty()) throw new NullPointerException();
        
        try {
            if(rs.isBeforeFirst() | rs.isAfterLast()) return false;
            if (rs.getString(cardNumberColumnName).trim().contentEquals(cardNumber.trim())){
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
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
