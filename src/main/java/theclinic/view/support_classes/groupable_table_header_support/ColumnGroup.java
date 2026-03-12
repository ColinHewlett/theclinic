/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.groupable_table_header_support;

import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;

public class ColumnGroup {
    protected String text;
    protected List<TableColumn> columns = new ArrayList<>();

    public ColumnGroup(String text) {
        this.text = text;
    }

    public void add(TableColumn column) {
        columns.add(column);
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public String getText() {
        return text;
    }
}
