package tue.dnssd.context.tag.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nina on 5/6/14.
 */
public class Tag extends JPanel {
    private Tag me = this;
    private String _name;
    private Set<ActionListener> _addListeners;
    private Set<ActionListener> _removeListeners;
    private int _actionId = 0;


    public String getName() {
        return _name;
    }

    public Tag(String name) {
        _name = name;

        _addListeners = new HashSet<ActionListener>();
        _removeListeners = new HashSet<ActionListener>();

        this.add(new JLabel(name));

        JButton closeButton = new JButton("x");
        closeButton.addActionListener(removeAL);
        this.add(closeButton);

        this.setBackground(Color.lightGray);

        this.setLayout(new FlowLayout());
    }

    public void addToContainer(JPanel where) {
        notifyAddListeners();
        where.add(this);
        where.revalidate();
        _removeListeners.add(getJPanelRemoveListener(where));
    }

    private void notifyAddListeners() {
        for (ActionListener a : _addListeners) {
            a.actionPerformed(new ActionEvent(this, _actionId++, _name));
        }
    }

    private ActionListener getJPanelRemoveListener(final JPanel panel) {
        ActionListener jPanelRemoveListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panel.remove(me);
                panel.revalidate();
            }
        };

        return jPanelRemoveListener;
    }

    public void remove() {
        notifyRemoveListeners();
    }

    private void notifyRemoveListeners() {
        for (ActionListener a : _removeListeners) {
            a.actionPerformed(new ActionEvent(this, _actionId++, _name));
        }
    }

    private ActionListener removeAL = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            remove();
        }
    };

    public void addTagAddedListener(ActionListener a) {
        _addListeners.add(a);
    }

    public void addTagRemovedListener(ActionListener a) {
        _removeListeners.add(a);
    }

}
