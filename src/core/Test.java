package core;

import processing.core.PApplet;

public class Test extends PApplet implements SoniaConstants
{
  SoniaUI ui;

  public void setup()
  {

    size(1280, 768);
  }

  public void draw()
  {
    background(0);
    ui.draw();
  }

  public static void main(String[] args)
  {
    PApplet.main(new String[] { Test.class.getName().toString() });
  }

}
