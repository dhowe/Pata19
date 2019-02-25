package core;

import javax.swing.SwingUtilities;

import mkv.MyGUI.MyGUILabel;
import mkv.MyGUI.MyGUIPinSlider;
import processing.core.PApplet;
import processing.core.PConstants;

public class Pata19 extends PApplet
{
  SoniaUI uiMan;

  public void setup()
  {
    size(1280, 768);
    uiMan = new SoniaUI(this);
  }

  public void draw()
  {
    background(0);
    uiMan.draw();
  }

  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new SoniaFrame(new Pata19(), 1280, 768, 0, 1);
      }
    });
  }
 
}
