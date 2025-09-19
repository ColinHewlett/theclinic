/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.groupable_table_header_support;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class GroupableTableHeaderUI extends BasicTableHeaderUI {

    @Override
    public void paint(Graphics g, JComponent c) {
        
        GroupableTableHeader header = (GroupableTableHeader) c;
        TableColumnModel columnModel = header.getColumnModel();
        System.out.println("Header class active in GroupableTableHeaderUI = " + header.getClass().getName());
        int columnCount = columnModel.getColumnCount();
        if (columnCount == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int headerHeight = header.getHeight();
        Color gridColor = UIManager.getColor("Table.gridColor");

        // Determine grouped columns set for quick lookup
        java.util.Set<TableColumn> groupedColumns = new java.util.HashSet<>();
        for (ColumnGroup group : header.getColumnGroups()) {
            groupedColumns.addAll(group.getColumns());
        }

        // Paint ungrouped columns (full header height)
        for (int i = 0; i < columnCount; i++) {
            TableColumn col = columnModel.getColumn(i);
            if (groupedColumns.contains(col)) continue;

            Rectangle cellRect = header.getHeaderRect(i); // accurate view-position
            paintColumnHeader(g, header, col, cellRect);
            
            // draw sort arrow (if any) for this view column index (i)
            SortOrder order = getColumnSortOrder(header.getTable(), i);
            if (order != null) {
                drawSortArrow(header, g2, cellRect, order);
            }

            g.setColor(gridColor);
            g.drawLine(cellRect.x + cellRect.width - 1, cellRect.y,
                       cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height);
        }

        // Paint grouped columns
        for (ColumnGroup group : header.getColumnGroups()) {
            if (group.getColumns().isEmpty()) continue;

            // find view index of first column in this group
            TableColumn firstCol = group.getColumns().get(0);
            int firstIndex = findColumnIndex(columnModel, firstCol);
            if (firstIndex == -1) continue; // safety

            // Compute x and width of the whole group using header.getHeaderRect for each column
            Rectangle firstRect = header.getHeaderRect(firstIndex);
            int groupX = firstRect.x;
            int groupWidth = 0;
            for (TableColumn col : group.getColumns()) {
                int idx = findColumnIndex(columnModel, col);
                if (idx == -1) continue;
                Rectangle r = header.getHeaderRect(idx);
                groupWidth += r.width;
            }

            // Top half: group title
            Rectangle groupRect = new Rectangle(groupX, 0, groupWidth, headerHeight / 2);

            // Use renderer background
            Component renderer = header.getDefaultRenderer()
                    .getTableCellRendererComponent(header.getTable(), "", false, false, -1, 0);
            g.setColor(renderer.getBackground());
            g.fillRect(groupRect.x, groupRect.y, groupRect.width, groupRect.height);

            // Draw group title centered
            g.setColor(header.getForeground());
            drawCenteredString(g, group.getText(), groupRect);
            
            
            
            // Draw group bottom border
            g.setColor(gridColor);
            g.drawLine(groupRect.x, groupRect.y + groupRect.height - 1,
                       groupRect.x + groupRect.width, groupRect.y + groupRect.height - 1);

            // Bottom half: paint each subcolumn header (accurate positions)
            for (TableColumn col : group.getColumns()) {
                int idx = findColumnIndex(columnModel, col);
                if (idx == -1) continue;
                Rectangle r = header.getHeaderRect(idx);
                Rectangle cellRect = new Rectangle(r.x, headerHeight / 2, r.width, headerHeight / 2);
                paintColumnHeader(g, header, col, cellRect);
                
                // draw sort arrow (if any) for this sub-column view index
                SortOrder order = getColumnSortOrder(header.getTable(), idx);
                if (order != null) {
                    drawSortArrow(header, g2, cellRect, order);
                }
                
                // Draw group title in bold
                /*Font originalFont = g.getFont();
                g.setFont(originalFont.deriveFont(Font.BOLD));
                g.setColor(header.getForeground());
                drawCenteredString(g, group.getText(), groupRect);
                g.setFont(originalFont); // restore*/

                // vertical line for subheader right edge
                g.setColor(gridColor);
                g.drawLine(cellRect.x + cellRect.width - 1, cellRect.y,
                           cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height);
            }

            // Vertical separator between grouped area and whatever follows (top half)
            g.setColor(gridColor);
            g.drawLine(groupX + groupWidth - 1, 0, groupX + groupWidth - 1, headerHeight / 2);
        }
        
    }
    
    private SortOrder getColumnSortOrder(JTable table, int column) {
        if (table.getRowSorter() == null) return null;
        for (RowSorter.SortKey sortKey : table.getRowSorter().getSortKeys()) {
            if (table.convertColumnIndexToView(sortKey.getColumn()) == column) {
                return sortKey.getSortOrder();
            }
        }
        return null;
    }

    /**
     * Draw the sort arrow on the given rect. 'owner' is used when painting the L&F icon.
     */
    private void drawSortArrow(Component owner, Graphics2D g2, Rectangle rect, SortOrder order) {
        if (order == null) return;
        Icon icon = UIManager.getIcon(order == SortOrder.ASCENDING
                ? "Table.ascendingSortIcon" : "Table.descendingSortIcon");

        if (icon != null) {
            int x = rect.x + rect.width - icon.getIconWidth() - 4;
            int y = rect.y + (rect.height - icon.getIconHeight()) / 2;
            icon.paintIcon(owner, g2, x, y);
            return;
        }

        // Fallback: simple filled triangle
        int size = Math.min(8, rect.height - 6);
        int x = rect.x + rect.width - size - 6;
        int y = rect.y + (rect.height - size) / 2;
        Polygon p = new Polygon();
        if (order == SortOrder.ASCENDING) {
            p.addPoint(x, y + size);
            p.addPoint(x + size / 2, y);
            p.addPoint(x + size, y + size);
        } else {
            p.addPoint(x, y);
            p.addPoint(x + size / 2, y + size);
            p.addPoint(x + size, y);
        }
        Color old = g2.getColor();
        g2.setColor(UIManager.getColor("TableHeader.foreground"));
        g2.fill(p);
        g2.setColor(old);
    }

    // Find view index of a TableColumn by identity (safe even if identifiers differ)
    private int findColumnIndex(TableColumnModel cm, TableColumn col) {
        for (int i = 0; i < cm.getColumnCount(); i++) {
            if (cm.getColumn(i) == col) return i;
        }
        return -1;
    }

    private void paintColumnHeader(Graphics g, GroupableTableHeader header, TableColumn col, Rectangle rect) {
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(header.getTable(), col.getHeaderValue(), false, false, -1, 0);
        
        // If the renderer is a JLabel (the usual case), remove any icon to avoid duplicate arrows.
        if (comp instanceof JLabel) {
            JLabel lbl = (JLabel) comp;
            lbl.setIcon(null);                    // important: we will draw icon ourselves
            lbl.setHorizontalTextPosition(SwingConstants.LEFT); // text left of icon space
        }
        
        comp.setBounds(rect);
        comp.validate();
        comp.paint(g.create(rect.x, rect.y, rect.width, rect.height));
    }

    private void drawCenteredString(Graphics g, String text, Rectangle rect) {
        FontMetrics fm = g.getFontMetrics();
        int x = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
    }
} 
    
    
   /* @Override
    public void paint(Graphics g, JComponent c) {
        System.out.println("GroupableTableHeaderUI paint method entered: " + c.getClass().getName());
        GroupableTableHeader header = (GroupableTableHeader) c;
        TableColumnModel columnModel = header.getColumnModel();

        int columnCount = columnModel.getColumnCount();
        if (columnCount == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int headerHeight = header.getHeight();
        int x = 0;
        Color gridColor = UIManager.getColor("Table.gridColor");

        // Determine grouped columns
        java.util.Set<TableColumn> groupedColumns = new java.util.HashSet<>();
        for (ColumnGroup group : header.getColumnGroups()) {
            groupedColumns.addAll(group.getColumns());
        }

        // Paint ungrouped columns
        for (int i = 0; i < columnCount; i++) {
            TableColumn col = columnModel.getColumn(i);
            int colWidth = col.getWidth();

            if (!groupedColumns.contains(col)) {
                Rectangle cellRect = new Rectangle(x, 0, colWidth, headerHeight);
                paintColumnHeader(g, header, col, cellRect);

                g.setColor(gridColor);
                g.drawLine(cellRect.x + cellRect.width - 1, cellRect.y, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height);
            }

            x += colWidth;
        }

        // Paint grouped columns
        x = 0;
        for (ColumnGroup group : header.getColumnGroups()) {
            int groupWidth = 0;
            for (TableColumn col : group.getColumns()) {
                groupWidth += col.getWidth();
            }

            Rectangle groupRect = new Rectangle(x, 0, groupWidth, headerHeight / 2);

            // Use header background from default renderer
            Component renderer = header.getDefaultRenderer()
                .getTableCellRendererComponent(header.getTable(), "", false, false, -1, 0);
            g.setColor(renderer.getBackground());
            g.fillRect(groupRect.x, groupRect.y, groupRect.width, groupRect.height);

            g.setColor(header.getForeground());
            drawCenteredString(g, group.getText(), groupRect);

            // Draw group bottom border
            g.setColor(gridColor);
            g.drawLine(groupRect.x, groupRect.y + groupRect.height - 1,
                       groupRect.x + groupRect.width, groupRect.y + groupRect.height - 1);

            int subX = x;
            for (TableColumn col : group.getColumns()) {
                Rectangle cellRect = new Rectangle(subX, headerHeight / 2, col.getWidth(), headerHeight / 2);
                paintColumnHeader(g, header, col, cellRect);

                g.setColor(gridColor);
                g.drawLine(cellRect.x + cellRect.width - 1, cellRect.y, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height);
                subX += col.getWidth();
            }

            // Draw vertical right border of grouped area
            g.drawLine(x + groupWidth - 1, 0, x + groupWidth - 1, headerHeight / 2);

            x += groupWidth;
        }
    }*/
/*
    private void paintGroupHeader(Graphics g, String text, Rectangle rect) {
        Component renderer = header.getDefaultRenderer()
                .getTableCellRendererComponent(header.getTable(), "", false, false, -1, 0);
        g.setColor(renderer.getBackground());
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(header.getForeground()); // or Color.GRAY for lines
        drawCenteredString(g, text, rect);
    }
*/
    /*
    private void paintColumnHeader(Graphics g, GroupableTableHeader header, TableColumn col, Rectangle rect) {
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(header.getTable(), col.getHeaderValue(), false, false, -1, 0);
        comp.setBounds(rect);
        comp.validate();
        comp.paint(g.create(rect.x, rect.y, rect.width, rect.height));
    }

    private void drawCenteredString(Graphics g, String text, Rectangle rect) {
        FontMetrics fm = g.getFontMetrics();
        int x = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
    }
}*/
