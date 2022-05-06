package ca.ulaval.glo2004.patio;

import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class DebugWoodPiece extends Component {


    public DebugWoodPiece(Dimensions woodPieceDimensions, Vector3 centralPosition, ComponentType _componentType) {

        WoodPiece woodPiece = new WoodPiece(woodPieceDimensions, centralPosition, _componentType);

        woodPieces.add(woodPiece);
    }
}