package tue.dnssd.context.tag.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Created by nina on 5/13/14.
 */
public class addTagOnAdapter extends KeyAdapter {
    private JTextField tagsSourceJTextField;
    private JPanel tagContainer;
    private List<String> tagStorage;

    public addTagOnAdapter(JTextField tagsSourceJTextField, JPanel tagContainer, List<String> tagStorage) {
        this.tagsSourceJTextField = tagsSourceJTextField;
        this.tagContainer = tagContainer;
        this.tagStorage = tagStorage;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            String newTag = tagsSourceJTextField.getText();
            tagsSourceJTextField.setText("");

            Tag t = new Tag(newTag);

            t.addTagAddedListener(tagAddedListener);
            t.addTagRemovedListener(tagRemovedListener);

            t.addToContainer(tagContainer);
        }
    }

    private ActionListener tagAddedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String tagName = actionEvent.getActionCommand();

            tagStorage.add(tagName);
        }
    };

    private ActionListener tagRemovedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String tagName = actionEvent.getActionCommand();

            tagStorage.remove(tagName);
        }
    };

}
