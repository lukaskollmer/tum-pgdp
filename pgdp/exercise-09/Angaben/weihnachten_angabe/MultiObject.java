import java.util.LinkedList;
import java.util.List;

public abstract class MultiObject extends Weihnachtsobjekt {

  protected List<SingleObject> parts;
  protected int breite;

  public MultiObject(int x, int y, int breite) {
    super(x, y);
    this.breite = breite;
    this.parts = new LinkedList<>();
  }

  @Override
  public String toString() {
    return "MultiObject{" +
        "parts=" + parts +
        ", breite=" + breite +
        '}';
  }
}
