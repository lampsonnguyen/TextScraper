package com;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JCheckBox;


public class GUI extends JFrame
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public GUI(int keyCode)
  {
    JButton button = new JButton("button");
    addMnemonicToButton(button,keyCode);
    
    JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
    	groupLayout.createParallelGroup(Alignment.TRAILING)
    		.addGroup(groupLayout.createSequentialGroup()
    			.addContainerGap(109, Short.MAX_VALUE)
    			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    				.addGroup(groupLayout.createSequentialGroup()
    					.addComponent(chckbxNewCheckBox)
    					.addGap(35))
    				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
    					.addComponent(button, GroupLayout.PREFERRED_SIZE, 348, GroupLayout.PREFERRED_SIZE)
    					.addContainerGap())))
    );
    groupLayout.setVerticalGroup(
    	groupLayout.createParallelGroup(Alignment.LEADING)
    		.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
    			.addContainerGap(120, Short.MAX_VALUE)
    			.addComponent(button)
    			.addGap(55)
    			.addComponent(chckbxNewCheckBox)
    			.addContainerGap())
    );
    getContentPane().setLayout(groupLayout);
    button.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println(e.getActionCommand());
      }
    });

    pack();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args) throws Exception
  {
    new GUI(KeyEvent.VK_A);
  }

  void addMnemonicToButton(JButton button,int keyCode)
  {
    int shiftMask = InputEvent.SHIFT_DOWN_MASK;

    // signature: getKeyStroke(int keyCode, int modifiers, boolean onKeyRelease)
    KeyStroke keyPress = KeyStroke.getKeyStroke(keyCode,shiftMask,false);
    KeyStroke keyReleaseWithShift = KeyStroke.getKeyStroke(keyCode,shiftMask,true);

    // get maps for key bindings
    InputMap inputMap = button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = button.getActionMap();

    // add key bindings for pressing and releasing the button
    inputMap.put(keyPress,"press"+keyCode);
    actionMap.put("press"+keyCode, new ButtonPress(button));

    inputMap.put(keyReleaseWithShift,"releaseWithShift"+keyCode);
    actionMap.put("releaseWithShift"+keyCode, new ButtonRelease(button));

    ///*
    // add key binding for releasing SHIFT before A
    // if you use more than one modifier it gets really messy
    KeyStroke keyReleaseAfterShift = KeyStroke.getKeyStroke(keyCode,0,true);
    inputMap.put(keyReleaseAfterShift,"releaseAfterShift"+keyCode);
    actionMap.put("releaseAfterShift"+keyCode, new ButtonRelease(button));
    //*/
  }

  class ButtonPress extends AbstractAction
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton button;
    private ButtonModel model;
    ButtonPress(JButton button)
    {
      this.button = button;
      this.model = button.getModel();
    }

    public void actionPerformed(ActionEvent e)
    {
      // visually press the button
      model.setPressed(true);
      model.setArmed(true);

      button.requestFocusInWindow();
    }
  }

  class ButtonRelease extends AbstractAction
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ButtonModel model;
    ButtonRelease(JButton button)
    {
      this.model = button.getModel();
    }

    public void actionPerformed(ActionEvent e)
    {
      if (model.isPressed()) {
        // visually release the button
        // setPressed(false) also makes the button fire an ActionEvent
        model.setPressed(false);
        model.setArmed(false);
      }
    }
  }
}