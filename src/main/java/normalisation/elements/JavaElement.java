package normalisation.elements;

import java.io.Serializable;

public interface JavaElement extends Serializable {

    String toString();

    String originalString();

    int length();


}
