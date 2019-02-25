package core;

import java.awt.event.ActionEvent;

import com.softsynth.jsyn.Synth;

import mkv.MyGUI.*;
import pitaru.sonia.LiveInput;
import processing.core.PApplet;
import processing.core.PConstants;

public class IOControls
{
  public MyGUIPinSlider inputGain, outputGain;

  private MyGUILabel inputGainLabel, outputGainLabel;
  
  private float masterInputGain = .5f, masterOutputGain = .5f;

  private PApplet p;
  private int x, y;//w, h;
  private int timestamp = 0;
  private String cpu = "";

  public IOControls(PApplet p, MyGUI gui, int x, int y)//, int w, int h)
  {
    this.p = p;
    this.x = x;
    this.y = y;

    int sx = x + 390, sy = y -5, sw = 140, sh = 12; 
    inputGainLabel = new MyGUILabel(p, "LEVEL", sx, sy);
    inputGain = new GUIPinSlider(p, inputGainLabel._x + 110, inputGainLabel._y, sw, sh, 0, 100);
    inputGain.setValue(Math.round(masterInputGain * 100));
    gui.add(inputGain);
    gui.add(inputGainLabel);

    outputGainLabel = new MyGUILabel(p, "GAIN", sx + 210, sy);
    outputGain = new GUIPinSlider(p, outputGainLabel._x + 110, sy, sw, sh, 0, 100);
    outputGain.setValue(Math.round(masterOutputGain * 100));
    gui.add(outputGain);
    gui.add(outputGainLabel);
  }

  public void draw()
  {
    p.rectMode(PConstants.CORNER);

    p.noFill();
    p.strokeWeight(2);
    p.stroke(255, 63);

    p.rect(x, y - 24, 800, 38);

    if (p.millis() - timestamp > 500) { // every .5 sec

      try {
        cpu = "CPU: " + getCpuPercentage();
      } catch (Exception e) {
        cpu = "CPU: 1%";
      }
      timestamp = p.millis();
    }

    p.textSize(12);
    
    p.fill(255);
    p.text(cpu, x + 8, y);
    
    p.line(x + 69, y - 22, x + 69, y + 11);
    
    p.fill(255);
    p.text("IN:", x + 83, y);

    drawMeter(x + 112, y - 14, 290, 20);


    p.strokeWeight(1);
  }

  private String getCpuPercentage()
  {
    double usage = Synth.getUsage();
    int percent = (int) (usage * 100.0);
    return Integer.toString(percent) + "%";
  }

  public void drawMeter(int mx, int my, int mw, int mh)
  {
    float level = LiveInput.getLevel() * masterInputGain;

    int numBars = 50;
    int unit = Math.round(mw / numBars);

    p.stroke(0);
    p.strokeWeight(2);

    for (int i = 0; i < numBars; i++) {
      int col = level > (i / (float) numBars) ? 255 : 100;
      p.fill(0, col, 0);
      if (i > numBars * .72) {
        p.fill(col, col, 0);
      }
      if (i > numBars * .86) {
        p.fill(col, 0, 0);
      }
      p.rect(mx + i * unit, my, unit, mh);
    }
  }

  public void onActionPerformed(ActionEvent e)
  {
    Object source = e.getSource();
    if (source == inputGain) inputGain(inputGain.getValue() / 100f);
    if (source == outputGain) outputGain(outputGain.getValue() / 100f);
  }

  public float outputGain()
  {
    return masterOutputGain;
  }

  public float inputGain()
  {
    return masterInputGain;
  }

  public void outputGain(float level)
  {
    masterOutputGain = level;
    outputGain.setValue((int) (100 * level));
  }

  public void inputGain(float level)
  {
    masterInputGain = level;
    inputGain.setValue((int) (100 * level));
  }
}
