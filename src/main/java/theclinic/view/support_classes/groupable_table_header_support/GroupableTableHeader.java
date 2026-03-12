/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.groupable_table_header_support;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;

public class GroupableTableHeader extends JTableHeader {
    private final List<ColumnGroup> columnGroups = new ArrayList<>();

    public GroupableTableHeader(TableColumnModel model) {
        super(model);
        updateUI();
        setReorderingAllowed(false);
    }

    public void addColumnGroup(ColumnGroup group) {
        columnGroups.add(group);
    }

    public List<ColumnGroup> getColumnGroups() {
        return columnGroups;
    }
    
    @Override
    public void updateUI(){
        setUI(new GroupableTableHeaderUI());
    }
}
