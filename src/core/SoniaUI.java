package core;

import java.awt.event.*;

import mkv.MyGUI.MyGUI;
import processing.core.PApplet;
import processing.core.PFont;

public class SoniaUI implements SoniaConstants, ActionListener
{
  public static MyGUI gui;
  public static PFont font;
  
  boolean shiftDown;
  PApplet p;
  IOControls ioControls;

  public SoniaUI(PApplet p)
  {
    this.p = p;

    font = p.loadFont(FONT);
    p.textFont(font, 32);

    gui = new MyGUI(p, 260);
    gui.getStyle().setFont(font, 10);
    gui.getStyle().buttonFace = p.color(0);
    gui.getStyle().buttonShadow = p.color(20);
    gui.getStyle().tintColor(p.color(255)); // tint color
    gui.getStyle().buttonText = p.color(255); // text color
    gui.getStyle().face = p.color(255); // slider color
    // MyGUIButton.SHOW_BUTTON_ARROW = true;
    
    ioControls = new IOControls(p, gui, 100, 100);
  }

  public void draw()
  {
    ioControls.draw();
  }
  
  public void keyReleased(char key, int keyCode)
  {
    if (keyCode == 16) {
      shiftDown = false;
    }
  }

  public void keyPressed(char key, int keyCode)
  {
    if (keyCode == 16)
    {
      shiftDown = true;
    }
  }

  public void mousePressed()
  {
  }

  public void mouseDragged()
  {
  }

  public void mouseReleased()
  {
  }

  public void mouseMoved()
  {
  }

  public boolean shiftDown()
  {
    return shiftDown;
  }

  public static boolean rightMouseButton(MouseEvent me)
  {
    return (me.getModifiers() == 4 || (me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK);
  }

  // INPUT, SLIDERS, CHECK_BOXES ================
  public void actionPerformed(ActionEvent e)
  {
    ioControls.onActionPerformed(e);
  }

}// end
