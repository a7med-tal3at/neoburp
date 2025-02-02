package burp.n0ptex.neoburp.components;

public class VimState {
  public enum Mode {
    NORMAL,
    INSERT,
    VISUAL
  }

  private Mode currentMode;
  private StringBuilder commandBuffer;

  public VimState() {
    currentMode = Mode.NORMAL;
    commandBuffer = new StringBuilder();
  }

  public Mode getCurrentMode() {
    return currentMode;
  }

  public void setMode(Mode mode) {
    currentMode = mode;
    commandBuffer.setLength(0);
  }

  public void appendCommand(char c) {
    commandBuffer.append(c);
  }

  public String getCommandBuffer() {
    return commandBuffer.toString();
  }

  public void clearCommandBuffer() {
    commandBuffer.setLength(0);
  }
}