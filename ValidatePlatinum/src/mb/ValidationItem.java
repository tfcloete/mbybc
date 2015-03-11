/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mb;

/**
 *
 * @author Tertius
 */
public class ValidationItem implements Comparable {
    
    public int itemTypeNumber;
    public String description;
    
    public ValidationItem(int itemTypeNumber, String description){
        this.itemTypeNumber=itemTypeNumber;
        this.description=description;
    }

    @Override
    public int compareTo(Object o) {
        
        ValidationItem x = (ValidationItem)o;
        if (x.itemTypeNumber>this.itemTypeNumber) return -1;
        if (x.itemTypeNumber<this.itemTypeNumber) return 1;
        return 0;
    }
    
}
