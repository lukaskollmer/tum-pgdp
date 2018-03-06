package korrektur;

public class Korrekturschema {
  public static int punkte(int aufgabe, String antwort) {
    switch (aufgabe) {
      case 1:
        if (antwort.equals("b")) return 6;
        else if (antwort.equals("d")) return 4;
        else return 0;
      case 2:
        if (antwort.equals("c")) return 5;
        else if (antwort.equals("b")) return 2;
        else return 0;
      case 3:
        if (antwort.equals("a")) return 6;
        else if (antwort.equals("d")) return 2;
        else return 0;
      case 4:
        if (antwort.equals("a")) return 2;
        else return 0;
      case 5:
        if (antwort.equals("b")) return 4;
        else if (antwort.equals("c")) return 3;
        else return 0;
      case 6:
        if (antwort.equals("d")) return 7;
        else if (antwort.equals("c")) return 5;
        else if (antwort.equals("a")) return 3;
        else return 0;
      case 7:
        if (antwort.equals("d")) return 6;
        else if (antwort.equals("a")) return 3;
        else return 0;
      case 8:
        if (antwort.equals("c")) return 4;
        else if (antwort.equals("a")) return 1;
        else return 0;
      default: return 0;
    }
  }

  public static float note(int punkte) {
    if (punkte >= 38)
      return 1.0f;
    else if (punkte > 36)
      return 1.3f;
    else if (punkte > 33)
      return 1.7f;
    else if (punkte > 30)
      return 2.0f;
    else if (punkte > 27)
      return 2.3f;
    else if (punkte > 24)
      return 2.7f;
    else if (punkte > 22)
      return 3.0f;
    else if (punkte > 20)
      return 3.3f;
    else if (punkte > 18)
      return 3.7f;
    else if (punkte > 16)
      return 4.0f;
    else if (punkte > 12)
      return 4.3f;
    else if (punkte > 8)
      return 4.7f;
    return 5.0f;
  }
}
